package com.tg.locationsystem.config;


import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.entity.SysLog;
import com.tg.locationsystem.service.ISysLogService;
import com.xiaoleilu.hutool.system.SystemUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author hyy
 * @ Date2020/4/2
 */
@Aspect
@Component
public class SysAspect {

    @Autowired
    private ISysLogService sysLogService;

    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation(com.tg.locationsystem.config.Operation)")
    public void logPointCut(){
    }


    @AfterReturning("logPointCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        try {


        //保存日志
        SysLog sysLog = new SysLog();

        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //获取切入点所在的方法
        Method method = signature.getMethod();

        //获取操作
        Operation operation = method.getAnnotation(Operation.class);

        if (operation != null) {
            String value = operation.value();
            sysLog.setRequestdesc(value);//保存获取的操作
        }
        //获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();

        //获取请求的方法名
        String methodName = method.getName();

        //注入Syslog对象
        //username应从session里取出
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        //读取session中的用户
        Myuser user = (Myuser) session.getAttribute("user");
        sysLog.setUsername(user.getUsername());

        sysLog.setUserip(SystemUtil.getHostInfo().getAddress());
        sysLog.setRequestmethod(className + "." + methodName);
        //创建时间
        sysLog.setCreatetime(new Date());
        //顶级账号id
        if (user.getParentId()==0||"".equals(user.getParentId())||user.getParentId()==null){
            sysLog.setParentId(user.getId());

        }else {
            sysLog.setParentId(user.getParentId());
        }

        //调用service保存SysLog实体类到数据库
        sysLogService.insertSelective(sysLog);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
