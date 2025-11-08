package org.example.blogsakura.controller.frontend;

import jakarta.annotation.Resource;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.model.vo.article.ArticleVO;
import org.example.blogsakura.service.ESService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ESController {
    @Resource
    private ESService esService;


//    @PostMapping("/search/title")
//    public BaseResponse<List<ArticleVO>> searchArticleByTitle(@RequestBody Map<String, Object> data) {
//        return ResultUtils.success(esService.searchArticleVOByTitle(data.get("keyword").toString()));
//    }

    @PostMapping("/search")
    public BaseResponse<List<ArticleVO>> searchArticleByTitleOrContent(@RequestBody Map<String, Object> data) {
        return ResultUtils.success(esService.searchArticleOrContentVOByTitle(data.get("keyword").toString()));
    }
}
