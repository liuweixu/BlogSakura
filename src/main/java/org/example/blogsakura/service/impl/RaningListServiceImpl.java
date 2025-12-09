package org.example.blogsakura.service.impl;

import jakarta.annotation.Resource;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.article.Article;
import org.example.blogsakura.model.vo.article.ArticleVO;
import org.example.blogsakura.service.ArticleService;
import org.example.blogsakura.service.RankingListService;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RaningListServiceImpl implements RankingListService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ArticleService articleService;

    /**
     * 获取排行榜
     *
     * @return
     */
    @Override
    public List<ArticleVO> getRankingList() {
        Set<String> articleIds = stringRedisTemplate.opsForZSet().reverseRangeByScore(
                "ranking", Double.MIN_VALUE, Double.MAX_VALUE, 0, 10
        );
        if (articleIds == null || articleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return convertToRankList(articleIds);
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
        for (String articleId : articleIds) {
            Article article = articleService.getById(articleId);
            ArticleVO articleVO = articleService.getArticleVO(article);
            articleVOList.add(articleVO);
        }
        return articleVOList;
    }
}
