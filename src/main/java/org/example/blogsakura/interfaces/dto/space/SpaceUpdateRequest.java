package org.example.blogsakura.interfaces.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间更新请求，给管理员使用，权限更大
 */
@Data
public class SpaceUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    /**
     * 空间类型
     */
    private Integer spaceType;

    private static final long serialVersionUID = 1L;
}
