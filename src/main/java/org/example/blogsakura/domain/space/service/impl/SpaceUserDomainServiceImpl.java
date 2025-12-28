package org.example.blogsakura.domain.space.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.blogsakura.domain.space.entity.SpaceUser;
import org.example.blogsakura.domain.space.service.SpaceUserDomainService;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.infrastruct.mapper.SpaceUserMapper;
import org.example.blogsakura.interfaces.dto.space.SpaceUserQueryRequest;
import org.springframework.stereotype.Service;

/**
 * 空间用户关联 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
public class SpaceUserDomainServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserDomainService {


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
     * 从空间移除成员
     * 权限：仅拥有成员管理权限的用户可使用
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Override
    public Boolean deleteSpaceUser(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        // 判断是否存在
        SpaceUser spaceUser = this.getById(id);
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return result;
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
        ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long userId = spaceUserQueryRequest.getUserId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        ThrowUtils.throwIf(spaceId == null || spaceId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        SpaceUser spaceUser = this.getOne(this.getQueryWrapper(spaceUserQueryRequest));
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        return spaceUser;
    }


}
