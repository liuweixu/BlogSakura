package org.example.blogsakura.model.dto.picture;

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
 * 图片管理 实体类。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("picture")
public class Picture implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON 数组）
     */
    private String tags;

    /**
     * 图片体积
     */
    @Column("picSize")
    private Long picSize;

    /**
     * 图片宽度
     */
    @Column("picWidth")
    private Integer picWidth;

    /**
     * 图片高度
     */
    @Column("picHeight")
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    @Column("picScale")
    private Double picScale;

    /**
     * 图片格式
     */
    @Column("picFormat")
    private String picFormat;

    @Column("userId")
    private Long userId;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    @Column("editTime")
    private LocalDateTime editTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

}
