package com.chat.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

//@Configuration
//@Aspect
public class ControllerAOP {


//    @Around("execution (* com.chat.controller..*.*(..))")
    public Object testAop(ProceedingJoinPoint pro) throws Throwable {

        System.out.println("=====================");
        //获取response
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //核心设置
        response.setHeader("Access-Control-Allow-Origin", "*");

        //执行调用的方法
        Object proceed = pro.proceed();
        return proceed;

    }
}
