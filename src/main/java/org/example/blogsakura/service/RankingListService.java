package org.example.blogsakura.service;

import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.vo.article.ArticleVO;

import java.util.List;

public interface RankingListService {

    /**
     * 获取排行榜
     *
     * @return
     */
    List<ArticleVO> getRankingList();

    
}
