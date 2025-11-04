package org.example.blogsakura.configure;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.constants.RabbitMQConstants;
import org.example.blogsakura.mapper.ArticleMapper;
import org.example.blogsakura.service.ESService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
public class RabbitMQListener {
    @Resource
    private ESService esService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ArticleMapper articleMapper;

    /**
     * 监听文章新增或修改的业务
     *
     * @param id
     */
    @RabbitListener(queues = RabbitMQConstants.ARTICLE_INSERT_QUEUE)
    public void listenArticleInsertOrUpdate(String id) {
        esService.insertArticleDocById(id);
    }

    /**
     * 监听文章删除的业务
     *
     * @param id
     */
    @RabbitListener(queues = RabbitMQConstants.ARTICLE_DELETE_QUEUE)
    public void listenArticleDelete(String id) {
        esService.deleteArticleDocById(id);
    }

    /**
     *
     * 监听Redis的更新业务
     * 引入事务机制
     */
    @RabbitListener(queues = RabbitMQConstants.VIEW_UPDATE_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void listenViewUpdate(String id) {
        try {
            String value = stringRedisTemplate.opsForValue().get(id);
            if (value == null) return;

            Long count = Long.valueOf(value);
            articleMapper.updateViewById(id, count);
        } catch (Exception e) {
            log.error("Failed to update view for {}", id, e);
        }
    }
}
