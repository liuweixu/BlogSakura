package org.example.blogsakura.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.space.Space;
import org.example.blogsakura.mapper.SpaceMapper;
import org.example.blogsakura.model.enums.SpaceLevelEnum;
import org.example.blogsakura.service.SpaceService;
import org.springframework.stereotype.Service;

/**
 * 空间 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceService {

    /**
     * 校验空间
     *
     * @param space
     * @param add
     */
    @Override
    public void validSpace(Space space, boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        // 从空间对象中取值
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum.getEnumByValue(spaceLevel);
    }
}
