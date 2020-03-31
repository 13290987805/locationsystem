package com.tg.locationsystem.controller;

import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.pojo.ShiroVO;
import com.tg.locationsystem.service.IMyuserRoleService;
import com.tg.locationsystem.service.IPermissionService;
import com.tg.locationsystem.service.IRolePermissionService;
import com.tg.locationsystem.service.IRoleService;
import com.tg.locationsystem.utils.test.BuildTree;
import com.tg.locationsystem.utils.test.Test;
import com.tg.locationsystem.utils.test.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hyy
 * @ Date2020/3/30
 */
@Controller
@RequestMapping("shiro")
public class ShiroController {

    @Autowired
    private IPermissionService permissionService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRolePermissionService rolePermissionService;

    @Autowired
    private IMyuserRoleService myuserRoleService;
    /*
    * 查看所有权限
    * */
    @RequestMapping(value = "getPermission", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPermission(HttpServletRequest request) {
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
        List<Permission> permissionList=permissionService.getAllPermission();
        List<Tree<com.tg.locationsystem.utils.test.Test>> trees = new ArrayList<Tree<com.tg.locationsystem.utils.test.Test>>();
        for (Permission permission : permissionList) {
            Tree<Test> tree = new Tree<com.tg.locationsystem.utils.test.Test>();
            tree.setId(String.valueOf(permission.getId()));
            tree.setParentId(String.valueOf(permission.getParentId()));
            tree.setTitle(permission.getRemark());
            List<Map<String, Object>> lmp = new ArrayList<Map<String, Object>>();
            Map<String, Object> mp = new HashMap<String, Object>();
            /*mp.put("COSTDEVICE_NUMBER", "");
            mp.put("PRICE_PER", "");
            mp.put("ORDER_INDEX", "");
            mp.put("ADJUST_DATE", "");
            mp.put("IS_LEAF", "");*/
            lmp.add(mp);
            trees.add(tree);
        }
        Tree<com.tg.locationsystem.utils.test.Test> tree = BuildTree.build(trees);

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List<Tree> list = new ArrayList<>();
        list.add(tree);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

    /*
    * 添加角色,权限
    *
    * */
    @RequestMapping(value = "AddRole",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddTag(@Valid ShiroVO shiroVO, BindingResult result,
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
        //有必填项没填
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
        Role role=new Role();
        role.setRoleName(shiroVO.getRoleName());
        role.setRemark(shiroVO.getRoleRemark());
        role.setCreateUserid(user.getId());
        //插入角色表
        roleService.insertSelective(role);
        //插入权限记录
        String[] permissionIds = shiroVO.getPermissionIds().split(",");
        for (String permissionId : permissionIds) {
            Permission permission = permissionService.selectByPrimaryKey(Integer.parseInt(permissionId));
            if (permission!=null){
                RolePermission rolePermission=new RolePermission();
                rolePermission.setRoleId(String.valueOf(role.getId()));
                rolePermission.setPermissionId(String.valueOf(permissionId));
                //插入角色权限记录
                rolePermissionService.insertSelective(rolePermission);
            }
        }
        //插入用户,角色记录
        MyuserRole myuserRole=new MyuserRole();
        myuserRole.setRoleId(String.valueOf(role.getId()));
        myuserRole.setRemark(shiroVO.getRoleRemark());
        //插入
        myuserRoleService.insertSelective(myuserRole);

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List<Tree> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    /*
    *
    查看该用户下有什么角色
    * */
    @RequestMapping(value = "getRole", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getDep(HttpServletRequest request) {
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

        List<Role> roles=roleService.getRoleByUserId(user.getId());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(roles);
        resultBean.setSize(roles.size());
        return resultBean;
    }
/*
* 删除角色
*
* */
@RequestMapping(value = "deleteRole",method = {RequestMethod.POST})
@ResponseBody
public ResultBean deleteRole(@RequestParam("") Integer RoleId, HttpServletRequest request
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
    if (RoleId == null || "".equals(RoleId)) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("参数有误,角色id不能为空");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //是否存在该角色
    Role role = roleService.selectByPrimaryKey(RoleId);
    if (role == null) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("该角色不存在");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //admin角色不可修改
    if ("admin".equals(role.getRoleName())&&role.getId()==1){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("管理员角色不可修改");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //判断是否绑定账号
    //绑定账号不可删
    MyuserRole myuserRole = myuserRoleService.getMyuserRoleByRoleId(RoleId);
    if (myuserRole.getUserId()!=null&&!"".equals(myuserRole.getRoleId())){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("该角色已经绑定账号");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //删除角色
    roleService.deleteByPrimaryKey(RoleId);
    //删除用户_角色
    int del=myuserRoleService.deleteByRoleId(RoleId);
    //删除角色_权限
    rolePermissionService.deleteByRoleId(RoleId);

    resultBean = new ResultBean();
    resultBean.setCode(1);
    resultBean.setMsg("操作成功");
    List<Myuser> list = new ArrayList<>();
    resultBean.setData(list);
    resultBean.setSize(list.size());
    return resultBean;
}

/*
* 根据角色id查询角色权限
* */
@RequestMapping(value = "getPermissionsByRoleId",method = {RequestMethod.GET})
@ResponseBody
public ResultBean getPermissionsByRoleId(@RequestParam("") Integer RoleId, HttpServletRequest request
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
    if (RoleId == null || "".equals(RoleId)) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("参数有误,角色id不能为空");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //是否存在该角色
    Role role = roleService.selectByPrimaryKey(RoleId);
    if (role == null) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("该角色不存在");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    List<RolePermission> permissionList=rolePermissionService.getPermissionsByRoleId(RoleId);
    StringBuffer sb=new StringBuffer();
    for (RolePermission rolePermission : permissionList) {
        sb.append(rolePermission.getPermissionId());
        sb.append(",");
    }
    resultBean = new ResultBean();
    resultBean.setCode(1);
    resultBean.setMsg("操作成功");
    List<String> list = new ArrayList<>();
    list.add(sb.toString());
    resultBean.setData(list);
    resultBean.setSize(list.size());
    return resultBean;
}

/*
* 修改角色信息
* */

@RequestMapping(value = "UpdateRole",method = RequestMethod.POST)
@ResponseBody
public ResultBean UpdateRole(@Valid ShiroVO shiroVO, BindingResult result,
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
    //有必填项没填
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
    if (shiroVO.getRoleId()==null||"".equals(shiroVO.getRoleId())){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("参数有误,角色id不能为空");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //是否存在该角色
    Role role = roleService.selectByPrimaryKey(shiroVO.getRoleId());
    if (role == null) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("该角色不存在");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //admin角色不可修改
    if ("admin".equals(role.getRoleName())&&role.getId()==1){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("管理员角色不可修改");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //更新角色
    role.setRemark(shiroVO.getRoleRemark());
    role.setRoleName(shiroVO.getRoleName());
    roleService.updateByPrimaryKeySelective(role);
    //删除原先权限
    rolePermissionService.deleteByRoleId(shiroVO.getRoleId());
    //插入新权限
    String[] permissionIds = shiroVO.getPermissionIds().split(",");
    for (String permissionId : permissionIds) {
        Permission permission = permissionService.selectByPrimaryKey(Integer.parseInt(permissionId));
        if (permission!=null){
            RolePermission rolePermission=new RolePermission();
            rolePermission.setRoleId(String.valueOf(role.getId()));
            rolePermission.setPermissionId(String.valueOf(permissionId));
            //插入角色权限记录
            rolePermissionService.insertSelective(rolePermission);
        }
    }


    resultBean = new ResultBean();
    resultBean.setCode(1);
    resultBean.setMsg("操作成功");
    List<Role> list = new ArrayList<>();
    list.add(role);
    resultBean.setData(list);
    resultBean.setSize(list.size());
    return resultBean;
}


}
