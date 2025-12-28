package org.example.blogsakura.domain.space.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.domain.space.entity.Space;
import org.example.blogsakura.domain.space.service.SpaceDomainService;
import org.example.blogsakura.domain.space.valueobject.SpaceLevelEnum;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.infrastruct.mapper.SpaceMapper;
import org.example.blogsakura.interfaces.assembler.SpaceAssembler;
import org.example.blogsakura.interfaces.dto.space.SpaceByUserIdRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceLevel;
import org.example.blogsakura.interfaces.dto.space.SpaceQueryRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUpdateRequest;
import org.example.blogsakura.interfaces.vo.space.SpaceVO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 空间 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class SpaceDomainServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceDomainService {

    @Resource
    private SpaceMapper spaceMapper;


    /**
     * 从用户id获取相应的空间列表信息
     * spaceType表示私有或团队类型
     *
     * @param userId
     * @return
     */
    @Override
    public List<SpaceVO> getSpaceVOListByUserId(Long userId, Integer spaceType) {

        // 获取相应的空间列表
        List<Space> spaceListByUserId = spaceMapper.getSpaceListByUserId(userId, spaceType);
        return spaceListByUserId.stream().map(SpaceVO::objToVo).toList();
    }

    /**
     * 构建查询条件
     *
     * @param spaceQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        Integer spaceType = spaceQueryRequest.getSpaceType();
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        // 构建搜索条件
        queryWrapper.eq("id", id)
                .eq("userId", userId)
                .eq("spaceType", spaceType)
                .eq("spaceLevel", spaceLevel)
                .eq("spaceName", spaceName)
                .orderBy(sortField, sortOrder.equals("ascend"));
        return queryWrapper;
    }

    /**
     * 仅限管理员更新更新空间。此处下沉到领域层
     *
     * @param spaceUpdateRequest 空间更新请求
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @Override
    public Boolean updateSpace(SpaceUpdateRequest spaceUpdateRequest) {
        ThrowUtils.throwIf(spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Space space = SpaceAssembler.toSpaceEntity(spaceUpdateRequest);
        // 自动填充数据
        Space.fillSpaceBySpaceLevel(space);
        // 数据校验
        Space.validSpace(space, true);
        Space oldSpace = this.getById(space.getId());
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.PARAMS_ERROR);
        // 操作数据库
        boolean result = this.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return result;
    }

    /**
     * 查询空间级别列表
     *
     * @return
     */
    @Override
    public List<SpaceLevel> getSpaceListLevel() {
        return Arrays.stream(SpaceLevelEnum.values()) // 获取所有枚举
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
    }

    /**
     * 从用户id获取相应的空间列表信息
     *
     * @param spaceByUserIdRequest
     * @return
     */
    @Override
    public List<SpaceVO> getSpaceVOListByUserId(SpaceByUserIdRequest spaceByUserIdRequest) {
        ThrowUtils.throwIf(spaceByUserIdRequest == null, ErrorCode.PARAMS_ERROR);
        Long userId = spaceByUserIdRequest.getUserId();
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        Integer spaceType = spaceByUserIdRequest.getSpaceType();
        ThrowUtils.throwIf(spaceType != 0 && spaceType != 1, ErrorCode.PARAMS_ERROR);
        return this.getSpaceVOListByUserId(userId, spaceType);
    }


}
