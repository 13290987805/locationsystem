package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.entity.PersonType;
import com.tg.locationsystem.entity.SysLog;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.service.ISysLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2020/4/2
 */
@Controller
@RequestMapping("syslog")
public class SyslogController {
    @Autowired
    private ISysLogService sysLogService;

    /*
    * 查看系统日志
    * */
         @RequestMapping(value = "getSyslog",method = RequestMethod.GET)
         @ResponseBody
         public ResultBean getSyslog(HttpServletRequest request,
                                      @RequestParam(defaultValue = "1") Integer pageIndex,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
             ResultBean resultBean;
             Myuser user = (Myuser) request.getSession().getAttribute("user");
             //未登录
             if (user==null){
                 resultBean = new ResultBean();
                 resultBean.setCode(5);
                 resultBean.setMsg("还未登录");
                 List<Myuser> list = new ArrayList<>();
                 resultBean.setData(list);
                 resultBean.setSize(list.size());
                 return resultBean;
             }
             //是否顶级账号
             //顶级账号,查看自己及子账号记录
             //子账号,只看自己记录
             if (user.getParentId()==0||"".equals(user.getParentId())||user.getParentId()==null){
                 PageInfo<SysLog> pageInfo=sysLogService.getSyslogByPidPage(pageIndex,pageSize,user.getId());

                 resultBean = new ResultBean();
                 resultBean.setCode(1);
                 resultBean.setMsg("操作成功");
                 List list=new ArrayList<>();
                 list.add(pageInfo);
                 resultBean.setData(list);
                 resultBean.setSize(pageInfo.getSize());
                 return resultBean;
             }else {
                 PageInfo<SysLog> pageInfo=sysLogService.getSyslogByUsernamePage(pageIndex,pageSize,user.getUsername());

                 resultBean = new ResultBean();
                 resultBean.setCode(1);
                 resultBean.setMsg("操作成功");
                 List list=new ArrayList<>();
                 list.add(pageInfo);
                 resultBean.setData(list);
                 resultBean.setSize(pageInfo.getSize());
                 return resultBean;
             }

            }

    /*
     * 查询系统日志
     * */
    @RequestMapping(value = "getSyslogByMsg",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSyslogByMsg(HttpServletRequest request,
                                 @RequestParam(defaultValue = "") String msg,
                                @RequestParam(defaultValue = "1") Integer pageIndex,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user == null) {
            resultBean = new ResultBean();
            resultBean.setCode(5);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        if (msg == null || "".equals(msg)) {
            //是否顶级账号
            //顶级账号,查看自己及子账号记录
            //子账号,只看自己记录
            if (user.getParentId() == 0 || "".equals(user.getParentId()) || user.getParentId() == null) {
                PageInfo<SysLog> pageInfo = sysLogService.getSyslogByPidPage(pageIndex, pageSize, user.getId());

                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("操作成功");
                List list = new ArrayList<>();
                list.add(pageInfo);
                resultBean.setData(list);
                resultBean.setSize(pageInfo.getSize());
                return resultBean;
            } else {
                PageInfo<SysLog> pageInfo = sysLogService.getSyslogByUsernamePage(pageIndex, pageSize, user.getUsername());

                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("操作成功");
                List list = new ArrayList<>();
                list.add(pageInfo);
                resultBean.setData(list);
                resultBean.setSize(pageInfo.getSize());
                return resultBean;
            }
        }
        //是否顶级账号
        //顶级账号,查看自己及子账号记录
        //子账号,只看自己记录
        if (user.getParentId() == 0 || "".equals(user.getParentId()) || user.getParentId() == null) {
            PageInfo<SysLog> pageInfo = sysLogService.getSyslogByPidAndMsgPage(pageIndex, pageSize, user.getId(),msg);

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List list = new ArrayList<>();
            list.add(pageInfo);
            resultBean.setData(list);
            resultBean.setSize(pageInfo.getSize());
            return resultBean;
        } else {
            PageInfo<SysLog> pageInfo = sysLogService.getSyslogByUsernameAndMsgPage(pageIndex, pageSize, user.getUsername(),msg);

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List list = new ArrayList<>();
            list.add(pageInfo);
            resultBean.setData(list);
            resultBean.setSize(pageInfo.getSize());
            return resultBean;
        }
    }
}
