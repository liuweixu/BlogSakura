package org.example.blogsakura.model.vo.article;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章表 实体类。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("article")
public class ArticleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

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

    /**
     * 频道名称
     */
    private String channel;
}
