package org.example.blogsakura.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.constants.RabbitMQConstants;
import org.example.blogsakura.manager.BloomFilter.ArticleBloomFilter;
import org.example.blogsakura.mapper.ArticleMapper;
import org.example.blogsakura.service.ViewService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    @Value("${redis-ttl.base-expire}")
    private int baseExpire;
    private int randomExpire = new Random().nextInt(60 * 60); // 额外随机0~60分钟

    // Redis的Lua脚本
    private final DefaultRedisScript<Long> incrementScript;

    public ViewServiceImpl() {
        incrementScript = new DefaultRedisScript<>();
        incrementScript.setResultType(Long.class);
        incrementScript.setScriptText("""
                local key = KEYS[1]
                local expireSeconds = tonumber(ARGV[1])
                
                if redis.call("TTL", key) == -1 then
                    -- 如果没有设置过期时间
                    redis.call("EXPIRE", key, expireSeconds)
                end
                local result = redis.call("INCR", key)
                return result
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
        // Redis中Key不存在时，要先从数据库获取并更新到Redis。
        if (view == null || view.isBlank()) {
            RLock lock = redissonClient.getLock("lock:view:" + key);
            lock.lock();
            try {
                // 双重检查
                view = stringRedisTemplate.opsForValue().get(key);
                if (view == null || view.isBlank()) {
                    // 从数据库获取数值
                    defaultValue = articleMapper.getViewById(id);
                    Long result = defaultValue + 1;
                    stringRedisTemplate.opsForValue().set(key, String.valueOf(result), baseExpire + randomExpire, TimeUnit.SECONDS);
                    rabbitTemplate.convertAndSend(RabbitMQConstants.VIEW_EXCHANGE, RabbitMQConstants.VIEW_UPDATE_KEY, id);
                    return result;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        // Redis存在，可以直接使用increment更新，注意判断是否有永久不过期的键
        Long result = stringRedisTemplate.execute(
                incrementScript,
                Collections.singletonList(key),
                String.valueOf(baseExpire + randomExpire)
        );
        rabbitTemplate.convertAndSend(RabbitMQConstants.VIEW_EXCHANGE, RabbitMQConstants.VIEW_UPDATE_KEY, id);
        return result;
    }
}
