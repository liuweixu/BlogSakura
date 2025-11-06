package org.example.blogsakura2.service;


import org.example.blogsakura2.pojo.Article;
import org.example.blogsakura2.pojo.ArticleInsert;
import org.example.blogsakura2.pojo.ArticleQuery;
import org.example.blogsakura2.pojo.ArticleUpdate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {
    /**
     * 获取文章列表
     *
     * @param articleQuery
     * @return
     */
    List<Article> getArticleList(ArticleQuery articleQuery);

    /**
     * 前端部分，获取文章列表
     *
     * @return
     */
    List<Article> getHomeArticleList();

    /**
     * 按照文章id删除文章
     *
     * @param id
     */
    void deleteArticleById(String id);

    /**
     * 按照id获取文章
     *
     * @param id
     * @return
     */
    Article getArticleById(String id);

    /**
     * 插入文章
     *
     * @param articleInsert
     * @param id
     */
    void insertArticle(ArticleInsert articleInsert, String id);

    /**
     * 更新文章
     *
     * @param articleUpdate
     * @param id
     */
    void updateArticle(ArticleUpdate articleUpdate, String id);

    /**
     * 将上传的图片处理，作为一个File类上传到COS中
     *
     * @param file
     * @return
     */
    public String uploadImageToCOS(MultipartFile file);
}
