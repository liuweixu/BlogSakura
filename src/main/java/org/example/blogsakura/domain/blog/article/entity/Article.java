package org.example.blogsakura.domain.blog.article.entity;

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
public class Article implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
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
     * 频道Id
     */
    @Column("channelId")
    private Long channelId;

    /**
     * 图像上传类型
     */
    @Column("imageType")
    private Integer imageType;

    /**
     * 图像链接
     */
    @Column("imageUrl")
    private String imageUrl;

    /**
     * 发布日期
     */
    @Column("publishDate")
    private LocalDateTime publishDate;

    /**
     * 更新日期
     */
    @Column("editDate")
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
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

}
