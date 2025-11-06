package org.example.blogsakura.model.dto.channel;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import org.example.blogsakura.common.common.PageRequest;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChannelQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 频道名
     */
    private String channel;


    /**
     * 创建日期
     */
    private LocalDateTime createTime;

    /**
     * 修改日期
     */
    private LocalDateTime updateTime;
}
