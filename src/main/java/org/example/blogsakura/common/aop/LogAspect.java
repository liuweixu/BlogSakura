package org.example.blogsakura.common.aop;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.blogsakura.model.dto.operateLog.OperateLog;
import org.example.blogsakura.service.OperateLogService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@Aspect
public class LogAspect {

    @Resource
    private OperateLogService operateLogService;

    /**
     * PointCut切入点
     * 通知+切入点=切面
     */
    @Around("@annotation(org.example.blogsakura.common.aop.Log)")
    /**
     * JoinPoint是连接点，比如删除文章等业务方法就是JoinPoint.
     * Advice 通知，recordLog就是advice。
     */
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取雪花孙发

        // 获取当下操作时间
        LocalDateTime operateTime = LocalDateTime.now();

        // 获取操作方法名
        String methodName = joinPoint.getSignature().getName();
        String operateName = "";
        if (methodName.equals("getArticleList")) {
            operateName = "获取文章列表（后端）";
        } else if (methodName.equals("getHomeArticleList")) {
            operateName = "获取文章列表（前端）";
        } else if (methodName.equals("deleteArticleById")) {
            operateName = "删除文章";
        } else if (methodName.equals("getArticleById")) {
            operateName = "后端文章信息回填";
        } else if (methodName.equals("getHomeArticleById")) {
            operateName = "前端显示文章信息";
        } else if (methodName.equals("insertArticle")) {
            operateName = "新增文章";
        } else if (methodName.equals("updateArticle")) {
            operateName = "编辑文章";
        } else if (methodName.equals("getChannels")) {
            operateName = "获取频道列表信息";
        } else if (methodName.equals("getChannelById")) {
            operateName = "按照id获取频道";
        } else if (methodName.equals("login")) {
            operateName = "登录";
        } else if (methodName.equals("deleteOperateLogs")) {
            operateName = "清空日志";
        } else if (methodName.equals("getOperateLogs")) {
            operateName = "获取日志列表";
        } else if (methodName.equals("handleNotLogin")) {
            operateName = "未登录异常";
        }

        //开始计算时间
        long beginTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long costTime = endTime - beginTime;

        OperateLog operateLog = new OperateLog();
        operateLog.setOperateName(operateName);
        operateLog.setOperateTime(operateTime);
        operateLog.setCostTime(costTime);
        operateLogService.save(operateLog);
        log.info("AOP操作记录日志：{}", operateLog);

        return result;
    }
}
