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
        Set<String> articleIds = stringRedisTemplate.opsForZSet().reverseRange(
                RANKING_KEY, 0, 9);
        if (articleIds == null || articleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return convertToRankList(articleIds);
    }

    @Override
    public void increaseRankingScore(Long articleId) {
        if (articleId == null) {
            return;
        }
        stringRedisTemplate.opsForZSet().incrementScore(RANKING_KEY, String.valueOf(articleId), 1D);
    }

    /**
     * 将集合id转为相应的文章列表
     *
     * @param articleIds
     * @return
     */
    private List<ArticleVO> convertToRankList(Set<String> articleIds) {
        ThrowUtils.throwIf(articleIds == null || articleIds.isEmpty(), ErrorCode.PARAMS_ERROR);
        List<ArticleVO> articleVOList = new ArrayList<>();
        articleIds.forEach(articleId -> {
            Article article = articleService.getById(articleId);
            ArticleVO articleVO = articleService.getArticleVO(article);
            articleVOList.add(articleVO);
        });
        return articleVOList;
    }
}
