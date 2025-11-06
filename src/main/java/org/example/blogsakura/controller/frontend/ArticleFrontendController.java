package org.example.blogsakura.controller.frontend;

import jakarta.annotation.Resource;
import org.example.blogsakura.service.ArticleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class ArticleFrontendController {
    @Resource
    private ArticleService articleService;


}
