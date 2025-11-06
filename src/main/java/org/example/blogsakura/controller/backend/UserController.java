package org.example.blogsakura.controller.backend;

import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.common.annotation.AuthCheck;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.constants.UserConstant;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.user.*;
import org.example.blogsakura.model.vo.user.LoginUserVO;
import org.example.blogsakura.model.vo.user.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.blogsakura.service.UserService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/backend/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户管理：用户创建请求。
     *
     * @param userAddRequest 用户
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求为空");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encodePassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encodePassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 根据主键获取用户:管理者
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(@PathVariable Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 用户信息脱敏：后台展示
     *
     * @param id
     * @return
     */
    @GetMapping("/manage/{id}")
    public BaseResponse<UserVO> getUserVOById(@PathVariable Long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 根据主键删除用户:管理者
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> removeUserById(@PathVariable Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.removeById(id));
    }

    /**
     * 根据主键更新用户。
     *
     * @param userUpdateRequest 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null
                , ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(result);
    }


    /**
     * 分页获取用户列表:管理者。注意数据脱敏，要在后台展示
     *
     * @return 分页对象
     */
    @PostMapping("list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = userQueryRequest.getCurrentPage();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(Page.of(currentPage, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        // 数据脱敏
        Page<UserVO> userVOPage = new Page<>(currentPage, pageSize, userPage.getTotalRow());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }


    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        return ResultUtils.success(userService.userRegister(userAccount, userPassword, checkPassword));
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> loginUser(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 无需请求，就直接获取当前session上的登录信息，就是登录时，可以从这个判断用户是否登录
     *
     * @param request
     * @return
     */
    @GetMapping("/session/login")
    public BaseResponse<LoginUserVO> loginUserSession(HttpServletRequest request) {
        User loginUser = userService.sessionLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    @GetMapping("/logout")
    public BaseResponse<Boolean> logoutUser(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userLogout(request));
    }

}
