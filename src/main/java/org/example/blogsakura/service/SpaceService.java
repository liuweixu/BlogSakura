package org.example.blogsakura.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.model.dto.space.Space;
import org.example.blogsakura.model.dto.space.SpaceAddRequest;
import org.example.blogsakura.model.dto.space.SpaceQueryRequest;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.vo.space.SpaceVO;

import java.util.List;

/**
 * 空间 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface SpaceService extends IService<Space> {

    /**
     * 校验空间
     *
     * @param space
     * @param add
     */
    void validSpace(Space space, boolean add);

    /**
     * 根据空间权限补充限额
     *
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

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
}
