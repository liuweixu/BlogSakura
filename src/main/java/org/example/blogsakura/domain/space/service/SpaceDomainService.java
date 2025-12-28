package org.example.blogsakura.domain.space.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import org.example.blogsakura.domain.space.entity.Space;
import org.example.blogsakura.interfaces.dto.space.SpaceByUserIdRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceLevel;
import org.example.blogsakura.interfaces.dto.space.SpaceQueryRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUpdateRequest;
import org.example.blogsakura.interfaces.vo.space.SpaceVO;

import java.util.List;

/**
 * 空间 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface SpaceDomainService extends IService<Space> {


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
