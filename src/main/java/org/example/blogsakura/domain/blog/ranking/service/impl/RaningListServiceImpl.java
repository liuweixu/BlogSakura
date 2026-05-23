package org.example.blogsakura.domain.blog.ranking.service.impl;

import jakarta.annotation.Resource;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.domain.blog.article.entity.Article;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.example.blogsakura.application.service.ArticleApplicationService;
import org.example.blogsakura.domain.blog.ranking.service.RankingListService;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class RaningListServiceImpl implements RankingListService {

    private static final String RANKING_KEY = "article:ranking";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ArticleApplicationService articleService;

    /**
     * 获取排行榜
     *
     * @return
     */
    @Override
    public List<ArticleVO> getRankingList() {
        Set<ZSetOperations.TypedTuple<String>> rankingTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(RANKING_KEY, 0, 9);
        if (rankingTuples == null || rankingTuples.isEmpty()) {
            return Collections.emptyList();
        }
        return convertToRankList(rankingTuples);
    }

    @Override
    public void increaseRankingScore(Long articleId) {
        if (articleId == null) {
            return;
        }
        stringRedisTemplate.opsForZSet().incrementScore(RANKING_KEY, String.valueOf(articleId), 1D);
    }

    /**
     * 按 Redis 排行榜分数从高到低组装文章列表
     */
    private List<ArticleVO> convertToRankList(Set<ZSetOperations.TypedTuple<String>> rankingTuples) {
        ThrowUtils.throwIf(rankingTuples == null || rankingTuples.isEmpty(), ErrorCode.PARAMS_ERROR);
        List<ArticleVO> articleVOList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : rankingTuples) {
            String articleId = tuple.getValue();
            if (articleId == null) {
                continue;
            }
            Article article = articleService.getById(articleId);
            if (article == null) {
                continue;
            }
            ArticleVO articleVO = articleService.getArticleVO(article);
            Double score = tuple.getScore();
            if (score != null) {
                articleVO.setView(score.longValue());
            }
            articleVOList.add(articleVO);
        }
        return articleVOList;
    }
}
