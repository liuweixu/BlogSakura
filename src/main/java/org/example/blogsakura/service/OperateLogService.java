package org.example.blogsakura.service;

import org.example.blogsakura.pojo.OperateLog;

import java.util.List;

public interface OperateLogService {
    void deleteOperateLogs();

    List<OperateLog> getOperateLogs();
}
