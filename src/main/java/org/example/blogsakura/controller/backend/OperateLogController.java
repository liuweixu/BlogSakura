package org.example.blogsakura.controller.backend;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.mapper.OperateLogMapper;
import org.example.blogsakura.model.dto.operateLog.OperateLogQueryRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.blogsakura.model.dto.operateLog.OperateLog;
import org.example.blogsakura.service.OperateLogService;
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
    private OperateLogService operateLogService;

    @Resource
    private OperateLogMapper operateLogMapper;

    /**
     * 保存操作日志表。
     *
     * @param operateLog 操作日志表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    public BaseResponse<Boolean> addOperateLog(@RequestBody OperateLog operateLog) {
        return ResultUtils.success(operateLogService.save(operateLog));
    }

    /**
     * 根据主键删除操作日志表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> removeOperateLogById(@PathVariable BigInteger id) {
        return ResultUtils.success(operateLogService.removeById(id));
    }

    /**
     * 根据主键更新操作日志表。
     *
     * @param operateLog 操作日志表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
    public BaseResponse<Boolean> updateOperateLog(@RequestBody OperateLog operateLog) {
        return ResultUtils.success(operateLogService.updateById(operateLog));
    }

    /**
     * 查询所有操作日志表。
     *
     * @return 所有数据
     */
    @GetMapping("/list")
    public BaseResponse<List<OperateLog>> getOperateLogList() {
        return ResultUtils.success(operateLogService.list());
    }

    /**
     * 根据主键获取操作日志表。
     *
     * @param id 操作日志表主键
     * @return 操作日志表详情
     */
    @GetMapping("/{id}")
    public BaseResponse<OperateLog> getOperateLogById(@PathVariable BigInteger id) {
        return ResultUtils.success(operateLogService.getById(id));
    }

    /**
     * 分页查询操作日志表。
     *
     * @param operateLogQueryRequest 分页请求
     * @return 分页对象
     */
    @PostMapping("list/page/vo")
    public BaseResponse<Page<OperateLog>> getOperateLogListByPage(@RequestBody OperateLogQueryRequest operateLogQueryRequest) {
        ThrowUtils.throwIf(operateLogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = operateLogQueryRequest.getCurrentPage();
        long pageSize = operateLogQueryRequest.getPageSize();
        Page<OperateLog> operateLogPage = operateLogService.page(
                Page.of(currentPage, pageSize)
        );
        return ResultUtils.success(operateLogPage);
    }

    /**
     * 清空操作日志
     *
     * @return
     */
    @DeleteMapping("/all")
    public BaseResponse<Boolean> deleteOperateLogs() {
        log.info("清空日志");
        return ResultUtils.success(operateLogMapper.deleteOperateAll());
    }
}
