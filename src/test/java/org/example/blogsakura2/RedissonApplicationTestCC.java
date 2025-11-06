package org.example.blogsakura2;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class RedissonApplicationTestCC {
    @Resource
    private RedissonClient redissonClient;

    @Test
    void printTest() {
        System.out.println(redissonClient);
    }

    @Test
    void testLock() {
        RLock lock = redissonClient.getLock("test-lock");
        // 加锁
        lock.lock();
        try {
            System.out.println("加锁成功。ID:" + Thread.currentThread().threadId());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("释放成功。ID:" + Thread.currentThread().threadId());
        }
    }
}
