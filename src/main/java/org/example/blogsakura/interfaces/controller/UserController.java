package org.example.blogsakura.interfaces.controller;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.domain.user.constant.UserConstant;
import org.example.blogsakura.domain.user.entity.User;
import org.example.blogsakura.infrastruct.annotation.AuthCheck;
import org.example.blogsakura.infrastruct.aop.Log;
import org.example.blogsakura.infrastruct.common.BaseResponse;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.common.ResultUtils;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.interfaces.assembler.UserAssembler;
import org.example.blogsakura.interfaces.vo.user.LoginUserVO;
import org.example.blogsakura.interfaces.vo.user.UserVO;
import org.example.blogsakura.interfaces.dto.user.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.example.blogsakura.application.service.UserApplicationService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户 控制层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/backend/user")
@Slf4j
public class UserController {

    @Resource
    private UserApplicationService userApplicationService;

    /**
     * 用户管理：用户创建请求。
     *
     * @param userAddRequest 用户
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR, "请求为空");
        User userEntity = UserAssembler.toUserEntity(userAddRequest);
        return ResultUtils.success(userApplicationService.addUser(userEntity));
    }

    /**
     * 根据主键获取用户:管理者
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("/{id}")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(@PathVariable Long id) {
        return ResultUtils.success(userApplicationService.getUserById(id));
    }

    /**
     * 用户信息脱敏：后台展示
     *
     * @param id
     * @return
     */
    @GetMapping("/manage/{id}")
    public BaseResponse<UserVO> getUserVOById(@PathVariable Long id) {
        return ResultUtils.success(userApplicationService.getUserVOById(id));
    }

    /**
     * 根据删除请求删除用户:管理者
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return ResultUtils.success(userApplicationService.deleteUser(deleteRequest, request));
    }

    /**
     * 根据主键更新用户。
     *
     * @param userUpdateRequest 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null
                , ErrorCode.PARAMS_ERROR);
        User userEntity = UserAssembler.toUserEntity(userUpdateRequest);
        return ResultUtils.success(userApplicationService.updateUser(userEntity));
    }


    /**
     * 分页获取用户列表:管理者。注意数据脱敏，要在后台展示
     *
     * @return 分页对象
     */
    @PostMapping("list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> getUserVOListByPage(@RequestBody UserQueryRequest userQueryRequest) {
        return ResultUtils.success(userApplicationService.getUserVOListByPage(userQueryRequest));
    }


    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    @Log("用户注册")
    public BaseResponse<Long> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        return ResultUtils.success(userApplicationService.userRegister(userRegisterRequest));
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    @Log("登录用户")
    public BaseResponse<LoginUserVO> loginUser(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        return ResultUtils.success(userApplicationService.userLogin(userLoginRequest, request));
    }

    /**
     * 无需请求，就直接获取当前session上的登录信息，就是登录时，可以从这个判断用户是否登录
     *
     * @param request
     * @return
     */
    @GetMapping("/session/login")
    public BaseResponse<LoginUserVO> SessionLoginUser(HttpServletRequest request) {
        User loginUser = userApplicationService.sessionLoginUser(request);
        return ResultUtils.success(userApplicationService.getLoginUserVO(loginUser));
    }

    @GetMapping("/logout")
    @Log("用户退出")
    public BaseResponse<Boolean> logoutUser(HttpServletRequest request) {
        return ResultUtils.success(userApplicationService.userLogout(request));
    }

}
