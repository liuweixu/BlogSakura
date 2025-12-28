package org.example.blogsakura.domain.blog.operateLog.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.blogsakura.domain.blog.operateLog.entity.OperateLog;
import org.example.blogsakura.domain.blog.operateLog.service.OperateLogDomainService;
import org.example.blogsakura.infrastruct.mapper.OperateLogMapper;
import org.springframework.stereotype.Service;

/**
 * 操作日志表 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
public class OperateLogDomainServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements OperateLogDomainService {

    @Resource
    private OperateLogMapper operateLogMapper;

    /**
     * 清空操作日志
     *
     * @return
     */
    @Override
    public Boolean deleteOperateLogs() {
        return operateLogMapper.deleteOperateAll();
    }
}
