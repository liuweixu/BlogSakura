package org.example.blogsakura.interfaces.controller;

import jakarta.annotation.Resource;
import org.example.blogsakura.infrastruct.common.BaseResponse;
import org.example.blogsakura.infrastruct.common.ResultUtils;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.example.blogsakura.application.service.ESApplicationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ESController {
    @Resource
    private ESApplicationService esApplicationService;


//    @PostMapping("/search/title")
//    public BaseResponse<List<ArticleVO>> searchArticleByTitle(@RequestBody Map<String, Object> data) {
//        return ResultUtils.success(esApplicationService.searchArticleVOByTitle(data.get("keyword").toString()));
//    }

    @PostMapping("/search")
    public BaseResponse<List<ArticleVO>> searchArticleByTitleOrContent(@RequestBody Map<String, Object> data) {
        return ResultUtils.success(esApplicationService.searchArticleOrContentVOByTitle(data.get("keyword").toString()));
    }
}
