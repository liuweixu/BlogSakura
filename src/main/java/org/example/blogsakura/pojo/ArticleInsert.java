package org.example.blogsakura.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleInsert {
    private String title;
    private String content;
    private String channel;
    private Integer imageType;
    private String imageUrl;
}
