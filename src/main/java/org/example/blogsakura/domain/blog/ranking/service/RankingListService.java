package org.example.blogsakura.domain.blog.ranking.service;

import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;

import java.util.List;

public interface RankingListService {

    /**
     * 获取排行榜
     *
     * @return
     */
    List<ArticleVO> getRankingList();


}
