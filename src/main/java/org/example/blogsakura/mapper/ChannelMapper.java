package org.example.blogsakura.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.blogsakura.pojo.Channel;

import java.util.List;

@Mapper
public interface ChannelMapper {

    @Select("select * from channel")
    public List<Channel> getChannels();

    @Select("select id from channel where name = #{name}")
    public Long getChannelIdByName(String name);

    @Select("select * from channel where id = #{id}")
    public Channel getChannelById(Long id);
}
