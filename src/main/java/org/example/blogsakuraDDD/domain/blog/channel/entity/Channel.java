package org.example.blogsakuraDDD.domain.blog.channel.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;

import java.io.Serial;
import java.time.LocalDateTime;

import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 频道表 实体类。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("channel")
public class Channel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 频道名
     */
    private String channel;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

    /**
     * 创建日期
     */
    @Column(value = "createTime")
    private LocalDateTime createTime;

    /**
     * 修改日期
     */
    @Column(value = "updateTime")
    private LocalDateTime updateTime;

}
