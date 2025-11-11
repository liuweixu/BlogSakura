package org.example.blogsakura.controller.backend;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.example.blogsakura.common.annotation.AuthCheck;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.constants.UserConstant;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.space.SpaceAddRequest;
import org.example.blogsakura.model.dto.space.SpaceUpdateRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.example.blogsakura.model.dto.space.Space;
import org.example.blogsakura.service.SpaceService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 创建空间（所有人都可以使用）。
     *
     * @param spaceAddRequest 空间创建请求
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
//    @PostMapping("/")
//    public boolean addSpace(@RequestBody SpaceAddRequest spaceAddRequest) {
//        return spaceService.save(space);
//    }
//
//    /**
//     * 根据主键删除空间。
//     *
//     * @param id 主键
//     * @return {@code true} 删除成功，{@code false} 删除失败
//     */
//    @DeleteMapping("remove/{id}")
//    public boolean remove(@PathVariable Long id) {
//        return spaceService.removeById(id);
//    }


    /**
     * 仅限管理员更新更新空间。
     *
     * @param spaceUpdateRequest 空间更新请求
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> update(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
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

//    /**
//     * 查询所有空间。
//     *
//     * @return 所有数据
//     */
//    @GetMapping("list")
//    public List<Space> list() {
//        return spaceService.list();
//    }
//
//    /**
//     * 根据主键获取空间。
//     *
//     * @param id 空间主键
//     * @return 空间详情
//     */
//    @GetMapping("getInfo/{id}")
//    public Space getInfo(@PathVariable Long id) {
//        return spaceService.getById(id);
//    }
//
//    /**
//     * 分页查询空间。
//     *
//     * @param page 分页对象
//     * @return 分页对象
//     */
//    @GetMapping("page")
//    public Page<Space> page(Page<Space> page) {
//        return spaceService.page(page);
//    }

}
