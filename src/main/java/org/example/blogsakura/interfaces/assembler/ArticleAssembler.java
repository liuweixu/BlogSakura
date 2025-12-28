package org.example.blogsakura.interfaces.assembler;

import org.example.blogsakura.domain.blog.article.entity.Article;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.springframework.beans.BeanUtils;

public class ArticleAssembler {
    public static Article toArticleEntity(ArticleVO articleVO) {
        Article article = new Article();
        BeanUtils.copyProperties(articleVO, article);
        return article;
    }
}
