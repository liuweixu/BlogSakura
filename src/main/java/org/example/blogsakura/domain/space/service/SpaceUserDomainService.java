package org.example.blogsakura.domain.space.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import org.example.blogsakura.domain.space.entity.SpaceUser;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUserQueryRequest;

/**
 * 空间用户关联 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface SpaceUserDomainService extends IService<SpaceUser> {


    /**
     * 构造查询条件
     *
     * @return
     */
    QueryWrapper getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 从空间移除成员
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    Boolean deleteSpaceUser(DeleteRequest deleteRequest);

    /**
     * 查询某个成员在空间的信息（从用户id和空间id）
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param spaceUserQueryRequest
     * @return 空间用户关联详情
     */
    SpaceUser getSpaceUser(SpaceUserQueryRequest spaceUserQueryRequest);


}
