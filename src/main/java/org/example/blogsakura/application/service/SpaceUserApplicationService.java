package org.example.blogsakura.application.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.domain.space.entity.SpaceUser;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUserAddRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUserEditRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUserQueryRequest;
import org.example.blogsakura.interfaces.vo.space.SpaceUserVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 空间用户关联 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface SpaceUserApplicationService extends IService<SpaceUser> {

    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest
     * @return
     */
    Long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验函数
     *
     * @param spaceUser
     * @param add
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 构造查询条件
     *
     * @return
     */
    QueryWrapper getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取封装类
     *
     * @param spaceUser
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser);

    /**
     * 获取封装列表
     *
     * @param spaceUserList
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

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

    /**
     * 查询空间成员列表
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @return 所有数据
     */
    List<SpaceUserVO> getSpaceUserVOList(SpaceUserQueryRequest spaceUserQueryRequest,
                                         HttpServletRequest request);

    /**
     * 编辑成员信息
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param spaceUserEditRequest
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    Boolean editSpaceUser(SpaceUserEditRequest spaceUserEditRequest, HttpServletRequest request);

    /**
     * 查看我加入的团队空间列表
     *
     * @param request
     * @return
     */
    List<SpaceUserVO> getMyTeamSpaceList(HttpServletRequest request);
}
