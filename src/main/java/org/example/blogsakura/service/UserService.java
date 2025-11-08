package org.example.blogsakura.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.dto.user.UserQueryRequest;
import org.example.blogsakura.model.vo.user.LoginUserVO;
import org.example.blogsakura.model.vo.user.UserVO;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

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
     * 获取脱敏后的用户列表
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);


    /**
     * 分页查询条件
     *
     * @param userQueryRequest
     * @return
     */
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

}
