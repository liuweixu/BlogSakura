package org.example.blogsakura.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.blogsakura.application.service.RankingApplicationService;
import org.example.blogsakura.domain.blog.ranking.service.RankingListService;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingApplicationServiceImpl implements RankingApplicationService {

    private final RankingListService rankingListService;

    @Override
    public List<ArticleVO> getRankingList() {
        return rankingListService.getRankingList();
    }

    @Override
    public void increaseRankingScore(Long articleId) {
        rankingListService.increaseRankingScore(articleId);
    }
}
