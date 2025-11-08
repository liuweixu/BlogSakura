package org.example.blogsakura.controller.frontend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.constants.RabbitMQConstants;
import org.example.blogsakura.service.ViewService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ViewController {

    private final ViewService viewService;

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/article/views/{id}")
    public BaseResponse<Long> updateViews(@PathVariable Long id) {
        log.info("更新文章阅读数：{}", id);
        Long result = viewService.updateViews(id);
        rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE, RabbitMQConstants.ARTICLE_INSERT_KEY, id);
        return ResultUtils.success(result);
    }
}
