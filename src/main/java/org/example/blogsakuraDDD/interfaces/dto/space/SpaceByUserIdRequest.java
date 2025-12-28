package org.example.blogsakuraDDD.interfaces.dto.space;

import lombok.Data;

@Data
public class SpaceByUserIdRequest {

    private Long userId;

    /**
     * 空间类型 0:私有 1:团队
     */
    private Integer spaceType;
}
