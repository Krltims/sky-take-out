package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws Exception {
        log.info("开始进行公共字段自动填充");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }

        Object o = args[0];
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if (operationType == OperationType.INSERT){
            Method setUpdateTime = o.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            Method setCreateTime = o.getClass().getDeclaredMethod("setCreateTime", LocalDate.class);
            Method setCreateUser = o.getClass().getDeclaredMethod("setCreateUser", Long.class);
            Method setUpdateUser = o.getClass().getDeclaredMethod("setUpdateUser", Long.class);
            setUpdateTime.invoke(o,now);
            setCreateTime.invoke(o,now);
            setCreateUser.invoke(o,currentId);
            setUpdateUser.invoke(o,currentId);
        }else{
            Method setUpdateTime = o.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            Method setUpdateUser = o.getClass().getDeclaredMethod("setUpdateUser", Long.class);
            setUpdateTime.invoke(o,now);
            setUpdateUser.invoke(o,currentId);
        }
    }
}
