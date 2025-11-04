package org.example.blogsakura.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.aop.Log;
import org.example.blogsakura.constants.RabbitMQConstants;
import org.example.blogsakura.pojo.*;
import org.example.blogsakura.service.ArticleService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@RestController
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PutMapping("/backend/articlelist")
    public Result getArticleList(@RequestBody ArticleQuery articleQuery) {
        log.info("查询所有数据");
        return Result.success(articleService.getArticleList(articleQuery));
    }

    @DeleteMapping("/backend/article/{id}")
    @Log
    public Result deleteArticleById(@PathVariable String id) {
        log.info("删除id的文章");
        // 发送删除文章的消息
        rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE, RabbitMQConstants.ARTICLE_DELETE_KEY, id);
        articleService.deleteArticleById(id);
        return Result.success();
    }

    @GetMapping("/backend/article/{id}")
    @Log
    public Result getArticleById(@PathVariable String id) {
        log.info("筛选id的文章");
        return Result.success(articleService.getArticleById(id));
    }

    @PostMapping("/backend/article")
    @Log
    public Result insertArticle(@RequestBody ArticleInsert articleInsert) {
        log.info("插入文章");
        String id = String.valueOf(IdWorker.getId()); //添加雪花算法的id
        articleService.insertArticle(articleInsert, id);
        // 发送新增文章的消息
        rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE, RabbitMQConstants.ARTICLE_INSERT_KEY, id);
        return Result.success();
    }

    @PutMapping("/backend/article/{id}")
    @Log
    public Result UpdateArticle(@RequestBody ArticleUpdate articleUpdate, @PathVariable String id) {
        log.info("修改文章");
        articleService.updateArticle(articleUpdate, id);
        // 发送修改文章的消息
        rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE, RabbitMQConstants.ARTICLE_INSERT_KEY, id);
        return Result.success();
    }

    @GetMapping("/article/{id}")
    public Result getHomeArticleById(@PathVariable String id) {
        log.info("筛选id的文章 前端");
        return Result.success(articleService.getArticleById(id));
    }

    @GetMapping("/home")
    public Result getHomeArticleList() {
        log.info("获取前端首页的所有文章列表信息");
        return Result.success(articleService.getHomeArticleList());
    }

    @PostMapping("/backend/upload/image")
    public Result getUploadFile(@RequestPart("image") MultipartFile file) {
        // 获取文件名
        return Result.success(articleService.uploadImageToCOS(file));
    }
}

