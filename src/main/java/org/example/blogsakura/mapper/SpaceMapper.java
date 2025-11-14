package org.example.blogsakura.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.blogsakura.model.dto.space.Space;
import org.example.blogsakura.model.vo.space.SpaceVO;

import java.util.List;

/**
 * 空间 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface SpaceMapper extends BaseMapper<Space> {

    @Update("update space set totalSize = totalSize + #{size}, totalCount = totalCount + 1 where isDelete = 0")
    public boolean incrementSpaceMySpace(@Param("id") Long id,
                                         @Param("size") Long size,
                                         @Param("count") Integer count);

    @Update("update space set totalSize = totalSize - #{size}, totalCount = totalCount - 1 where isDelete = 0")
    public boolean decrementSpaceMySpace(@Param("id") Long id,
                                         @Param("size") Long size,
                                         @Param("count") Integer count);

    /**
     * 从用户id获取相应的私有空间（管理员可以有多个，用户只有一个）
     *
     * @param userId
     * @return
     */
    @Select("select * from space where userId = #{userId} and spaceType = #{spaceType} and isDelete = 0")
    public List<Space> getSpaceListByUserId(@Param("userId") long userId,
                                            @Param("spaceType") int spaceType);
}
