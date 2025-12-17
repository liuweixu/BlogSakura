package org.example.blogsakura.common.aop;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.blogsakura.model.dto.operateLog.OperateLog;
import org.example.blogsakura.service.OperateLogService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Aspect
public class LogAspect {

    @Resource
    private OperateLogService operateLogService;

    private final Map<MethodSignature, Method> methodCache = new ConcurrentHashMap<>();

    /**
     * PointCut切入点
     * 通知+切入点=切面
     * JoinPoint是连接点，比如删除文章等业务方法就是JoinPoint.
     * Advice 通知，recordLog就是advice。
     */
    @Around("@annotation(org.example.blogsakura.common.aop.Log)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取当下操作时间
        LocalDateTime operateTime = LocalDateTime.now();

        // 获取操作方法名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 因为不同的方法名，signature也有所不同，故而针对这个可以用哈希表缓存。
        Method method;
        if (methodCache.containsKey(signature)) {
            method = methodCache.get(signature);
        } else {
            method = signature.getMethod();
            methodCache.put(signature, method);
        }
        Log annotation = method.getAnnotation(Log.class);
        String operateName = annotation != null ? annotation.value() : "";

        //开始计算时间
        long beginTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long costTime = endTime - beginTime;

        OperateLog operateLog = new OperateLog();
        if (operateName != null && !operateName.isEmpty()) {
            operateLog.setOperateName(operateName);
            operateLog.setOperateTime(operateTime);
            operateLog.setCostTime(costTime);
            operateLogService.save(operateLog);
            log.info("AOP操作记录日志：{}", operateLog);
        }

        return result;
    }
}
