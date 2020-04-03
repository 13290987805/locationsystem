package com.tg.locationsystem.config;

import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.pojo.ResultBean;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/28
 */
@ControllerAdvice
public class ShiroException {
      @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public ResultBean handleShiroException(Exception ex) {

          ResultBean resultBean = new ResultBean();
          resultBean.setCode(-1);
          resultBean.setMsg("没有权限！");
          List<Myuser> list = new ArrayList<>();
          resultBean.setData(list);
          resultBean.setSize(list.size());
          return resultBean;
    }

    @ResponseBody
    @ExceptionHandler(AuthorizationException.class)
    public ResultBean AuthorizationException(Exception ex) {
        ResultBean resultBean = new ResultBean();
        resultBean.setCode(-5);
        resultBean.setMsg("未进行登录认证！");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}
