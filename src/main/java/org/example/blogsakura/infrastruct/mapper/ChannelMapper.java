package org.example.blogsakura.infrastruct.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.blogsakura.domain.blog.channel.entity.Channel;

/**
 * 频道表 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface ChannelMapper extends BaseMapper<Channel> {

    /**
     * 从频道名字获取频道
     *
     * @param channelName
     * @return
     */
    @Select("select * from channel where channel = #{channelName} and isDelete = 0")
    public Channel getChannelByChannelName(String channelName);

    /**
     * 计算某一个频道下的文章数量
     *
     * @param channelId
     * @return
     */
    @Select("select count(*) from article where channelId = #{channelId} and isDelete = 0")
    public long countArticlesByChannelId(@Param("channelId") long channelId);
}
