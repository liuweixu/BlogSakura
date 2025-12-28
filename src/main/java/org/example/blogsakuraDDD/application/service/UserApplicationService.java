package org.example.blogsakuraDDD.application.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakuraDDD.domain.user.entity.User;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.interfaces.dto.user.UserLoginRequest;
import org.example.blogsakuraDDD.interfaces.dto.user.UserQueryRequest;
import org.example.blogsakuraDDD.interfaces.dto.user.UserRegisterRequest;
import org.example.blogsakuraDDD.interfaces.vo.user.LoginUserVO;
import org.example.blogsakuraDDD.interfaces.vo.user.UserVO;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface UserApplicationService extends IService<User> {

    /**
     * 用户注册
     *
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);


    /**
     * 用户登录
     *
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User sessionLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);


    /**
     * 分页查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 增加用户
     *
     * @param user
     * @return
     */
    Boolean addUser(User user);

    /**
     * 根据id获取User
     *
     * @param userId
     * @return
     */
    User getUserById(long userId);

    /**
     * 用户信息脱敏：后台展示
     *
     * @param userId
     * @return
     */
    UserVO getUserVOById(long userId);

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    Boolean deleteUser(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    Boolean updateUser(User user);

    /**
     * 获取分页
     *
     * @param userQueryRequest
     * @return
     */
    Page<UserVO> getUserVOListByPage(UserQueryRequest userQueryRequest);


}
