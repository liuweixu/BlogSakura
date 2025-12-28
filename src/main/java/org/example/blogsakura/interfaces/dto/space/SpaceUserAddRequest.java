package org.example.blogsakura.interfaces.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加空间成员请求，给空间管理员使用（即创建团队空间的用户）
 */
@Data
public class SpaceUserAddRequest implements Serializable {

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}
