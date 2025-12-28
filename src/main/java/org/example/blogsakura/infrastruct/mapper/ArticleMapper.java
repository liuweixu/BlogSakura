package org.example.blogsakura.infrastruct.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.blogsakura.domain.blog.article.entity.Article;

/**
 * 文章表 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {


    /**
     * 从文章id获取相应文章的点赞数
     *
     * @param id
     * @return
     */
    @Select("select view from article where id = #{id} and isDelete = 0")
    public long getViewById(@Param("id") long id);

    /**
     * 更新点赞数
     *
     * @param id
     * @param count
     * @return
     */
    @Update("update article set view = #{count} where id = #{id} and isDelete = 0")
    public boolean updateViewById(@Param("id") long id, @Param("count") long count);

}
