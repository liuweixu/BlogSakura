package org.example.blogsakura.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.mapper.SpaceUserMapper;
import org.example.blogsakura.model.dto.picture.Picture;
import org.example.blogsakura.model.dto.space.Space;
import org.example.blogsakura.mapper.SpaceMapper;
import org.example.blogsakura.model.dto.space.SpaceAddRequest;
import org.example.blogsakura.model.dto.space.SpaceQueryRequest;
import org.example.blogsakura.model.dto.spaceUser.SpaceUser;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.enums.SpaceLevelEnum;
import org.example.blogsakura.model.enums.SpaceRoleEnum;
import org.example.blogsakura.model.enums.SpaceTypeEnum;
import org.example.blogsakura.model.vo.picture.PictureVO;
import org.example.blogsakura.model.vo.space.SpaceVO;
import org.example.blogsakura.service.SpaceService;
import org.example.blogsakura.service.SpaceUserService;
import org.example.blogsakura.service.UserService;
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
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceService {

    /**
     * 实现编程式事务的关键
     */
    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserService userService;
    @Resource
    private SpaceMapper spaceMapper;
    @Resource
    private SpaceUserMapper spaceUserMapper;

    /**
     * 校验空间
     *
     * @param space
     * @param add
     */
    @Override
    public void validSpace(Space space, boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        Integer spaceType = space.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        // 要创建
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(spaceName), ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            ThrowUtils.throwIf(spaceLevel == null, ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            ThrowUtils.throwIf(spaceType == null, ErrorCode.PARAMS_ERROR, "空间类型不能为空");
        }
        // 修改数据时，如果要改空间级别
        ThrowUtils.throwIf(spaceLevel != null && spaceLevelEnum == null, ErrorCode.PARAMS_ERROR, "空间级别不存在");
        ThrowUtils.throwIf(StrUtil.isNotBlank(spaceName) && spaceName.length() > 30, ErrorCode.PARAMS_ERROR, "空间名称过长");
        ThrowUtils.throwIf(spaceType != null && spaceTypeEnum == null, ErrorCode.PARAMS_ERROR, "空间类型不存在");
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别，自动填充限额
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    /**
     * 创建空间，其中用户只能创建一个私有空间！
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
        this.fillSpaceBySpaceLevel(space);
        // 校验参数
        this.validSpace(space, true);
        // 获取用户登录
        Long userId = user.getId();
        space.setUserId(userId);
        // 权限校验
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 针对用户加锁（分布式锁）
        RLock lock = redissonClient.getLock("sakurablog:space:" + space.getId());
        lock.lock();
        try {
            Long newSpaceId = transactionTemplate.execute(status -> {
                if (!userService.isAdmin(user)) {
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

        List<Space> spaceListByUserId = spaceMapper.getSpaceListByUserId(userId, spaceType);
        // 获取相应的空间列表
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
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充用户信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }
}
