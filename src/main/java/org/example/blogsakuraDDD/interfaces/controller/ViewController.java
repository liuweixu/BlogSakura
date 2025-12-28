package org.example.blogsakuraDDD.interfaces.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakuraDDD.infrastruct.common.BaseResponse;
import org.example.blogsakuraDDD.infrastruct.common.ResultUtils;
import org.example.blogsakuraDDD.infrastruct.constants.RabbitMQConstants;
import org.example.blogsakuraDDD.application.service.ViewApplicationService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ViewController {

    private final ViewApplicationService viewApplicationService;

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/article/views/{id}")
    public BaseResponse<Long> updateViews(@PathVariable Long id) {
        log.info("更新文章阅读数：{}", id);
        Long result = viewApplicationService.updateViews(id);
        rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE, RabbitMQConstants.ARTICLE_INSERT_KEY, id);
        return ResultUtils.success(result);
    }
}
