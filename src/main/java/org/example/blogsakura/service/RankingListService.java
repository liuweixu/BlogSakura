package org.example.blogsakura.service;

import org.example.blogsakuraDDD.interfaces.vo.blog.article.ArticleVO;

import java.util.List;

public interface RankingListService {

    /**
     * 获取排行榜
     *
     * @return
     */
    List<ArticleVO> getRankingList();


}
