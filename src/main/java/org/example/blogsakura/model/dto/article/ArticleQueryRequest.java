package org.example.blogsakura.model.dto.article;

import lombok.Data;
import org.example.blogsakura.common.common.PageRequest;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ArticleQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 频道名称
     */
    private String channel;

    /**
     * 图像上传类型
     */
    private Integer imageType;

    /**
     * 图像链接
     */
    private String imageUrl;

    /**
     * 发布日期
     */
    private LocalDateTime publishDate;

    /**
     * 更新日期
     */
    private LocalDateTime editDate;

    /**
     * 阅读数
     */
    private Long view;

    /**
     * 点赞数
     */
    private Long like;


}
