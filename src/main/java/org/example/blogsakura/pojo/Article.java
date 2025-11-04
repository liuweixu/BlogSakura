package org.example.blogsakura.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private String id;
    private String title;
    private String content;
    private Long channelId;
    private Integer imageType;
    private String imageUrl;
    private String publishDate;
    private String editDate;
    private Long view;
}
