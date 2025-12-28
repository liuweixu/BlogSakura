package org.example.blogsakura.application.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.domain.space.service.SpaceUserDomainService;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.exception.BusinessException;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.domain.space.entity.Space;
import org.example.blogsakura.domain.space.entity.SpaceUser;
import org.example.blogsakura.infrastruct.mapper.SpaceUserMapper;
import org.example.blogsakura.interfaces.assembler.SpaceUserAssembler;
import org.example.blogsakura.interfaces.dto.space.SpaceUserAddRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUserEditRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUserQueryRequest;
import org.example.blogsakura.domain.user.entity.User;
import org.example.blogsakura.domain.space.valueobject.SpaceRoleEnum;
import org.example.blogsakura.interfaces.vo.space.SpaceVO;
import org.example.blogsakura.interfaces.vo.space.SpaceUserVO;
import org.example.blogsakura.interfaces.vo.user.UserVO;
import org.example.blogsakura.application.service.SpaceApplicationService;
import org.example.blogsakura.application.service.SpaceUserApplicationService;
import org.example.blogsakura.application.service.UserApplicationService;
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
public class SpaceUserApplicationServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserApplicationService {

    @Resource
    private UserApplicationService userApplicationService;
    @Resource
    private SpaceApplicationService spaceApplicationService;
    @Resource
    private SpaceUserDomainService spaceUserDomainService;

    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest
     * @return
     */
    @Override
    public Long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = SpaceUserAssembler.toSpaceUserEntity(spaceUserAddRequest);
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
            User user = userApplicationService.getById(userId);
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            Space space = spaceApplicationService.getById(spaceId);
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
        return spaceUserDomainService.getQueryWrapper(spaceUserQueryRequest);
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
            User user = userApplicationService.getById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            spaceUserVO.setUser(userVO);
        }
        // 关联查询空间信息
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceApplicationService.getById(spaceId);
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
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceApplicationService.listByIds(spaceIdSet).stream()
                .collect(Collectors.groupingBy(Space::getId));
        // 3. 填充 SpaceUserVO 的用户和空间信息
        spaceUserVOList.forEach(spaceUserVO -> {
            Long userId = spaceUserVO.getUserId();
            Long spaceId = spaceUserVO.getSpaceId();
            // 填充用户信息
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).getFirst();
            }
            spaceUserVO.setUser(userApplicationService.getUserVO(user));
            // 填充空间信息
            Space space = null;
            if (spaceIdSpaceListMap.containsKey(spaceId)) {
                space = spaceIdSpaceListMap.get(spaceId).getFirst();
            }
            spaceUserVO.setSpace(SpaceVO.objToVo(space));
        });
        return spaceUserVOList;
    }

    /**
     * 从空间移除成员
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Override
    public Boolean deleteSpaceUser(DeleteRequest deleteRequest) {
        return spaceUserDomainService.deleteSpaceUser(deleteRequest);
    }

    /**
     * 查询某个成员在空间的信息（从用户id和空间id）
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param spaceUserQueryRequest
     * @return 空间用户关联详情
     */
    @Override
    public SpaceUser getSpaceUser(SpaceUserQueryRequest spaceUserQueryRequest) {
        return spaceUserDomainService.getSpaceUser(spaceUserQueryRequest);
    }

    /**
     * 查询空间成员列表
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param spaceUserQueryRequest
     * @param request
     * @return 所有数据
     */
    @Override
    public List<SpaceUserVO> getSpaceUserVOList(SpaceUserQueryRequest spaceUserQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
        List<SpaceUser> spaceUserList = spaceUserDomainService.list(
                spaceUserDomainService.getQueryWrapper(spaceUserQueryRequest)
        );
        return this.getSpaceUserVOList(spaceUserList);
    }

    /**
     * 编辑成员信息
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param spaceUserEditRequest
     * @param request
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @Override
    public Boolean editSpaceUser(SpaceUserEditRequest spaceUserEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserEditRequest == null || spaceUserEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = SpaceUserAssembler.toSpaceUserEntity(spaceUserEditRequest);
        // 数据校验
        this.validSpaceUser(spaceUser, false); // 是更新
        // 查看数据是否存在
        Long id = spaceUserEditRequest.getId();
        SpaceUser oldSpaceUser = spaceUserDomainService.getById(id);
        ThrowUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceUserDomainService.updateById(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return result;
    }

    /**
     * 查看我加入的团队空间列表
     *
     * @param request
     * @return
     */
    @Override
    public List<SpaceUserVO> getMyTeamSpaceList(HttpServletRequest request) {
        User loginUser = userApplicationService.sessionLoginUser(request);
        SpaceUserQueryRequest spaceUserQueryRequest = new SpaceUserQueryRequest();
        spaceUserQueryRequest.setUserId(loginUser.getId());
        List<SpaceUser> spaceUserList = spaceUserDomainService.list(
                spaceUserDomainService.getQueryWrapper(spaceUserQueryRequest)
        );
        return this.getSpaceUserVOList(spaceUserList);
    }

}
