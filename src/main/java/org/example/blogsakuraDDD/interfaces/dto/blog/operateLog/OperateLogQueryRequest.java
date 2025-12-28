package org.example.blogsakuraDDD.interfaces.dto.blog.operateLog;

import lombok.Data;
import org.example.blogsakuraDDD.infrastruct.common.PageRequest;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class OperateLogQueryRequest extends PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private BigInteger id;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 操作名称
     */
    private String operateName;

    /**
     * 方法执行耗时，单位：ms
     */
    private Long costTime;
}
