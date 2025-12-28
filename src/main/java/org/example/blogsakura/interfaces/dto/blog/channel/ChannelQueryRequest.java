package org.example.blogsakura.interfaces.dto.blog.channel;

import lombok.Data;
import org.example.blogsakura.infrastruct.common.PageRequest;

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
