package org.example.blogsakura.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 给用户使用，目前只允许用户修改空间名字
 */
@Data
public class SpaceEditRequest implements Serializable {

    /**
     * 空间 id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    private static final long serialVersionUID = 1L;
}
