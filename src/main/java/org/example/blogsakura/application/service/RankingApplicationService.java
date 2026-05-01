package org.example.blogsakura.application.service;

import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;

import java.util.List;

public interface RankingApplicationService {

    /**
     * 获取文章排行榜
     *
     * @return 文章列表
     */
    List<ArticleVO> getRankingList();

    /**
     * 增加文章排行榜分值
     *
     * @param articleId 文章 id
     */
    void increaseRankingScore(Long articleId);
}
