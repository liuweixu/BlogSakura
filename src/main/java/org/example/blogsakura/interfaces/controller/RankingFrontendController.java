package org.example.blogsakura.interfaces.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogsakura.application.service.RankingApplicationService;
import org.example.blogsakura.infrastruct.common.BaseResponse;
import org.example.blogsakura.infrastruct.common.ResultUtils;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/article/ranking")
@RequiredArgsConstructor
public class RankingFrontendController {

    private final RankingApplicationService rankingApplicationService;

    @GetMapping("/list")
    public BaseResponse<List<ArticleVO>> getFrontendRankingList() {
        return ResultUtils.success(rankingApplicationService.getRankingList());
    }
}
