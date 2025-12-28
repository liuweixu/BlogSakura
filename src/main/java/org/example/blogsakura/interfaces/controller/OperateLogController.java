package org.example.blogsakura.interfaces.controller;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.infrastruct.common.BaseResponse;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.common.ResultUtils;
import org.example.blogsakura.interfaces.dto.blog.operateLog.OperateLogQueryRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.example.blogsakura.domain.blog.operateLog.entity.OperateLog;
import org.example.blogsakura.application.service.OperateLogApplicationService;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

/**
 * 操作日志表 控制层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/backend/logging")
@Slf4j
public class OperateLogController {

    @Resource
    private OperateLogApplicationService operateLogApplicationService;

    /**
     * 保存操作日志表。
     *
     * @param operateLog 操作日志表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    public BaseResponse<Boolean> addOperateLog(@RequestBody OperateLog operateLog) {
        return ResultUtils.success(operateLogApplicationService.addOperateLog(operateLog));
    }

    /**
     * 删除操作日志表。
     *
     * @param deleteRequest 根据删除请求删除日志表
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
    public BaseResponse<Boolean> deleteOperateLog(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return ResultUtils.success(operateLogApplicationService.deleteOperateLog(deleteRequest, request));
    }

    /**
     * 根据主键更新操作日志表。
     *
     * @param operateLog 操作日志表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
    public BaseResponse<Boolean> updateOperateLog(@RequestBody OperateLog operateLog) {
        return ResultUtils.success(operateLogApplicationService.updateOperateLog(operateLog));
    }

    /**
     * 查询所有操作日志表。
     *
     * @return 所有数据
     */
    @GetMapping("/list")
    public BaseResponse<List<OperateLog>> getOperateLogList() {
        return ResultUtils.success(operateLogApplicationService.getOperateLogList());
    }

    /**
     * 根据主键获取操作日志表。
     *
     * @param id 操作日志表主键
     * @return 操作日志表详情
     */
    @GetMapping("/{id}")
    public BaseResponse<OperateLog> getOperateLogById(@PathVariable BigInteger id) {
        return ResultUtils.success(operateLogApplicationService.getOperateLogById(id));
    }

    /**
     * 分页查询操作日志表。
     *
     * @param operateLogQueryRequest 分页请求
     * @return 分页对象
     */
    @PostMapping("list/page/vo")
    public BaseResponse<Page<OperateLog>> getOperateLogListByPage(@RequestBody OperateLogQueryRequest operateLogQueryRequest) {
        return ResultUtils.success(operateLogApplicationService.getOperateLogListByPage(operateLogQueryRequest));
    }

    /**
     * 清空操作日志
     *
     * @return
     */
    @DeleteMapping("/all")
    public BaseResponse<Boolean> deleteOperateLogs() {
        return ResultUtils.success(operateLogApplicationService.deleteOperateLogs());
    }
}
