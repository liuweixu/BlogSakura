package org.example.blogsakura.controller.backend;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.common.annotation.AuthCheck;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.constants.UserConstant;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.space.*;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.enums.SpaceLevelEnum;
import org.example.blogsakura.model.vo.space.SpaceVO;
import org.example.blogsakura.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.example.blogsakura.service.SpaceService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 空间 控制层。
 * TODO: 注意，用户只能创建一个空间，所谓空间创始人可以是用户，但只能创建一个空间，只有管理员可以多次创建空间
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/backend/space")
public class SpaceController {

    @Resource
    private SpaceService spaceService;

    @Resource
    private UserService userService;

    /**
     * 创建空间（所有人都可以使用）。
     *
     * @param spaceAddRequest 空间创建请求
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    public BaseResponse<Boolean> addSpace(@RequestBody SpaceAddRequest spaceAddRequest,
                                          HttpServletRequest request) {
        User loginUser = userService.sessionLoginUser(request);
        long spaceId = spaceService.addSpace(spaceAddRequest, loginUser);
        return ResultUtils.success(spaceId != -1L);
    }

    @GetMapping("/")
    public BaseResponse<SpaceVO> getSpaceVOById(@RequestParam Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(SpaceVO.objToVo(space));
    }


    /**
     * 根据主键删除空间。针对管理员或者用户
     *
     * @param spaceDeleteRequest 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
    public BaseResponse<Boolean> deleteSpace(@RequestBody SpaceDeleteRequest spaceDeleteRequest, HttpServletRequest request) {
        Space space = spaceService.getById(spaceDeleteRequest.getId());
        User loginUser = userService.sessionLoginUser(request);
        if (!space.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(spaceService.removeById(space));
    }

    @PostMapping("list/page")
    public BaseResponse<Page<Space>> getSpaceVOListByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = spaceQueryRequest.getCurrentPage();
        long pageSize = spaceQueryRequest.getPageSize();
        return ResultUtils.success(spaceService.page(Page.of(currentPage, pageSize)));
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
        ThrowUtils.throwIf(spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        // 数据校验
        spaceService.validSpace(space, true);
        Space oldSpace = spaceService.getById(space.getId());
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.PARAMS_ERROR);
        // 操作数据库
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 查询空间级别列表
     *
     * @return
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> getSpaceListLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values()) // 获取所有枚举
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }


}
