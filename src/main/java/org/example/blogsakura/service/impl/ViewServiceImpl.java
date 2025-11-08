package org.example.blogsakura.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.constants.RabbitMQConstants;
import org.example.blogsakura.manager.ArticleBloomFilter;
import org.example.blogsakura.mapper.ArticleMapper;
import org.example.blogsakura.service.ViewService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class ViewServiceImpl implements ViewService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private ArticleBloomFilter articleBloomFilter;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    // Redis的Lua脚本
    private final DefaultRedisScript<Long> incrementScript;

    public ViewServiceImpl() {
        incrementScript = new DefaultRedisScript<>();
        incrementScript.setResultType(Long.class);
        incrementScript.setScriptText("""
                local key = KEYS[1]
                local defaultValue = tonumber(ARGV[1])
                local expireSeconds = tonumber(ARGV[2])
                
                local value = redis.call("GET", key)
                -- Redis不存在，设置初始值为defaultValue，并设置过期时间ttl
                local num = defaultValue + 1
                redis.call("SET", key, num, "EX", expireSeconds)
                return num
                """);
    }

    /**
     * 更新点赞数:
     * 修改方案：先查询Redis，Redis不存在才查询数据库
     *
     * @param id
     */
    @Override
    public Long updateViews(Long id) {
        // 布隆过滤器判断文章是否不存在
        if (!articleBloomFilter.mightExist(id)) {
            log.warn("文章id {} 不存在", id);
            return null;
        }

        String key = String.valueOf(id);
        // 先检查Redis是否存在
        String view = stringRedisTemplate.opsForValue().get(key);
        Long defaultValue = null; // 如果Redis存在就默认为null，否则更新从数据库获取的值
        if (view == null || view.isBlank()) {
            RLock lock = redissonClient.getLock("lock:view:" + key);
            lock.lock();
            try {
                // 双重检查
                view = stringRedisTemplate.opsForValue().get(key);
                if (view == null || view.isBlank()) {
                    // 从数据库获取数值
                    defaultValue = articleMapper.getViewById(id);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        Long result = 0L;
        if (defaultValue != null) {
            // Redis不存在 使用数据库值初始化
            result = stringRedisTemplate.execute(
                    incrementScript,
                    Collections.singletonList(String.valueOf(id)),
                    String.valueOf(defaultValue),
                    "360000"
            );
        } else {
            // Redis存在，可以直接使用increment更新
            result = stringRedisTemplate.opsForValue().increment(key);
        }
        rabbitTemplate.convertAndSend(RabbitMQConstants.VIEW_EXCHANGE, RabbitMQConstants.VIEW_UPDATE_KEY, id);
        return result;
    }
}
