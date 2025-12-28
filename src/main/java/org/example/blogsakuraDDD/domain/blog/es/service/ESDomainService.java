package org.example.blogsakuraDDD.domain.blog.es.service;

import org.example.blogsakuraDDD.interfaces.vo.blog.article.ArticleVO;

import java.util.List;

/**
 * ES搜索处理层
 */
public interface ESDomainService {

    /**
     * 添加文章到ES中
     *
     * @param id
     */
    void addArticleVOToESById(Long id, ArticleVO articleVO);


    /**
     * 在ES中删除id的文章
     *
     * @param id
     */
    void deleteArticleVOFromESById(Long id);

    /**
     * 初始化数据库数据到ES
     *
     */
    void initDataToES(List<ArticleVO> articleVOList);

}
