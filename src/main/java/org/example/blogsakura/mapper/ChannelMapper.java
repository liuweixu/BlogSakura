package org.example.blogsakura.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.blogsakura.model.dto.channel.Channel;

/**
 * 频道表 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface ChannelMapper extends BaseMapper<Channel> {

    @Select("select * from channel where channel = #{channelName} and isDelete = 0")
    public Channel getChannelByChannelName(String channelName);
}
