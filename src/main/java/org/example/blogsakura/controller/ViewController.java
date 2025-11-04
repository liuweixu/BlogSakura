package org.example.blogsakura.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.constants.RabbitMQConstants;
import org.example.blogsakura.pojo.ViewRequest;
import org.example.blogsakura.service.ViewService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ViewController {

    @Resource
    private ViewService viewService;

    @Resource
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/article/views/{id}")
    public Long getViews(@PathVariable String id) {
        log.info("读取文章阅读数：{}", id);
        return viewService.getViews(id);
    }

    @PutMapping("/article/views/{id}")
    public void updateViews(@PathVariable String id, @RequestBody ViewRequest viewRequest) {
        log.info("更新文章阅读数：{}", id);
        viewService.updateViews(id, viewRequest.getView());
        rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE, RabbitMQConstants.ARTICLE_INSERT_KEY, id);
    }
}
