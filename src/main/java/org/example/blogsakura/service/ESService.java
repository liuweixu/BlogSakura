package org.example.blogsakura.service;

import org.example.blogsakura.pojo.Article;

import java.util.List;

public interface ESService {
    public void insertArticleDocById(String id);

    public void deleteArticleDocById(String id);

    // 新增初始化操作
    public void initDataToES();

    public List<Article> findArticleByTitle(String title);

    public List<Article> findArticleByTitleOrContent(String keyword);
}
