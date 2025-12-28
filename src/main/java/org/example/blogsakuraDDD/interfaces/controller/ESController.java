package org.example.blogsakuraDDD.interfaces.controller;

import jakarta.annotation.Resource;
import org.example.blogsakuraDDD.infrastruct.common.BaseResponse;
import org.example.blogsakuraDDD.infrastruct.common.ResultUtils;
import org.example.blogsakuraDDD.interfaces.vo.blog.article.ArticleVO;
import org.example.blogsakuraDDD.application.service.ESApplicationService;
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
