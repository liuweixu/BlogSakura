package org.example.blogsakuraDDD.infrastruct.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.blogsakuraDDD.domain.blog.operateLog.entity.OperateLog;

/**
 * 操作日志表 映射层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Mapper
public interface OperateLogMapper extends BaseMapper<OperateLog> {

    @Update("update operate_log set isDelete = 1 where isDelete = 0")
    public boolean deleteOperateAll();
}
