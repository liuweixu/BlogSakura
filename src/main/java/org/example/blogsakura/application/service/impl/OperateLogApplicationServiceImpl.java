package org.example.blogsakura.application.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.domain.blog.operateLog.entity.OperateLog;
import org.example.blogsakura.domain.blog.operateLog.service.OperateLogDomainService;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.infrastruct.mapper.OperateLogMapper;
import org.example.blogsakura.application.service.OperateLogApplicationService;
import org.example.blogsakura.interfaces.dto.blog.operateLog.OperateLogQueryRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * 操作日志表 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
public class OperateLogApplicationServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements OperateLogApplicationService {

    @Resource
    private OperateLogDomainService operateLogDomainService;

    /**
     * 添加日志
     *
     * @param operateLog
     * @return
     */
    @Override
    public Boolean addOperateLog(OperateLog operateLog) {
        ThrowUtils.throwIf(operateLog == null, ErrorCode.PARAMS_ERROR);
        return operateLogDomainService.save(operateLog);
    }

    /**
     * 删除日志
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @Override
    public Boolean deleteOperateLog(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return operateLogDomainService.removeById(id);
    }

    /**
     * 更新日志
     *
     * @param operateLog
     * @return
     */
    @Override
    public Boolean updateOperateLog(OperateLog operateLog) {
        return operateLogDomainService.updateById(operateLog);
    }

    /**
     * 查看所有操作日志表
     *
     * @return
     */
    @Override
    public List<OperateLog> getOperateLogList() {
        return operateLogDomainService.list();
    }

    /**
     * 根据id获取日志信息
     *
     * @param id
     * @return
     */
    @Override
    public OperateLog getOperateLogById(BigInteger id) {
        ThrowUtils.throwIf(id.compareTo(BigInteger.ZERO) <= 0, ErrorCode.PARAMS_ERROR);
        OperateLog operateLog = operateLogDomainService.getById(id);
        ThrowUtils.throwIf(operateLog == null, ErrorCode.PARAMS_ERROR);
        return operateLog;
    }

    /**
     * 分页查询日志表
     *
     * @param operateLogQueryRequest
     * @return
     */
    @Override
    public Page<OperateLog> getOperateLogListByPage(OperateLogQueryRequest operateLogQueryRequest) {
        ThrowUtils.throwIf(operateLogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = operateLogQueryRequest.getCurrentPage();
        long pageSize = operateLogQueryRequest.getPageSize();
        Page<OperateLog> operateLogPage = operateLogDomainService.page(
                Page.of(currentPage, pageSize)
        );
        return operateLogPage;
    }

    /**
     * 清空操作日志
     *
     * @return
     */
    @Override
    public Boolean deleteOperateLogs() {
        return operateLogDomainService.deleteOperateLogs();
    }
}
