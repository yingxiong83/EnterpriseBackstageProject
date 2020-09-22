package com.itheima.ssm.controller;

import com.itheima.ssm.domain.SysLog;
import com.itheima.ssm.service.SysLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 使用AOP记录访问日志
 */
//@Component
//@Aspect
public class LogAopCopy {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private SysLogService sysLogService;

    private Date visitTime;//访问时间(√)
    private String username;//操作者用户名(√)
    private String ip;//访问ip(√)
    private String url;//访问资源url(其实是uri，懒得改了）(√)
    private Long executionTime;//加载时间(√)
    private String method;//访问的方法的全限定类名(√）

    /**
     * 前置通知
     * @param jp 连接点
     */
    @Before("execution(* com.itheima.ssm.controller.*.*(..))")
    public void doBefore(JoinPoint jp) throws NoSuchMethodException {

        Class visitClass = null;//访问的类
        Method visitMethod = null;//访问的方法
        String methodName = null;//访问的方法名
        Object[] visitArgs = null;//访问的方法携带的参数
        Class[] parameterTypes = null;//访问的方法携带的参数的类型

        //SysLog:访问时间
        visitTime = new Date();

        /*
            获取访问的方法(Method)
         */
        visitClass = jp.getTarget().getClass();//访问的类
        methodName = jp.getSignature().getName();//访问的方法名
        visitArgs = jp.getArgs();//访问的参数
        /*if (visitArgs == null || visitArgs.length == 0) {
            //获取无参方法
            visitMethod = visitClass.getMethod(methodName);
        } else {
            //获取有参方法
            parameterTypes = new Class[visitArgs.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes[i] = visitArgs[i].getClass();
            }
            visitMethod = visitClass.getMethod(methodName, parameterTypes);
        }*/

        //SysLog:访问方法全限定类名
        method = visitClass.getName() + "." + methodName;

        /*
            SysLog:访问资源url
         */
        /*if (visitClass != null && method != null && visitClass != LogAop.class) {
            //1.获取类上的访问路径
            RequestMapping classAnnotation = (RequestMapping) visitClass.getAnnotation(RequestMapping.class);
            if (classAnnotation != null) {
                String[] classValue = classAnnotation.value();//类上的访问路径
                //2.获取方法上的访问路径
                RequestMapping methodAnnotation = visitMethod.getAnnotation(RequestMapping.class);
                if (methodAnnotation != null) {
                    String[] methodValue = methodAnnotation.value();
                    url = classValue[0] + methodValue[0];
                }
            }
        }*/
        //另一种获取url的方法
        url = request.getRequestURI();//此处带虚拟目录，上面通过反射方法不带，若不想带使用字符串的spit方法分割即可

        //SysLog:ip
        ip = request.getRemoteAddr();

        //Sys:username
        /*SecurityContext context = SecurityContextHolder.getContext();*/
        //另一种获取登录SecurityContext对象的方法
        SecurityContext context = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        User user = (User) context.getAuthentication().getPrincipal();
        username = user.getUsername();
    }

    /**
     * 后置通知
     * @param jp 连接点
     */
    @After("execution(* com.itheima.ssm.controller.*.*(..))")
    public void doAfter(JoinPoint jp) throws Exception {
        //SysLog:执行时长(加载时间）
        executionTime = new Date().getTime() - visitTime.getTime();

        /*
            将日志信息保存到数据库
         */
        SysLog sysLog = new SysLog();
        sysLog.setVisitTime(visitTime);
        sysLog.setUsername(username);
        sysLog.setIp(ip);
        sysLog.setUrl(url);
        sysLog.setExecutionTime(executionTime);
        sysLog.setMethod(method);
        sysLogService.save(sysLog);
    }

}
