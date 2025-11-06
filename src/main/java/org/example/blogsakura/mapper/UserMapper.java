package org.example.blogsakura.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.vo.user.UserVO;

import java.util.List;

/**
 * 用户 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("update user set userPassword = #{userPassword}, userName = #{userName}, userRole = #{userRole}, isDelete = #{isDelete} where id = #{id}")
    public boolean updateUserWithoutLogicDelete(User user);

    @Select("select * from user where userAccount = #{userAccount}")
    public User selectUserWithoutLogicDelete(String userAccount);

    @Select("select * from user where isDelete = 0 limit #{offset}, #{pageSize}")
    public List<User> listUserVOByPage(@Param("offset") long offset, @Param("pageSize") long pageSize);
}
