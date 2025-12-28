package org.example.blogsakura.domain.space.entity;

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
 * 空间用户关联 实体类。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("space_user")
public class SpaceUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 空间 id
     */
    @Column("spaceId")
    private Long spaceId;

    /**
     * 用户 id
     */
    @Column("userId")
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    @Column("spaceRole")
    private String spaceRole;

    /**
     * 创建时间
     */
    @Column(value = "createTime", onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(value = "updateTime", onInsertValue = "now()")
    private LocalDateTime updateTime;

}
