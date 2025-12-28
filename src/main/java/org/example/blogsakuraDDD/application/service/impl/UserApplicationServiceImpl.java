package org.example.blogsakuraDDD.application.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.blogsakuraDDD.domain.user.constant.UserConstant;
import org.example.blogsakuraDDD.domain.user.service.UserDomainService;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.infrastruct.exception.BusinessException;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.domain.user.entity.User;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.infrastruct.mapper.UserMapper;
import org.example.blogsakuraDDD.interfaces.dto.user.UserLoginRequest;
import org.example.blogsakuraDDD.interfaces.dto.user.UserQueryRequest;
import org.example.blogsakuraDDD.domain.user.valueobject.UserRoleEnum;
import org.example.blogsakuraDDD.interfaces.dto.user.UserRegisterRequest;
import org.example.blogsakuraDDD.interfaces.vo.user.LoginUserVO;
import org.example.blogsakuraDDD.interfaces.vo.user.UserVO;
import org.example.blogsakuraDDD.application.service.UserApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class UserApplicationServiceImpl extends ServiceImpl<UserMapper, User> implements UserApplicationService {

    @Resource
    private UserDomainService userDomainService;

    /**
     * 用户注册
     *
     * @return 新用户 id
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 1. 校验
        User.validUserRegister(userAccount, userPassword, checkPassword);
        return userDomainService.userRegister(userAccount, userPassword, checkPassword);
    }


    /**
     * 用户登录
     *
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 1. 校验
        User.validUserLogin(userAccount, userPassword);
        return userDomainService.userLogin(userAccount, userPassword, request); // 返回脱敏后的用户数据
    }

    /**
     * 获取脱敏的已登录用户信息
     *
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        return userDomainService.getLoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User sessionLoginUser(HttpServletRequest request) {
        return userDomainService.sessionLoginUser(request);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        return userDomainService.userLogout(request);
    }

    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        return userDomainService.getUserVO(user);
    }


    /**
     * 构造分页查询条件
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        return userDomainService.getQueryWrapper(userQueryRequest);
    }

    /**
     * 增加用户
     *
     * @param user
     * @return
     */
    @Override
    public Boolean addUser(User user) {
        return userDomainService.addUser(user);
    }

    /**
     * 根据id获取User
     *
     * @param userId
     * @return
     */
    @Override
    public User getUserById(long userId) {
        return userDomainService.getUserById(userId);
    }

    /**
     * 用户信息脱敏：后台展示
     *
     * @param userId
     * @return
     */
    @Override
    public UserVO getUserVOById(long userId) {
        return userDomainService.getUserVOById(userId);
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
        return userDomainService.deleteUser(deleteRequest, request);
    }

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    @Override
    public Boolean updateUser(User user) {
        return userDomainService.updateUser(user);
    }

    /**
     * 获取分页
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public Page<UserVO> getUserVOListByPage(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = userQueryRequest.getCurrentPage();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userDomainService.page(Page.of(currentPage, pageSize),
                userDomainService.getQueryWrapper(userQueryRequest));
        // 数据脱敏
        Page<UserVO> userVOPage = new Page<>(currentPage, pageSize, userPage.getTotalRow());
        List<UserVO> userVOList = userDomainService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return userVOPage;
    }
}
