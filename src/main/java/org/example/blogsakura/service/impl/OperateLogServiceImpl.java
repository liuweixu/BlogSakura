package org.example.blogsakura.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.blogsakura.model.dto.operateLog.OperateLog;
import org.example.blogsakura.mapper.OperateLogMapper;
import org.example.blogsakura.service.OperateLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志表 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements OperateLogService {

}
