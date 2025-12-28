package org.example.blogsakura.interfaces.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.infrastruct.common.BaseResponse;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.common.ResultUtils;
import org.example.blogsakura.interfaces.dto.space.SpaceUserAddRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUserEditRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUserQueryRequest;
import org.example.blogsakura.interfaces.vo.space.SpaceUserVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.example.blogsakura.domain.space.entity.SpaceUser;
import org.example.blogsakura.application.service.SpaceUserApplicationService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 空间用户关联 控制层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/spaceUser")
public class SpaceUserController {

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    /**
     * 添加成员到空间
     * 权限：仅拥有成员管理权限的用户可使用（该用户只能创建一个团队空间）
     *
     * @param spaceUserAddRequest
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
//    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest,
                                           HttpServletRequest request) {
        return ResultUtils.success(spaceUserApplicationService.addSpaceUser(spaceUserAddRequest));
    }

    /**
     * 从空间移除成员
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
//    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(spaceUserApplicationService.deleteSpaceUser(deleteRequest));
    }

    /**
     * 查询某个成员在空间的信息（从用户id和空间id）
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param spaceUserQueryRequest
     * @return 空间用户关联详情
     */
    @PostMapping("/list_one")
//    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<SpaceUser> getSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
        return ResultUtils.success(spaceUserApplicationService.getSpaceUser(spaceUserQueryRequest));
    }

    /**
     * 查询空间成员列表
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @return 所有数据
     */
    @PostMapping("list")
//    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<List<SpaceUserVO>> getSpaceUserVOList(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest,
                                                              HttpServletRequest request) {

        return ResultUtils.success(spaceUserApplicationService.getSpaceUserVOList(spaceUserQueryRequest, request));
    }

    /**
     * 编辑成员信息
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param spaceUserEditRequest
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
//    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest, HttpServletRequest request) {
        return ResultUtils.success(spaceUserApplicationService.editSpaceUser(spaceUserEditRequest, request));
    }

    /**
     * 查看我加入的团队空间列表
     *
     * @param request
     * @return
     */
    @PostMapping("/list/my")
    public BaseResponse<List<SpaceUserVO>> getMyTeamSpaceList(HttpServletRequest request) {
        return ResultUtils.success(spaceUserApplicationService.getMyTeamSpaceList(request));
    }
}
