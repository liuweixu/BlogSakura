package org.example.blogsakura.model.dto.space;

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
 * 空间 实体类。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("space")
public class Space implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 空间名称
     */
    @Column("spaceName")
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    @Column("spaceLevel")
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    @Column("maxSize")
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    @Column("maxCount")
    private Long maxCount;

    /**
     * 当前空间下图片的总大小
     */
    @Column("totalSize")
    private Long totalSize;

    /**
     * 当前空间下的图片数量
     */
    @Column("totalCount")
    private Long totalCount;

    /**
     * 创建用户 id
     */
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

    /**
     * 空间类型 0:私有 1:团队
     */
    @Column("spaceType")
    private Integer spaceType;

}
