package org.example.blogsakura.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDoc {
    private String id;
    private String title;
    private String content;
    private Long channelId;
    private Integer imageType;
    private String imageUrl;
    private String publishDate;
    private String editDate;
    private Long view;
    private List<String> suggestions;

    public ArticleDoc(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.channelId = article.getChannelId();
        this.imageType = article.getImageType();
        this.imageUrl = article.getImageUrl();
        this.publishDate = article.getPublishDate();
        this.editDate = article.getEditDate();
        this.view = article.getView();
        this.suggestions = Arrays.asList(this.title);
    }
}
