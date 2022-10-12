package ru.yandex.practicum.filmorate.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAudit {

    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.*.create*(..))")
    public void callPublicControllerCreate() { }

    @Before("callPublicControllerCreate()")
    public void beforeCallCreateMethod(JoinPoint jp) {
        String args = Arrays.toString(jp.getArgs());
        log.info("Request to create new " + args.substring(1, args.length() - 1));
    }

    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.*.delete*(..))")
    public void callPublicControllerDelete() { }

    @Before("callPublicControllerDelete()")
    public void beforeCallDeleteMethod(JoinPoint jp) {
        String args = Arrays.toString(jp.getArgs());
        log.info("Request to delete " + jp.getSignature().getName().substring(6) +
                " with parameters: " + args);
    }

    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.*.update*(..))")
    public void callPublicControllerUpdate() { }

    @Before("callPublicControllerUpdate()")
    public void beforeCallUpdateMethod(JoinPoint jp) {
        String args = Arrays.toString(jp.getArgs());
        log.info("Request to update " + args.substring(1, args.length() - 1));
    }

    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.*.add(..))")
    public void callPublicControllerAdd() { }

    @Before("callPublicControllerAdd()")
    public void beforeCallAddLikeMethod(JoinPoint jp) {
        String args = Arrays.toString(jp.getArgs());
        log.info("Request " + jp.getSignature().getName() + " with parameters: " + args);
    }
}
