package org.example.blogsakura.configure;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
class MyRedissonConfig {

    @Bean
    public RedissonClient redissonClient() throws IOException {
        // 1. 新建配置
        Config config = new Config();
        // 2. 创建单节点Redisson配置
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }
}
