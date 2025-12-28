package org.example.blogsakuraDDD.infrastruct.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.blogsakuraDDD.domain.picture.entity.Picture;

/**
 * 图片管理 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface PictureMapper extends BaseMapper<Picture> {

    /**
     * 彻底删除图片（需要图片回收箱确认删除）
     *
     * @param id
     * @return
     */
    @Delete("delete from picture where id = #{id}")
    public boolean deleteDeepPicture(@Param("id") Long id);
}
