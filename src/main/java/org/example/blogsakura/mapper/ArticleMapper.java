package org.example.blogsakura.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.blogsakura.model.dto.article.Article;

/**
 * 文章表 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("select count(*) from article where channelId = #{channelId}")
    public long countArticlesByChannelId(@Param("channelId") long channelId);

}
