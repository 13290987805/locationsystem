package com.tg.locationsystem;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hyy
 * @ Date2020/3/27
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

public class ShiroTest {
    private SimpleAccountRealm simpleAccountRealm=
            new SimpleAccountRealm();
    @Before
    public void addUser() {
        //这里给kobey授予了admin，user两种角色,为什么授权会授予角色呢？因为角色是权力的集合，自己体会
        simpleAccountRealm.addAccount("Kobey","123","admin","user");
    }

    @Test
    public void test(){
        //构建securityManager环境
        DefaultSecurityManager defaultSecurityManager=
                new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject=SecurityUtils.getSubject();

        UsernamePasswordToken token=
                new UsernamePasswordToken("Kobey","123");

        subject.login(token);
        System.out.println("isAuthenticated:"+subject.isAuthenticated());
        //判断kebey主体是否具有这两种角色，如果你将user改变成user1试试，会抛出未授权异常，自己尝试
        subject.checkRoles("admin","user");
    }

}
