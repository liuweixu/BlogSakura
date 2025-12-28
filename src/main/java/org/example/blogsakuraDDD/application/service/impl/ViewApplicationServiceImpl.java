package org.example.blogsakuraDDD.application.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakuraDDD.domain.blog.view.service.ViewDomainService;
import org.example.blogsakuraDDD.infrastruct.constants.RabbitMQConstants;
import org.example.blogsakuraDDD.infrastruct.manager.BloomFilter.ArticleBloomFilter;
import org.example.blogsakuraDDD.infrastruct.mapper.ArticleMapper;
import org.example.blogsakuraDDD.application.service.ViewApplicationService;
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
public class ViewApplicationServiceImpl implements ViewApplicationService {

    @Resource
    private ViewDomainService viewDomainService;

    /**
     * 更新点赞数:
     * 修改方案：先查询Redis，Redis不存在才查询数据库
     *
     * @param id
     */
    @Override
    public Long updateViews(Long id) {
        return viewDomainService.updateViews(id);
    }
}
