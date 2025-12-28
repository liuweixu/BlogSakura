package org.example.blogsakura.application.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.domain.space.entity.Space;
import org.example.blogsakura.interfaces.dto.space.*;
import org.example.blogsakura.domain.user.entity.User;
import org.example.blogsakura.interfaces.vo.space.SpaceVO;

import java.util.List;

/**
 * 空间 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface SpaceApplicationService extends IService<Space> {

    /**
     * 获取封装类
     *
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 创建空间，其中用户只能创建一个私有空间！
     *
     * @param spaceAddRequest
     * @param user
     * @returu
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User user);

    /**
     * 从用户id获取相应的空间列表信息
     * spaceType表示私有或团队类型
     *
     * @param userId
     * @return
     */
    List<SpaceVO> getSpaceVOListByUserId(Long userId, Integer spaceType);


    /**
     * 构建查询条件
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 对Page列表处理
     *
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getPictureVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 创建空间（所有人都可以使用）。
     *
     * @param spaceAddRequest 空间创建请求
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    Boolean addSpace(SpaceAddRequest spaceAddRequest, HttpServletRequest request);

    /**
     * 获取空间信息
     *
     * @param id
     * @param request
     * @return
     */
    SpaceVO getSpaceVOById(Long id, HttpServletRequest request);

    /**
     * 根据主键删除空间。针对管理员或者用户
     *
     * @param spaceDeleteRequest 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    Boolean deleteSpace(SpaceDeleteRequest spaceDeleteRequest, HttpServletRequest request);

    /**
     * 获取封装后的空间信息分页
     *
     * @param spaceQueryRequest
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOListByPage(SpaceQueryRequest spaceQueryRequest,
                                       HttpServletRequest request);

    /**
     * 仅限管理员更新更新空间。此处下沉到领域层
     *
     * @param spaceUpdateRequest 空间更新请求
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    Boolean updateSpace(SpaceUpdateRequest spaceUpdateRequest);

    /**
     * 查询空间级别列表
     *
     * @return
     */
    List<SpaceLevel> getSpaceListLevel();

    /**
     * 从用户id获取相应的空间列表信息
     *
     * @param spaceByUserIdRequest
     * @return
     */
    List<SpaceVO> getSpaceVOListByUserId(SpaceByUserIdRequest spaceByUserIdRequest);
}
