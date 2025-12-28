package org.example.blogsakuraDDD.infrastruct.constants;

public class RabbitMQConstants {
    /**
     * 交换机 使用static和final设置常量
     */
    public static final String ARTICLE_EXCHANGE = "article.topic";
    /**
     * MySQL与ES
     */
    /**
     * 监听新增或修改的队列
     */
    public static final String ARTICLE_INSERT_QUEUE = "article.insert.queue";
    /**
     * 监听删除的队列
     */
    public static final String ARTICLE_DELETE_QUEUE = "article.delete.queue";
    /**
     * 新增或修改的RoutingKey
     */
    public static final String ARTICLE_INSERT_KEY = "article.insert.key";
    /**
     * 删除的RoutingKey
     */
    public static final String ARTICLE_DELETE_KEY = "article.delete.key";

    /**
     * MySQL与Redis
     */
    public static final String VIEW_EXCHANGE = "view.topic.exchange";
    public static final String VIEW_UPDATE_QUEUE = "view.update.queue";
    public static final String VIEW_UPDATE_KEY = "view.update.key";
}
