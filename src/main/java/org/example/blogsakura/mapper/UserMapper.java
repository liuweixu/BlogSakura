package org.example.blogsakura.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.blogsakura.pojo.User;

@Mapper
public interface UserMapper {

    @Select("select * from user where mobile = #{mobile} and code = #{code}")
    public User getUserByMobile(User user);
}
