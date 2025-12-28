package org.example.blogsakura.interfaces.controller;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.infrastruct.annotation.AuthCheck;
import org.example.blogsakura.infrastruct.common.BaseResponse;
import org.example.blogsakura.infrastruct.common.ResultUtils;
import org.example.blogsakura.domain.user.constant.UserConstant;
import org.example.blogsakura.interfaces.vo.space.SpaceVO;
import org.example.blogsakura.interfaces.dto.space.*;
import org.springframework.web.bind.annotation.*;
import org.example.blogsakura.application.service.SpaceApplicationService;

import java.util.List;

/**
 * 空间 控制层。
 * TODO: 注意，用户只能创建一个空间，所谓空间创始人可以是用户，但只能创建一个空间，只有管理员可以多次创建空间
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/backend/space")
@Slf4j
public class SpaceController {

    @Resource
    private SpaceApplicationService spaceApplicationService;


    /**
     * 创建空间（所有人都可以使用）。
     *
     * @param spaceAddRequest 空间创建请求
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    public BaseResponse<Boolean> addSpace(@RequestBody SpaceAddRequest spaceAddRequest,
                                          HttpServletRequest request) {
        return ResultUtils.success(spaceApplicationService.addSpace(spaceAddRequest, request));
    }

    /**
     * 获取空间信息
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/")
    public BaseResponse<SpaceVO> getSpaceVOById(@RequestParam Long id, HttpServletRequest request) {
        return ResultUtils.success(spaceApplicationService.getSpaceVOById(id, request));
    }


    /**
     * 根据主键删除空间。针对管理员或者用户
     *
     * @param spaceDeleteRequest 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
    public BaseResponse<Boolean> deleteSpace(@RequestBody SpaceDeleteRequest spaceDeleteRequest, HttpServletRequest request) {
        return ResultUtils.success(spaceApplicationService.deleteSpace(spaceDeleteRequest, request));
    }

    /**
     * 获取封装后的空间信息分页
     *
     * @param spaceQueryRequest
     * @param request
     * @return
     */
    @PostMapping("list/page")
    public BaseResponse<Page<SpaceVO>> getSpaceVOListByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
                                                            HttpServletRequest request) {
        return ResultUtils.success(spaceApplicationService.getSpaceVOListByPage(spaceQueryRequest, request));
    }


    /**
     * 仅限管理员更新更新空间。
     *
     * @param spaceUpdateRequest 空间更新请求
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        return ResultUtils.success(spaceApplicationService.updateSpace(spaceUpdateRequest));
    }

    /**
     * 查询空间级别列表
     *
     * @return
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> getSpaceListLevel() {
        return ResultUtils.success(spaceApplicationService.getSpaceListLevel());
    }

    /**
     * 从用户id获取相应的空间列表信息
     *
     * @param spaceByUserIdRequest
     * @return
     */
    @PostMapping("/list_user")
    public BaseResponse<List<SpaceVO>> getSpaceVOListByUserId(@RequestBody SpaceByUserIdRequest spaceByUserIdRequest) {
        return ResultUtils.success(spaceApplicationService.getSpaceVOListByUserId(spaceByUserIdRequest));
    }
}
