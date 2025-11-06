package org.example.blogsakura2.controller;

import jakarta.annotation.Resource;
import org.example.blogsakura2.pojo.Result;
import org.example.blogsakura2.service.ESService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@RestController
public class ESController {
    @Resource
    private ESService esService;

    //    @PostMapping("/search/title")
    public Result findArticleByTitle(@RequestBody Map<String, Object> data) {
        return Result.success(esService.findArticleByTitle(data.get("keyword").toString()));
    }

    //    @PostMapping("/search")
    public Result findArticleByTitleOrContent(@RequestBody Map<String, Object> data) {
        return Result.success(esService.findArticleByTitleOrContent(data.get("keyword").toString()));
    }
}
