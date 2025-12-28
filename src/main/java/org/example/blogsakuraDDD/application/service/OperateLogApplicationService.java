package org.example.blogsakuraDDD.application.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakuraDDD.domain.blog.operateLog.entity.OperateLog;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.interfaces.dto.blog.operateLog.OperateLogQueryRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigInteger;
import java.util.List;

/**
 * 操作日志表 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface OperateLogApplicationService extends IService<OperateLog> {

    /**
     * 添加日志
     *
     * @param operateLog
     * @return
     */
    Boolean addOperateLog(OperateLog operateLog);

    /**
     * 删除日志
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    Boolean deleteOperateLog(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 更新日志
     *
     * @param operateLog
     * @return
     */
    Boolean updateOperateLog(OperateLog operateLog);

    /**
     * 查看所有操作日志表
     *
     * @return
     */
    List<OperateLog> getOperateLogList();

    /**
     * 根据id获取日志信息
     *
     * @param id
     * @return
     */
    OperateLog getOperateLogById(BigInteger id);

    /**
     * 分页查询日志表
     *
     * @param operateLogQueryRequest
     * @return
     */
    Page<OperateLog> getOperateLogListByPage(@RequestBody OperateLogQueryRequest operateLogQueryRequest);

    /**
     * 清空操作日志
     *
     * @return
     */
    Boolean deleteOperateLogs();

}
