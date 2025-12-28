package org.example.blogsakuraDDD.domain.blog.operateLog.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

import java.io.Serial;

import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作日志表 实体类。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("operate_log")
public class OperateLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private BigInteger id;

    /**
     * 操作时间
     */
    @Column("operateTime")
    private LocalDateTime operateTime;

    /**
     * 操作名称
     */
    @Column("operateName")
    private String operateName;

    /**
     * 方法执行耗时，单位：ms
     */
    @Column("costTime")
    private Long costTime;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

}
