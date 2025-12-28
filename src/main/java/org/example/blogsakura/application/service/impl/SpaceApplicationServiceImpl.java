package org.example.blogsakura.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.domain.space.service.SpaceDomainService;
import org.example.blogsakura.domain.space.service.SpaceUserDomainService;
import org.example.blogsakura.infrastruct.exception.BusinessException;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.infrastruct.mapper.SpaceUserMapper;
import org.example.blogsakura.domain.space.entity.Space;
import org.example.blogsakura.infrastruct.mapper.SpaceMapper;
import org.example.blogsakura.interfaces.dto.space.*;
import org.example.blogsakura.domain.space.entity.SpaceUser;
import org.example.blogsakura.domain.user.entity.User;
import org.example.blogsakura.domain.space.valueobject.SpaceLevelEnum;
import org.example.blogsakura.domain.space.valueobject.SpaceRoleEnum;
import org.example.blogsakura.domain.space.valueobject.SpaceTypeEnum;
import org.example.blogsakura.interfaces.vo.space.SpaceVO;
import org.example.blogsakura.application.service.SpaceApplicationService;
import org.example.blogsakura.application.service.UserApplicationService;
import org.example.blogsakura.interfaces.vo.user.UserVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 空间 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class SpaceApplicationServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceApplicationService {

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceDomainService spaceDomainService;

    @Resource
    private SpaceUserMapper spaceUserMapper;


    /**
     * 实现编程式事务的关键
     */
    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private RedissonClient redissonClient;


    /**
     * 获取封装类
     *
     * @param space
     * @param request
     * @return
     */
    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联用户查询信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    /**
     * 创建空间，其中用户只能创建一个私有空间！
     * 涉及到SpaceUser的调用，故而不能下沉
     *
     * @param spaceAddRequest
     * @param user
     * @return
     */
    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User user) {
        // 设置默认值
        if (StrUtil.isBlank(spaceAddRequest.getSpaceName())) {
            spaceAddRequest.setSpaceName("默认空间");
        }
        if (spaceAddRequest.getSpaceLevel() == null) {
            spaceAddRequest.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        if (spaceAddRequest.getSpaceType() == null) {
            spaceAddRequest.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }

        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        // 填充容量和大小
        Space.fillSpaceBySpaceLevel(space);
        // 校验参数
        Space.validSpace(space, true);
        // 获取用户登录
        Long userId = user.getId();
        space.setUserId(userId);
        // 权限校验
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !user.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 针对用户加锁（分布式锁）
        RLock lock = redissonClient.getLock("sakurablog:space:" + space.getId());
        lock.lock();
        try {
            Long newSpaceId = transactionTemplate.execute(status -> {
                if (!user.isAdmin()) {
                    boolean exist = exists(this.query()
                            .eq(Space::getUserId, userId)
                            .eq(Space::getSpaceType, spaceAddRequest.getSpaceType()));

                    ThrowUtils.throwIf(exist, ErrorCode.OPERATION_ERROR, "每个用户每类空间只能创建一个");
                }
                // 写入数据库
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                // 如果是团队空间，关联新增团队成员记录
                if (SpaceTypeEnum.TEAM.getValue() == spaceAddRequest.getSpaceType()) {
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setUserId(userId);
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    result = spaceUserMapper.insert(spaceUser) > 0;
                    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建团队记录失败");
                }
                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
        } finally {
            lock.unlock();
        }
    }

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
        return spaceDomainService.getSpaceVOListByUserId(userId, spaceType);
    }

    /**
     * 构建查询条件
     *
     * @param spaceQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        return spaceDomainService.getQueryWrapper(spaceQueryRequest);
    }

    /**
     * 对Page处理
     *
     * @param spacePage
     * @param request
     * @return
     */
    @Override
    public Page<SpaceVO> getPictureVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(
                spacePage.getPageNumber(), spacePage.getPageSize(), spacePage.getTotalRow());
        // spaceList为空时，直接返回spaceVOPage(也是为空)
        if (spaceList == null || spaceList.isEmpty()) {
            return spaceVOPage;
        }
        // 对象列表->封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream().map(SpaceVO::objToVo).toList();
        // 关联查询的用户信息
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充用户信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userApplicationService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    /**
     * 创建空间（所有人都可以使用）。
     *
     * @param spaceAddRequest 空间创建请求
     * @param request
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @Override
    public Boolean addSpace(SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
//        User loginUser = userService.sessionLoginUser(request);
        Long userId = spaceAddRequest.getUserId();
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        User user = userApplicationService.getById(userId);
        long spaceId = this.addSpace(spaceAddRequest, user);
        return spaceId != -1L;
    }

    /**
     * 获取空间信息
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public SpaceVO getSpaceVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Space space = spaceDomainService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        return this.getSpaceVO(space, request);
    }

    /**
     * 根据主键删除空间。针对管理员或者用户
     *
     * @param spaceDeleteRequest 主键
     * @param request
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Override
    public Boolean deleteSpace(SpaceDeleteRequest spaceDeleteRequest, HttpServletRequest request) {
        Space space = spaceDomainService.getById(spaceDeleteRequest.getId());
        User loginUser = userApplicationService.sessionLoginUser(request);
        if (!space.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return spaceDomainService.removeById(space);
    }

    /**
     * 获取封装后的空间信息分页
     *
     * @param spaceQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<SpaceVO> getSpaceVOListByPage(SpaceQueryRequest spaceQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = spaceQueryRequest.getCurrentPage();
        long pageSize = spaceQueryRequest.getPageSize();
        Page<Space> spacePage = spaceDomainService.page(Page.of(currentPage, pageSize),
                spaceDomainService.getQueryWrapper(spaceQueryRequest));
        return this.getPictureVOPage(spacePage, request);
    }

    /**
     * 仅限管理员更新更新空间。
     *
     * @param spaceUpdateRequest 空间更新请求
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @Override
    public Boolean updateSpace(SpaceUpdateRequest spaceUpdateRequest) {
        return spaceDomainService.updateSpace(spaceUpdateRequest);
    }

    /**
     * 查询空间级别列表
     *
     * @return
     */
    @Override
    public List<SpaceLevel> getSpaceListLevel() {
        return spaceDomainService.getSpaceListLevel();
    }

    /**
     * 从用户id获取相应的空间列表信息
     *
     * @param spaceByUserIdRequest
     * @return
     */
    @Override
    public List<SpaceVO> getSpaceVOListByUserId(SpaceByUserIdRequest spaceByUserIdRequest) {
        return spaceDomainService.getSpaceVOListByUserId(spaceByUserIdRequest);
    }
}
