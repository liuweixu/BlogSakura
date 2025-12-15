package org.example.blogsakura.controller.backend;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.annotation.AuthCheck;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.constants.UserConstant;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.mapper.SpaceMapper;
import org.example.blogsakura.model.dto.space.*;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.enums.SpaceLevelEnum;
import org.example.blogsakura.model.vo.space.SpaceVO;
import org.example.blogsakura.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
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
//        User loginUser = userService.sessionLoginUser(request);
        Long userId = spaceAddRequest.getUserId();
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(userId);
        long spaceId = spaceService.addSpace(spaceAddRequest, user);
        return ResultUtils.success(spaceId != -1L);
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
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        User user = userService.getById(space.getUserId());
        spaceVO.setUser(userService.getUserVO(user));
        return ResultUtils.success(spaceVO);
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

    /**
     * 获取封装后的空间列表信息
     *
     * @param spaceQueryRequest
     * @param request
     * @return
     */
    @PostMapping("list/page")
    public BaseResponse<Page<SpaceVO>> getSpaceVOListByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
                                                            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = spaceQueryRequest.getCurrentPage();
        long pageSize = spaceQueryRequest.getPageSize();
        Page<Space> spacePage = spaceService.page(Page.of(currentPage, pageSize),
                spaceService.getQueryWrapper(spaceQueryRequest));
        Page<SpaceVO> spaceVOPage = spaceService.getPictureVOPage(spacePage, request);
        return ResultUtils.success(spaceVOPage);
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
        log.info("spaceUpdateRequest:{}", spaceUpdateRequest);
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

    /**
     * 从用户id获取相应的空间列表信息
     *
     * @param spaceByUserIdRequest
     * @return
     */
    @PostMapping("/list_user")
    public BaseResponse<List<SpaceVO>> getSpaceVOListByUserId(@RequestBody SpaceByUserIdRequest spaceByUserIdRequest) {
        ThrowUtils.throwIf(spaceByUserIdRequest == null, ErrorCode.PARAMS_ERROR);
        Long userId = spaceByUserIdRequest.getUserId();
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        Integer spaceType = spaceByUserIdRequest.getSpaceType();
        ThrowUtils.throwIf(spaceType != 0 && spaceType != 1, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceService.getSpaceVOListByUserId(userId, spaceType));
    }
}
