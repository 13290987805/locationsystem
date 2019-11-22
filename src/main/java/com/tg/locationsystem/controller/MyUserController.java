package com.tg.locationsystem.controller;


import com.tg.locationsystem.entity.EleCallSet;
import com.tg.locationsystem.entity.Frence;
import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.pojo.UpdatePassword;
import com.tg.locationsystem.pojo.User;
import com.tg.locationsystem.service.IEleCallSetService;
import com.tg.locationsystem.service.IMyUserService;
import com.tg.locationsystem.utils.BASE64;
import com.tg.locationsystem.utils.SystemMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Controller
@RequestMapping("myuser")
public class MyUserController {
    @Autowired
    private IMyUserService myUserService;
    @Autowired
    private IEleCallSetService eleCallSetService;

    /*
    * 登录
    * */
    @RequestMapping(value = "Login",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultBean shopuser(@Valid User user, BindingResult result,
                               HttpServletRequest request){
        ResultBean resultBean;
        if (result.hasErrors()) {
            List<String> errorlist=new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = field+":"+fieldError.getDefaultMessage();
                //System.out.println(field + ":" + message);
                errorlist.add(message);
            });
            resultBean =new ResultBean();
            resultBean.setCode(2);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        Myuser myuser=myUserService.getUserByName(user.getUsername());
        if (null==myuser){
            resultBean = new ResultBean();
            resultBean.setCode(3);
            resultBean.setMsg("该账户不存在");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        try {
            String pass = Base64.getEncoder().encodeToString(user.getPassword().getBytes());
            //System.out.println(pass+":"+pass.length());
            //System.out.println(myuser.getPassword()+":"+myuser.getPassword().length());
            if (!myuser.getPassword().equals(pass)){
                resultBean = new ResultBean();
                resultBean.setCode(4);
                resultBean.setMsg("密码错误");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }

            //将登陆凭证保存到session中
            request.getSession().setAttribute("user",myuser);

      /*      Map<String, Integer> usermap = SystemMap.getUsermap();
            for (String s : usermap.keySet()) {
                System.out.println("usermapkey:"+s+"----"+"usermapvalue:"+usermap.get(s));
            }
            Map<Integer, List<Frence>> frencemap = SystemMap.getFrencemap();
            //System.out.println("围栏集合:"+frencemap.get(myuser.getId()));
            for (Integer integer : frencemap.keySet()) {
                List<Frence> frenceList = frencemap.get(integer);
                for (Frence frence : frenceList) {
                    System.out.println("frencemapkey:"+integer+"----"+"frencemapvalue:"+frence.getName());
                }
            }*/

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("登录操作成功");
            List<Myuser> list = new ArrayList<>();

            //出于安全,将密码置空
            myuser.setPassword(null);

            list.add(myuser);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;

        } catch (Exception e) {
            resultBean = new ResultBean();
            resultBean.setCode(4);
            resultBean.setMsg("密码错误:"+e.getMessage());
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }

    /*
    * 修改账号密码
    * */
    @RequestMapping(value = "updatePassword",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultBean updatePassword(@Valid UpdatePassword updatePassword, BindingResult result,
                                     HttpServletRequest request){

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
        if (result.hasErrors()) {
            List<String> errorlist=new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = field+":"+fieldError.getDefaultMessage();
                //System.out.println(field + ":" + message);
                errorlist.add(message);
            });
            resultBean =new ResultBean();
            resultBean.setCode(2);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }
        Myuser loginUser = myUserService.selectByPrimaryKey(user.getId());

        if (!loginUser.getPassword().equals(updatePassword.getOldPassword())){
            resultBean =new ResultBean();
            resultBean.setCode(66);
            resultBean.setMsg("密码错误");
            List list=new ArrayList();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        loginUser.setPassword(updatePassword.getNewPassword());
        int update = myUserService.updateByPrimaryKeySelective(loginUser);
        if (update>0){
            resultBean =new ResultBean();
            resultBean.setCode(67);
            resultBean.setMsg("密码修改成功");
            //设置session过期
            request.getSession().setAttribute("user",null);

            List<Myuser> list=new ArrayList();
            list.add(user);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean =new ResultBean();
            resultBean.setCode(68);
            resultBean.setMsg("密码修改失败");
            List<Myuser> list=new ArrayList();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
    * 管理员添加用户
    *
    * */
    @RequestMapping(value = "AddUser",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddUser(@Valid Myuser myuser, BindingResult result,
                                HttpServletRequest request){

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
        if (result.hasErrors()) {
            List<String> errorlist=new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = field+":"+fieldError.getDefaultMessage();
                //System.out.println(field + ":" + message);
                errorlist.add(message);
            });
            resultBean =new ResultBean();
            resultBean.setCode(2);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        if (!"0".equals(user.getCreateUser())){
            resultBean =new ResultBean();
            resultBean.setCode(69);
            resultBean.setMsg("权限不足,无法添加用户");
            List<Myuser> list=new ArrayList<>();
            list.add(user);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Myuser userByName = myUserService.getUserByName(myuser.getUsername());
        if (userByName!=null){
            resultBean = new ResultBean();
            resultBean.setCode(72);
            resultBean.setMsg("该用户已经存在,无法添加");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        boolean matches = myuser.getPhonenumber().matches("^[1][3,4,5,7,8][0-9]{9}$");
        if (!matches){
            resultBean = new ResultBean();
            resultBean.setCode(73);
            resultBean.setMsg("手机号码格式不正确");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        try {
            String pass = BASE64.encryptBASE64(myuser.getPassword().getBytes());
            myuser.setPassword(pass);
            myuser.setCreateUser("1");
            int insert = myUserService.insertSelective(myuser);
            EleCallSet eleCallSet=new EleCallSet();
            eleCallSet.setSetSwitch("0");
            eleCallSet.setTimeInterval(15);
            eleCallSet.setUpdateTime(new Date());
            eleCallSet.setUserId(user.getId());
            int i = eleCallSetService.insertSelective(eleCallSet);
            if (insert>0&&i>0){
                resultBean = new ResultBean();
                resultBean.setCode(70);
                resultBean.setMsg("用户添加成功");
                List<Myuser> list = new ArrayList<>();
                list.add(myuser);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(71);
                resultBean.setMsg("用户添加失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        } catch (Exception e) {
            resultBean = new ResultBean();
            resultBean.setCode(71);
            resultBean.setMsg("用户添加失败:"+e.getMessage());
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }
    @RequestMapping(value = "toLogin",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean toLogin(HttpServletRequest request){

        ResultBean resultBean = new ResultBean();
        resultBean.setCode(5);
        resultBean.setMsg("还未登录");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}

