package org.example.blogsakura.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.blogsakuraDDD.infrastruct.exception.BusinessException;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.domain.space.entity.Space;
import org.example.blogsakura.model.dto.spaceUser.SpaceUser;
import org.example.blogsakuraDDD.infrastruct.mapper.SpaceUserMapper;
import org.example.blogsakura.model.dto.spaceUser.SpaceUserAddRequest;
import org.example.blogsakura.model.dto.spaceUser.SpaceUserQueryRequest;
import org.example.blogsakuraDDD.domain.user.entity.User;
import org.example.blogsakuraDDD.domain.space.valueobject.SpaceRoleEnum;
import org.example.blogsakuraDDD.interfaces.vo.space.SpaceVO;
import org.example.blogsakura.model.vo.spaceUser.SpaceUserVO;
import org.example.blogsakuraDDD.interfaces.vo.user.UserVO;
import org.example.blogsakura.service.SpaceService;
import org.example.blogsakura.service.SpaceUserService;
import org.example.blogsakuraDDD.application.service.UserApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 空间用户关联 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserService {

    @Resource
    private UserApplicationService userService;
    @Resource
    private SpaceService spaceService;

    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest
     * @return
     */
    @Override
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        // 校验添加空间成员的操作
        this.validSpaceUser(spaceUser, true);
        // 数据库操作
        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }

    /**
     * 校验
     *
     * @param spaceUser
     * @param add
     */
    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        // 创建时，空间 id 和用户 id 必填
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        if (add) {
            ThrowUtils.throwIf(spaceId == null || userId == null, ErrorCode.PARAMS_ERROR);
            User user = userService.getById(userId);
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        }
        // 校验空间角色
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null && spaceRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间角色不存在");
        }
    }

    /**
     * 构造查询条件
     *
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        Long id = spaceUserQueryRequest.getId();
        Long userId = spaceUserQueryRequest.getUserId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        queryWrapper.eq("spaceId", spaceId);
        queryWrapper.eq("spaceRole", spaceRole);
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("id", id);
        return queryWrapper;
    }

    /**
     * 获取封装类
     *
     * @param spaceUser
     * @return
     */
    @Override
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser) {
        SpaceUserVO spaceUserVO = new SpaceUserVO();
        BeanUtils.copyProperties(spaceUser, spaceUserVO);
        // 关联用户信息
        Long userId = spaceUser.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceUserVO.setUser(userVO);
        }
        // 关联查询空间信息
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceService.getById(spaceId);
            SpaceVO spaceVO = SpaceVO.objToVo(space);
            spaceUserVO.setSpace(spaceVO); // TODO 这部分也许有点问题，但不确定
        }
        return spaceUserVO;
    }

    /**
     * 获取封装列表
     *
     * @param spaceUserList
     * @return
     */
    @Override
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList) {
        // 判断输入列表是否为空
        if (spaceUserList == null || spaceUserList.isEmpty()) {
            return Collections.emptyList();
        }
        // 对象列表 => 封装对象列表
        List<SpaceUserVO> spaceUserVOList = spaceUserList.stream().map(SpaceUserVO::objToVo).collect(Collectors.toList());
        // 1. 收集需要关联查询的用户 ID 和空间 ID
        Set<Long> userIdSet = spaceUserList.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
        // 2. 批量查询用户和空间
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceService.listByIds(spaceIdSet).stream()
                .collect(Collectors.groupingBy(Space::getId));
        // 3. 填充 SpaceUserVO 的用户和空间信息
        spaceUserVOList.forEach(spaceUserVO -> {
            Long userId = spaceUserVO.getUserId();
            Long spaceId = spaceUserVO.getSpaceId();
            // 填充用户信息
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceUserVO.setUser(userService.getUserVO(user));
            // 填充空间信息
            Space space = null;
            if (spaceIdSpaceListMap.containsKey(spaceId)) {
                space = spaceIdSpaceListMap.get(spaceId).get(0);
            }
            spaceUserVO.setSpace(SpaceVO.objToVo(space));
        });
        return spaceUserVOList;
    }

}
