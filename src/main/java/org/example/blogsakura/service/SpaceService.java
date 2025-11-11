package org.example.blogsakura.service;

import com.mybatisflex.core.service.IService;
import org.example.blogsakura.model.dto.space.Space;

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
}
