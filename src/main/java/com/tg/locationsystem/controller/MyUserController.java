package com.tg.locationsystem.controller;


import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.entity.EleCallSet;
import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.entity.MyuserRole;
import com.tg.locationsystem.entity.Role;
import com.tg.locationsystem.pojo.AddUser;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.pojo.UpdatePassword;
import com.tg.locationsystem.pojo.User;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.UploadFileUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
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
    @Autowired
    private IMyuserRoleService myuserRoleService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRolePermissionService rolePermissionService;

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
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }
        //System.out.println(TestUtil.getGoP(request));
        Myuser myuser=myUserService.getUserByName(user.getUsername());
        if (null==myuser){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
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
                resultBean.setCode(-1);
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
            //添加用户认证信息
            Subject subject = SecurityUtils.getSubject();
            //密码加密
            String pwd = Base64.getEncoder().encodeToString(user.getPassword().getBytes());
            user.setPassword(pwd);
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
                    user.getUsername(),
                    user.getPassword()
            );
            try {
                //进行验证，这里可以捕获异常，然后返回对应信息
                subject.login(usernamePasswordToken);
        //            subject.checkRole("admin");
        //            subject.checkPermissions("query", "add");
            } catch (AuthenticationException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("账号或密码错误！");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            } catch (AuthorizationException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("没有权限！");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            if (myuser.getParentId()!=0&&myuser.getParentId()!=null&&!"".equals(myuser.getParentId())){
                myuser.setId(myuser.getParentId());
            }

            //将登陆凭证保存到session中
            request.getSession().setAttribute("user",myuser);

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("登录操作成功");
            List<Myuser> list = new ArrayList<>();

            //出于安全,将密码置空
            myuser.setPassword(null);

            //给系统默认名称
            if (myuser.getSystemName()==null||"".equals(myuser.getSystemName())){
                myuser.setSystemName("安全监护系统");
            }

            list.add(myuser);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;

        } catch (Exception e) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("密码错误:"+e.getMessage());
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }


    /*
    * 编辑登录用户资料
    * */
    @RequestMapping(value = "updateUser",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultBean updateUser(@Valid Myuser myuser, BindingResult result,
                                     HttpServletRequest request,@RequestParam(value="logoData",required=false)MultipartFile file){
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
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        Myuser sqlUser = myUserService.selectByPrimaryKey(user.getId());
        if (sqlUser==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该登录用户不存在");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        myuser.setId(user.getId());
        myuser.setCreateUser(user.getCreateUser());
        //设置图片路径
        myuser.setLogo(user.getLogo());
        //获取文件名加后缀
        if (file!=null){
            //保存图片的路径
            String s = System.getProperty("user.dir");//C:\whzy\locationsystem
            String filePath =s.split(":")[0]+":\\img";
            UploadFileUtil.isChartPathExist(filePath);
            //获取原始图片的拓展名
            String originalFilename = file.getOriginalFilename();
            //新的文件名字
            String newFileName = UUID.randomUUID()+originalFilename;
            //封装上传文件位置的全路径
            File targetFile = new File(filePath,newFileName);
            //把本地文件上传到封装上传文件位置的全路径
            try {
                file.transferTo(targetFile);
                myuser.setLogo(targetFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("上传logo失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        int update = myUserService.updateByPrimaryKeySelective(myuser);
        if (update>0){
            //将登陆凭证保存到session中
            request.getSession().setAttribute("user",myuser);


            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List<Myuser> list = new ArrayList<>();
            list.add(myuser);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("操作失败:");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }
    /*
     * 编辑登录用户资料
     * 只上传logo
     * */
    @RequestMapping(value = "uploadLogo",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultBean uploadLogo(HttpServletRequest request,
                                 @RequestParam(value="logo",required=false)MultipartFile file){
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
       // System.out.println("上传logo:"+request.getRequestURI());
        if (file==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("logo不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //设置图片路径
        //获取文件名加后缀
        if (file!=null){
            //保存图片的路径
            String s = System.getProperty("user.dir");//C:\whzy\locationsystem
            String filePath =s.split(":")[0]+":\\img";
            UploadFileUtil.isChartPathExist(filePath);
            //获取原始图片的拓展名
            String originalFilename = file.getOriginalFilename();
            //新的文件名字
            String newFileName = UUID.randomUUID()+originalFilename;
            //封装上传文件位置的全路径
            File targetFile = new File(filePath,newFileName);
            //把本地文件上传到封装上传文件位置的全路径
            try {
                file.transferTo(targetFile);
                user.setLogo(targetFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("上传logo失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }

        }

        int update = myUserService.updateByPrimaryKeySelective(user);
        if (update>0){
            //将登陆凭证保存到session中
            request.getSession().setAttribute("user",user);

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List<Myuser> list = new ArrayList<>();
            list.add(user);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("操作失败:");
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
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }
        Myuser loginUser = myUserService.selectByPrimaryKey(user.getId());
        String pass = Base64.getEncoder().encodeToString(updatePassword.getOldPassword().getBytes());
        if (!loginUser.getPassword().equals(pass)){
            resultBean =new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("密码错误");
            List list=new ArrayList();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        String Newpass = Base64.getEncoder().encodeToString(updatePassword.getNewPassword().getBytes());
        loginUser.setPassword(Newpass);
        int update = myUserService.updateByPrimaryKeySelective(loginUser);
        if (update>0){
            resultBean =new ResultBean();
            resultBean.setCode(1);
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
            resultBean.setCode(-1);
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
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        if (!"0".equals(user.getCreateUser())){
            resultBean =new ResultBean();
            resultBean.setCode(-1);
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
            resultBean.setCode(-1);
            resultBean.setMsg("该用户已经存在,无法添加");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        boolean matches = myuser.getPhonenumber().matches("^[1][3,4,5,7,8][0-9]{9}$");
        if (!matches){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("手机号码格式不正确");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        try {
            //String pass = BASE64.encryptBASE64(myuser.getPassword().getBytes());
            String pass = Base64.getEncoder().encodeToString(user.getPassword().getBytes());
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
                resultBean.setCode(1);
                resultBean.setMsg("用户添加成功");
                List<Myuser> list = new ArrayList<>();
                list.add(myuser);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("用户添加失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        } catch (Exception e) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
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

    /*
     * 测试在别的项目用URL网络资源请求该接口
     *
     * */
    @RequestMapping(value = "Login2",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultBean shopuser(@RequestBody User user,
                               HttpServletRequest request){
        System.out.println("原始"+user.toString());
        Myuser myuser = myUserService.getUserByName(user.getUsername());

        if (myuser!=null){
            ResultBean resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("ok");
            List<Myuser> list = new ArrayList<>();
            list.add(myuser);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            ResultBean resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("ok");
            List<User> list = new ArrayList<>();
            list.add(user);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }

    /*
    * 退出登录
    * */
    @RequestMapping(value = "LoginOut",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultBean LoginOut(HttpServletRequest request){
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("退出登录成功,还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        request.getSession().setAttribute("user",null);


        user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("退出登录成功,还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("退出登录失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }

    /*
    * 账号,密码.角色 添加账号
    *
    *
    * */
    @RequestMapping(value = "AddUserbByRole",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddUserbByRole(@Valid AddUser addUser, BindingResult result,
                                     HttpServletRequest request) {

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
        if (result.hasErrors()) {
            List<String> errorlist = new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = field + ":" + fieldError.getDefaultMessage();
                //System.out.println(field + ":" + message);
                errorlist.add(message);
            });
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        if (!"0".equals(user.getCreateUser())) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("权限不足,无法添加用户");
            List<Myuser> list = new ArrayList<>();
            list.add(user);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Myuser userByName = myUserService.getUserByName(addUser.getUsername());
        if (userByName != null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该用户已经存在,无法添加");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        try {
            //String pass = BASE64.encryptBASE64(myuser.getPassword().getBytes());
            String pass = Base64.getEncoder().encodeToString(addUser.getPassword().getBytes());
            Myuser myuser=new Myuser();
            myuser.setUsername(addUser.getUsername());
            myuser.setPassword(pass);
            myuser.setParentId(user.getId());
            myuser.setCreateUser("1");

            //更新角色用户表
            MyuserRole myuserRole=myuserRoleService.getMyuserRoleByRoleId(addUser.getRoleId());
            myuserRole.setUserId(myuser.getId());
            myuserRoleService.updateByPrimaryKey(myuserRole);

            int insert = myUserService.insertSelective(myuser);

            if (insert>0){
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("用户添加成功");
                List<Myuser> list = new ArrayList<>();
                list.add(myuser);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("用户添加失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        } catch (Exception e) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("用户添加失败:"+e.getMessage());
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
     * 查看子账号
     *
     * */
    @RequestMapping(value = "getSonUser",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSonUser(HttpServletRequest request,
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

        PageInfo<Myuser> page=myUserService.getUsersByUserId(user.getId(),pageIndex,pageSize);

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list=new ArrayList<>();
        list.add(page);
        resultBean.setData(list);
        resultBean.setSize(page.getSize());
        return resultBean;
    }

    /*
    * 搜索账号
    * 账号,角色模糊搜索
    * */
    @RequestMapping(value = "getSonUserByMsg",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSonUserByMsg(HttpServletRequest request,
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

        if (msg==null||"".equals(msg)){
            PageInfo<Myuser> page=myUserService.getUsersByUserId(user.getId(),pageIndex,pageSize);

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List list=new ArrayList<>();
            list.add(page);
            resultBean.setData(list);
            resultBean.setSize(page.getSize());
            return resultBean;
        }
        //所有账号
        PageInfo<Myuser> page=myUserService.getUsersByUserId(user.getId(),pageIndex,pageSize);

       Set<Integer> list=new TreeSet<>();
        for (Myuser myuser : page.getList()) {
            if (myuser.getUsername().contains(msg)){
                list.add(myuser.getId());
            }
            List<MyuserRole> myuserRolelist=myuserRoleService.getmyuserRoleByUserId(myuser.getId());

            for (MyuserRole myuserRole : myuserRolelist) {
                Role role = roleService.selectByPrimaryKey(Integer.parseInt(myuserRole.getRoleId()));
                if (role.getRoleName().contains(msg)){
                        list.add(myuser.getId());
                    }

            }
        }
        //System.out.println("结果:"+list.size());
        List<Myuser> resultList=new ArrayList<>();
        for (Integer integer : list) {
            Myuser myuser = myUserService.selectByPrimaryKey(integer);
            resultList.add(myuser);
        }


        page.setList(resultList);
        page.setTotal(resultList.size());


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list1=new ArrayList<>();
        list1.add(page);
        resultBean.setData(list1);
        resultBean.setSize(resultList.size());
        return resultBean;
    }

    /*
    * 删除账号
    * */
    @RequestMapping(value = "deleteUser",method = {RequestMethod.POST})
    @ResponseBody
    public ResultBean deleteUser(@RequestParam("") Integer UserId, HttpServletRequest request
    ) {
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
        if (UserId == null || "".equals(UserId)) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("参数有误,账号id不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //
        //是否存在该账号
        Myuser myuser = myUserService.selectByPrimaryKey(UserId);
        if (myuser == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该角色不存在");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //是否登录账号子账号
        if (myuser.getParentId()!=user.getId()){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该账号不是您的子账号");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //删除该账号
        myUserService.deleteByPrimaryKey(UserId);
        //该账户的所有账户,角色
        List<MyuserRole> myuserRoles = myuserRoleService.getmyuserRoleByUserId(UserId);
        for (MyuserRole myuserRole : myuserRoles) {
            //删除角色
            roleService.deleteByPrimaryKey(Integer.parseInt(myuserRole.getRoleId()));
            //删除权限
            rolePermissionService.deleteByRoleId(Integer.parseInt(myuserRole.getRoleId()));
            //删除角色,权限
            myuserRoleService.deleteByPrimaryKey(myuserRole.getId());
        }
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List<Myuser> list=new ArrayList<>();
        list.add(myuser);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }



    /*
     * 修改账号
     * */
    @RequestMapping(value = "uodataUser",method = {RequestMethod.POST})
    @ResponseBody
    public ResultBean uodataUser(@Valid Myuser updataUser, BindingResult result,
                                 HttpServletRequest request) {

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
        if (result.hasErrors()) {
            List<String> errorlist = new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = field + ":" + fieldError.getDefaultMessage();
                //System.out.println(field + ":" + message);
                errorlist.add(message);
            });
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        Myuser userByName = myUserService.getUserByName(updataUser.getUsername());
        if (userByName == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该用户不存在,无法修改");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        MyuserRole myuserRole=myuserRoleService.getmyuserRoleByUserId(userByName.getId()).get(0);
        myuserRole.setRoleId(userByName.getRoles().iterator().next().getRoleId());


        try {
            int update = myuserRoleService.updateByPrimaryKey(myuserRole);

            if (update > 0){
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("用户修改成功");
                List<MyuserRole> list = new ArrayList<>();
                list.add(myuserRole);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("用户修改失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        } catch (Exception e) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("用户修改失败:"+e.getMessage());
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
}

