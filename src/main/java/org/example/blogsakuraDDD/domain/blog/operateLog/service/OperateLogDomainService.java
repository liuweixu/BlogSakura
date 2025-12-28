package org.example.blogsakuraDDD.domain.blog.operateLog.service;

import com.mybatisflex.core.service.IService;
import org.example.blogsakuraDDD.domain.blog.operateLog.entity.OperateLog;

/**
 * 操作日志表 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface OperateLogDomainService extends IService<OperateLog> {
    /**
     * 清空操作日志
     *
     * @return
     */
    Boolean deleteOperateLogs();
}
