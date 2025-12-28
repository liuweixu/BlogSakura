package org.example.blogsakuraDDD.infrastruct.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.blogsakura.model.dto.spaceUser.SpaceUser;

/**
 * 空间用户关联 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface SpaceUserMapper extends BaseMapper<SpaceUser> {

}
