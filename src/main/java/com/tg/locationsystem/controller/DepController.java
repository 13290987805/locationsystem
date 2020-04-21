package com.tg.locationsystem.controller;

import com.google.gson.Gson;
import com.tg.locationsystem.config.Operation;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.service.IDepService;
import com.tg.locationsystem.service.IPersonService;
import com.tg.locationsystem.utils.SystemMap;
import com.tg.locationsystem.utils.test.BuildTree;
import com.tg.locationsystem.utils.test.Test;
import com.tg.locationsystem.utils.test.Tree;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.util.*;
import java.util.Map;

/**
 * @author hyy
 * @ Date2020/3/24
 */
@Controller
@RequestMapping("dep")
public class DepController {
    @Autowired
    private IDepService depService;
    @Autowired
    private IPersonService personService;

    /*
    * 添加组织部门
    *
    * Add the department
    * */
    @RequestMapping(value = "AddDep", method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("dep_add")
    @Operation("添加组织部门")
    public ResultBean AddDep(@Valid Dep dep, BindingResult result,
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
        //pid为空,顶级节点
        if (dep.getPid()==null||"".equals(dep.getPid())){
            dep.setPid(0);
            //一个账号,一个顶级节点
            Dep sqlDep=depService.getDepByTopPid(user.getId(),dep.getPid());
            if (sqlDep!=null){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("已存在顶级节点");
                List<Frence> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        dep.setUserId(user.getId());
        dep.setCreateTime(new Date());
        dep.setUpdateTime(new Date());
        //插入
        int insert = depService.insertSelective(dep);
        if (insert > 0) {

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("组织部门添加成功");
            List<Dep> list = new ArrayList<>();
            list.add(dep);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("添加失败");
            List<Dep> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
    *查看组织部门
    *
    * Look at the organizational department
    * */
    @RequestMapping(value = "getDep", method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("dep_select")
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
        List<Dep> depList=depService.getDepsByUserId(user.getId());
        List<Tree<Test>> trees = new ArrayList<Tree<com.tg.locationsystem.utils.test.Test>>();


        if (depList.size()==0||depList==null){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List<Tree> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        for (Dep dep : depList) {
            Tree<com.tg.locationsystem.utils.test.Test> tree = new Tree<com.tg.locationsystem.utils.test.Test>();
            tree.setId(String.valueOf(dep.getId()));
            tree.setParentId(String.valueOf(dep.getPid()));
            tree.setTitle(dep.getName());
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

        System.out.println("结果:"+new Gson().toJson(tree));

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
*
* 删除组织部门
*
* Delete organizational departments
* */
@RequestMapping(value = "deleteDep",method = {RequestMethod.POST,RequestMethod.GET})
@ResponseBody
@RequiresPermissions("dep_delete")
@Operation("删除组织部门")
public ResultBean deleteDep(@RequestParam("") Integer DepId, HttpServletRequest request
){
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
    if (DepId==null||"".equals(DepId)){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("参数有误,部门id不能为空");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //是否存在该组织部门
    Dep dep = depService.selectByPrimaryKey(DepId);
    if (dep==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("该部门不存在");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //删除该组织节点
    depService.deleteByPrimaryKey(DepId);
    //得到该节点的父节点
    Integer pid = dep.getPid();
    //更新组织id为DepId的人员
    List<Person> personList;
    personList=personService.getPersonsByDepId(user.getId(),DepId);
    for (Person person : personList) {
        //todo 更新组织部门id
        person.setDepId(pid);
        personService.updateByPrimaryKeySelective(person);
    }


    //删除该组织节点下的子节点
    List<Integer> depIds = depService.getDepIdsByParentId(user.getId(), DepId);
    if (depIds.size()>0){
        for (Integer depId : depIds) {
            //删除子节点
            depService.deleteByPrimaryKey(depId);
            //更新组织id为depId的人员
            personList=personService.getPersonsByDepId(user.getId(),depId);
            for (Person person : personList) {
                //todo 更新组织部门id
                person.setDepId(pid);
                personService.updateByPrimaryKeySelective(person);
            }
        }
    }

    resultBean = new ResultBean();
    resultBean.setCode(1);
    resultBean.setMsg("操作成功");
    List<Tree> list = new ArrayList<>();
    resultBean.setData(list);
    resultBean.setSize(list.size());
    return resultBean;
}

/*
* 修改组织部门信息
*
* Update departments information
* */
@RequestMapping(value = "updateDep",method = {RequestMethod.POST,RequestMethod.GET})
@ResponseBody
@RequiresPermissions("dep_update")
@Operation("修改组织部门")
public ResultBean updateDep(@Valid Dep dep, BindingResult result, HttpServletRequest request
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
    if (dep.getId() == null || "".equals(dep.getId())) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("参数有误,部门id不能为空");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //是否存在该组织部门
    Dep sqldDep = depService.selectByPrimaryKey(dep.getId());
    if (sqldDep == null) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("该部门不存在");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    int update = depService.updateByPrimaryKeySelective(dep);

    if (update>0){
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("部门名称修改成功");
        List<Dep> list = new ArrayList<>();
        list.add(dep);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }else {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("部门名称修改失败");
        List<Dep> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}
}
