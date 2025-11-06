package org.example.blogsakura2.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura2.configure.ArticleBloomFilter;
import org.example.blogsakura.common.constants.RabbitMQConstants;
import org.example.blogsakura2.mapper.ArticleMapper;
import org.example.blogsakura2.service.ViewService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class ViewServiceImpl implements ViewService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ArticleMapper articleMapper;

    private final DefaultRedisScript<Long> incrementScript;

    @Resource
    private ArticleBloomFilter articleBloomFilter;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    public ViewServiceImpl() {
        incrementScript = new DefaultRedisScript<>();
        incrementScript.setResultType(Long.class);
        incrementScript.setScriptText(
                "local key = KEYS[1]\n" +
                        "local defaultValue = tonumber(ARGV[1])\n" +
                        "local expireSeconds = tonumber(ARGV[2])\n" +
                        "\n" +
                        "local value = redis.call(\"GET\", key)\n" +
                        "if not value then\n" +
                        "    redis.call(\"SET\", key, defaultValue, \"EX\", expireSeconds)\n" +
                        "    return defaultValue\n" +
                        "else\n" +
                        "    local num = tonumber(value)\n" +
                        "    num = num + 1\n" +
                        "    redis.call(\"SET\", key, num)\n" +
                        "    return num\n" +
                        "end"
        );
    }

    /**
     *
     */
    @Override
    public Long getViews(String id) {
        if (!articleBloomFilter.mightExist(id)) {
            log.warn("文章id {} 不存在", id);
            return null;
        }

//        String result = stringRedisTemplate.opsForValue().get(id);
//        if (result == null || result.isBlank() || result.equalsIgnoreCase("null")) {
//            Long view = articleMapper.getViewById(id);
//            // 对非热门文章设置过期时间
//            if (view > 100) {
//                stringRedisTemplate.opsForValue().set(id, String.valueOf(view));
//            } else {
//                stringRedisTemplate.opsForValue().set(id, String.valueOf(view), Duration.ofHours(10));
//            }
//            return view;
//        } else {
//            return Long.valueOf(result);
//        }
        /**
         * 加入分布式锁Redisson
         */
        String result = stringRedisTemplate.opsForValue().get(id);
        if (result != null && !result.isBlank() && !result.equalsIgnoreCase("null")) {
            return Long.valueOf(result);
        }

        RLock lock = redissonClient.getLock("lock:view:" + id);
        lock.lock(); // Redisson利用看门狗机制，实现自动续期，防止过期
        try {
            // 双重检查
            result = stringRedisTemplate.opsForValue().get(id);
            if (result != null && !result.isBlank() && !"null".equalsIgnoreCase(result)) {
                return Long.valueOf(result);
            }

            Long view = articleMapper.getViewById(id);
            if (view == null) view = 0L;
            stringRedisTemplate.opsForValue().set(id, String.valueOf(view));
            return view;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateViews(String id, Long view) {
        if (!articleBloomFilter.mightExist(id)) {
            log.warn("文章id {} 不存在", id);
            return;
        }
        Long result = stringRedisTemplate.execute(
                incrementScript,
                Collections.singletonList(id),
                String.valueOf(view),
                "360000"
        );
//        articleMapper.updateViewById(id, result);
        rabbitTemplate.convertAndSend(RabbitMQConstants.VIEW_EXCHANGE, RabbitMQConstants.VIEW_UPDATE_KEY, id);
    }

    @PostConstruct
    public void testLog() {
        log.error("======error=======");
    }

}
