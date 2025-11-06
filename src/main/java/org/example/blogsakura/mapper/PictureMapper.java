package org.example.blogsakura.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.blogsakura.model.dto.picture.Picture;

/**
 * 图片管理 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface PictureMapper extends BaseMapper<Picture> {

}
