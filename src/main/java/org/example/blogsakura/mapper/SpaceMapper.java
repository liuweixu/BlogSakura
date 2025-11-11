package org.example.blogsakura.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.blogsakura.model.dto.space.Space;

/**
 * 空间 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface SpaceMapper extends BaseMapper<Space> {

    @Update("update space set totalSize = totalSize + #{size}, totalCount = totalCount + 1")
    public boolean incrementSpaceMySpace(@Param("id") Long id,
                                         @Param("size") Long size,
                                         @Param("count") Integer count);

    @Update("update space set totalSize = totalSize - #{size}, totalCount = totalCount - 1")
    public boolean decrementSpaceMySpace(@Param("id") Long id,
                                         @Param("size") Long size,
                                         @Param("count") Integer count);
}
