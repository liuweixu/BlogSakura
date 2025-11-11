package org.example.blogsakura.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import org.example.blogsakura.model.dto.space.Space;
import org.example.blogsakura.model.dto.space.SpaceAddRequest;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.vo.space.SpaceVO;

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
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
}
