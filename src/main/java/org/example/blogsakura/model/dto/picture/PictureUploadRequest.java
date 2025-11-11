package org.example.blogsakura.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {

    /**
     * 图片 id（用于修改）
     */
    private Long id;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 空间字段，null表示公共图库
     */
    private Long spaceId;

    private static final long serialVersionUID = 1L;
}
