package org.example.blogsakura.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import org.example.blogsakura.model.dto.spaceUser.SpaceUser;
import org.example.blogsakura.model.dto.spaceUser.SpaceUserAddRequest;
import org.example.blogsakura.model.dto.spaceUser.SpaceUserQueryRequest;
import org.example.blogsakura.model.vo.spaceUser.SpaceUserVO;

import java.util.List;

/**
 * 空间用户关联 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验函数
     *
     * @param spaceUser
     * @param add
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 构造查询条件
     *
     * @return
     */
    QueryWrapper getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取封装类
     *
     * @param spaceUser
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser);

    /**
     * 获取封装列表
     *
     * @param spaceUserList
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
