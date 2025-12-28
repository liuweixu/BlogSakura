package org.example.blogsakura.infrastruct.configuration;

import org.example.blogsakura.infrastruct.constants.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 此处程序的作用，项目启动时，这些配置类会被导入，创建交换器、队列，并且进行绑定设置。
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitMQConstants.ARTICLE_EXCHANGE, true, false);
    }

    /**
     * 创建队列，MySQL与ES之间的异步消息传递
     *
     * @return
     */
    @Bean
    public Queue insertQueue() {
        return new Queue(RabbitMQConstants.ARTICLE_INSERT_QUEUE, true);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(RabbitMQConstants.ARTICLE_DELETE_QUEUE, true);
    }


    /**
     * 绑定队列到交换机，MySQL与ES之间的异步传递
     *
     * @return
     */
    @Bean
    public Binding insertQueueBinding() {
        return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(RabbitMQConstants.ARTICLE_INSERT_KEY);
    }

    @Bean
    public Binding deleteQueueBinding() {
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(RabbitMQConstants.ARTICLE_DELETE_KEY);
    }

    /**
     * MySQL与Redis之间的异步传递
     */
    @Bean
    public TopicExchange topicViewExchange() {
        return new TopicExchange(RabbitMQConstants.VIEW_EXCHANGE, true, false);
    }

    @Bean
    public Queue updateViewQueue() {
        return new Queue(RabbitMQConstants.VIEW_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding updateViewQueueBinding() {
        return BindingBuilder.bind(updateViewQueue()).to(topicViewExchange()).with(RabbitMQConstants.VIEW_UPDATE_KEY);
    }
}
