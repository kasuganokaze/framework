package com.web.aop;

import org.kaze.framework.aop.annotation.After;
import org.kaze.framework.aop.annotation.Around;
import org.kaze.framework.aop.annotation.Before;
import org.kaze.framework.aop.bean.JoinPoint;
import org.kaze.framework.aop.bean.ProceedingJoinPoint;

//@Component
//@Aspect("com.web.controller.*")
public class TestAspect {

    @Before
    public void before(JoinPoint joinPoint) {
        System.out.println("1");
    }

    @Around
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("2");
        Object object = joinPoint.proceed();
        System.out.println("3");
        return object;
    }

    @After
    public void after(JoinPoint joinPoint) {
        System.out.println("4");
    }

}
