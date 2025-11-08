package org.example.blogsakura.model.dto.operateLog;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import org.example.blogsakura.common.common.PageRequest;

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
