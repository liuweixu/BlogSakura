package org.example.blogsakura.application.service;

import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;

import java.util.List;

/**
 * ES搜索处理层
 */
public interface ESApplicationService {

    /**
     * 添加id的文章到ES中
     *
     * @param id
     */
    void addArticleVOToESById(Long id);


    /**
     * 在ES中删除id的文章
     *
     * @param id
     */
    void deleteArticleVOFromESById(Long id);


    /**
     * 按照title查询
     *
     * @param title
     * @return
     */
    List<ArticleVO> searchArticleVOByTitle(String title);

    /**
     * 按照title或Content查询
     *
     * @param title
     * @return
     */
    List<ArticleVO> searchArticleOrContentVOByTitle(String title);

    /**
     * 初始化数据库数据到ES
     *
     */
    void initDataToES();
}
