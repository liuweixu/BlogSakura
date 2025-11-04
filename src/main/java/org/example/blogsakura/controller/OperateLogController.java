package org.example.blogsakura.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.pojo.OperateLog;
import org.example.blogsakura.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
class OperateLogController {

    @Autowired
    private OperateLogService operateLogService;

    @DeleteMapping("/backend/logging")
    public void deleteOperateLogs() {
        log.info("清空日志");
        operateLogService.deleteOperateLogs();
    }

    @GetMapping("/backend/logging")
    public List<OperateLog> getOperateLogs() {
        log.info("获取日志列表");
        return operateLogService.getOperateLogs();
    }
}
