package org.example.blogsakuraDDD.domain.user.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakuraDDD.domain.user.constant.UserConstant;
import org.example.blogsakuraDDD.domain.user.entity.User;
import org.example.blogsakuraDDD.domain.user.service.UserDomainService;
import org.example.blogsakuraDDD.domain.user.valueobject.UserRoleEnum;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.infrastruct.exception.BusinessException;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.infrastruct.mapper.UserMapper;
import org.example.blogsakuraDDD.interfaces.dto.user.UserQueryRequest;
import org.example.blogsakuraDDD.interfaces.vo.user.LoginUserVO;
import org.example.blogsakuraDDD.interfaces.vo.user.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class UserDomainServiceImpl extends ServiceImpl<UserMapper, User> implements UserDomainService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 检查是否重复
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        //TODO 逻辑删除下，此处查询会把逻辑删除的忽略掉
        long count = this.getMapper().selectCountByQuery(queryWrapper);
        //TODO 所以还要新增一个查询条件，把逻辑删除的也要查出来
        User user_exist = userMapper.selectUserWithoutLogicDelete(userAccount);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        if (user_exist != null) {
            user_exist.setIsDelete(0);
            user_exist.setUserPassword(User.getEncryptPassword(userPassword));
            user_exist.setUserName("无名");
            user_exist.setUserRole(UserRoleEnum.USER.getValue());
            userMapper.updateUserWithoutLogicDelete(user_exist);
            return user_exist.getId();
        }
        // 3. 加密
        String encryptPassword = User.getEncryptPassword(userPassword);
        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        this.save(user);
        return user.getId();
    }


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 加密
        String encryptPassword = User.getEncryptPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user); // 返回脱敏后的用户数据
    }

    /**
     * 获取脱敏的已登录用户信息
     *
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User sessionLoginUser(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) obj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否登录
        Object object = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (object == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏后的用户列表
     *
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }


    /**
     * 构造分页查询条件
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("userRole", userRole)
                .like("userAccount", userAccount)
                .like("userName", userName)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    /**
     * 增加用户
     *
     * @param user
     * @return
     */
    @Override
    public Boolean addUser(User user) {
        // 默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encodePassword = User.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encodePassword);
        boolean result = this.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.PARAMS_ERROR);
        return result;
    }

    /**
     * 根据id获取User
     *
     * @param userId
     * @return
     */
    @Override
    public User getUserById(long userId) {
        ThrowUtils.throwIf(userId <= 0, ErrorCode.PARAMS_ERROR);
        User user = this.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR);
        return user;
    }

    /**
     * 用户信息脱敏：后台展示
     *
     * @param userId
     * @return
     */
    @Override
    public UserVO getUserVOById(long userId) {
        User user = this.getUserById(userId);
        return this.getUserVO(user);
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @Override
    public Boolean deleteUser(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return this.removeById(id);
    }

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    @Override
    public Boolean updateUser(User user) {
        boolean result = this.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.PARAMS_ERROR);
        return result;
    }
}
