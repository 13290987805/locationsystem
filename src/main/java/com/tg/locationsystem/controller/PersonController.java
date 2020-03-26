package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.mapper.PersonMapper;
import com.tg.locationsystem.mapper.PersonTypeMapper;
import com.tg.locationsystem.mapper.TableMapper;
import com.tg.locationsystem.pojo.*;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.StringUtils;
import com.tg.locationsystem.utils.SystemMap;
import com.tg.locationsystem.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.*;
import java.util.Map;

/**
 * @author hyy
 * @ Date2019/6/27
 */
@Controller
@RequestMapping("person")
public class PersonController {
    @Autowired
    private IPersonService personService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IPersonTypeService personTypeService;
    @Autowired
    private PersonTypeMapper personTypeMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private TableMapper tableMapper;
    @Autowired
    private ITagHistoryService tagHistoryService;
    @Autowired
    private ITagStatusService tagStatusService;
    @Autowired
    private IFrenceHistoryService frenceHistoryService;
    @Autowired
    private IHeartRateHistoryService heartRateHistoryService;
    @Autowired
    private IDepService depService;

    /*
    * 添加人员
    * */
    @RequestMapping(value = "AddPerson",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultBean AddPerson(@Valid Person person, BindingResult result,
                                HttpServletRequest request, @RequestParam(value="image",required=false)MultipartFile file){
        //System.out.println("添加人员:"+person.getPersonName());
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

        //有必填项没填
        if (result.hasErrors()) {
            List<String> errorlist=new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = fieldError.getDefaultMessage();
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

        //System.out.println(person.toString());
        //人员已经存在
        Person myperson=personService.getPersonByIdCard(person.getIdCard());
        if (myperson!=null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该人员已经存在");
            List<Person> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Tag usetag = tagService.getTagByAddress(person.getTagAddress());
        if ("1".equals(usetag.getUsed())){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该标签已被别人使用");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //设置人员所属管理者
        person.setUserId(user.getId());
        //设置人员标签为""
        if (person.getTagAddress()==null){
            person.setTagAddress("");
        }
        Tag tag = tagService.getTagByAddress(person.getTagAddress());
        if (tag!=null){
            tag.setUsed("1");
            //更新标签表
            tagService.updateByPrimaryKeySelective(tag);
        }
        if (file==null){
            PersonType personType = personTypeService.selectByPrimaryKey(person.getPersonTypeid());
            if (personType!=null){
                person.setImg(personType.getLogo());
            }
        }

        //设置图片路径
        //获取文件名加后缀
        if (file!=null){
            //保存图片的路径
            String s = System.getProperty("user.dir");//C:\whzy\locationsystem
            String filePath =s.split(":")[0]+":\\img";
            UploadFileUtil.isChartPathExist(filePath);

            //String filePath = "C:\\whzy\\locationsystem\\src\\main\\resources\\static\\person";
            //获取原始图片的拓展名
            String originalFilename = file.getOriginalFilename();
            //新的文件名字
            String newFileName = UUID.randomUUID()+originalFilename;
             //封装上传文件位置的全路径
            File targetFile = new File(filePath,newFileName);
             //把本地文件上传到封装上传文件位置的全路径
            try {
                file.transferTo(targetFile);
                person.setImg(targetFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("上传人员头像失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        int insert = personService.insertSelective(person);
        if (insert>0){
            //把该标签放到缓存中
            Map<String, Integer> usermap = SystemMap.getUsermap();
            usermap.put(tag.getAddress(),tag.getUserId());
            SystemMap.getTagAndPersonMap().put(tag.getAddress(),person.getIdCard());
            //设置次数
            SystemMap.getCountmap().put(tag.getAddress(),0);


            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("添加人员成功");
            List<Person> list = new ArrayList<>();
            list.add(person);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("添加人员失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }


    }
    /*
     * 得到用户下没有绑定标签的人员
     * */
    @RequestMapping(value = "getPersonsByNoTag",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersonsByNoTag(HttpServletRequest request){
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
        List<Person> personList = personService.getPersonsByNoTag(user.getId());

        if (personList.size()>0){
            List<PersonVO> personVOList = new ArrayList<>();
            for (Person person : personList) {
                PersonVO personVO = new PersonVO();
                personVO.setId(person.getId());
                personVO.setIdCard(person.getIdCard());
                personVO.setImg(person.getImg());
                personVO.setPersonHeight(person.getPersonHeight());
                personVO.setPersonPhone(person.getPersonPhone());
                personVO.setPersonName(person.getPersonName());
                personVO.setPersonSex(person.getPersonSex());
                personVO.setTagAddress(person.getTagAddress());
                personVO.setUserId(person.getUserId());
                personVO.setPersonTypeid(person.getPersonTypeid());
                //人员类型名字
                PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
                if (personType != null) {
                    personVO.setPersonTypeName(personType.getTypeName());
                }
                //人员所属组织
                if (person.getDepId()!=null){
                    Dep dep = depService.selectByPrimaryKey(person.getDepId());
                    if (dep!=null){
                        personVO.setDepName(dep.getName());
                        personVO.setDepId(dep.getId());
                    }
                }


                personVOList.add(personVO);
            }
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            resultBean.setData(personVOList);
            resultBean.setSize(personVOList.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            resultBean.setData(personList);
            resultBean.setSize(personList.size());
            return resultBean;
        }
    }
    /*
     * 查看所有人员信息
     * */
    @RequestMapping(value = "getPersons",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersons(HttpServletRequest request,
                                  @RequestParam(defaultValue = "1") Integer pageIndex,
                                  @RequestParam(defaultValue = "1000") Integer pageSize){
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

        PageInfo<Person> pageInfo=personService.getPersonsByUserId(pageIndex,pageSize,user.getId());
        List<PersonVO> personVOList=new ArrayList<>();
        for (Person person : pageInfo.getList()) {
            PersonVO personVO=new PersonVO();
            personVO.setId(person.getId());
            personVO.setIdCard(person.getIdCard());
            personVO.setImg(person.getImg());
            personVO.setPersonHeight(person.getPersonHeight());
            personVO.setPersonPhone(person.getPersonPhone());
            personVO.setPersonName(person.getPersonName());
            personVO.setPersonSex(person.getPersonSex());
            personVO.setTagAddress(person.getTagAddress());
            personVO.setUserId(person.getUserId());
            personVO.setPersonTypeid(person.getPersonTypeid());
            //人员类型名字
            PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
            if (personType!=null){
                personVO.setPersonTypeName(personType.getTypeName());
            }
            //人员所属组织
            if (person.getDepId()!=null){
                Dep dep = depService.selectByPrimaryKey(person.getDepId());
                if (dep!=null){
                    personVO.setDepName(dep.getName());
                    personVO.setDepId(dep.getId());
                }
            }
            personVOList.add(personVO);
        }
        PageInfo<PersonVO> page= new PageInfo<>(personVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(personVOList);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());

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
    * 查看人员位置
    * */
    @RequestMapping(value = "getLocation",method = RequestMethod.GET)
    @ResponseBody
    public AllTagLocationResult getLocation(HttpServletRequest request,
                                            @RequestParam("personids") String personids){

        AllTagLocationResult resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new AllTagLocationResult();
            resultBean.setCode(5);
            resultBean.setMsg("还未登录");
            return resultBean;
        }
        String[] split = personids.split(",");
        List<Location> locationList=new ArrayList<>();
        for (String s : split) {
            Location location=new Location();
            Person person = personService.selectByPrimaryKey(Integer.parseInt(s));
            if (person!=null){
                location.setId(person.getId());
                location.setIdCard(person.getIdCard());
                location.setImg(person.getImg());
                location.setPersonHeight(person.getPersonHeight());
                location.setPersonName(person.getPersonName());
                location.setPersonPhone(person.getPersonPhone());
                location.setPersonSex(person.getPersonSex());
                location.setPersonType(personTypeService.selectByPrimaryKey(person.getPersonTypeid()));
                location.setUserId(person.getUserId());
                location.setTagAddress(person.getTagAddress());

                Tag tag = tagService.getTagByAddress(person.getTagAddress());
                if (tag!=null){
                    location.setTag(tag);
                }
                locationList.add(location);
            }
        }
        resultBean = new AllTagLocationResult();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setPersonlocationList(locationList);
        return resultBean;
    }

    /*
    * 新增人员类型
    * */
    @RequestMapping(value = "AddPersonType",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddPersonType(@Valid PersonType personType, BindingResult result,
                                    HttpServletRequest request,@RequestParam(value="image",required=false)MultipartFile file){

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

        //有必填项没填
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

        //人员类型已经存在
      PersonType mypersontype= personTypeService.getPersonTypeByName(personType.getTypeName(),user.getId());
        if (mypersontype!=null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("类型已经存在,无法重复添加");
            List<PersonType> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //设置人员类型创建时间和所属管理者
        personType.setCreateTime(new Date());

        personType.setUserId(user.getId());
        System.out.println("图片:"+file.getOriginalFilename());

        if (file==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("图标不能为空");
            List<PersonType> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        //设置图片路径
        //获取文件名加后缀
        if (file!=null){
            System.out.println("文件不为空");
            //保存图片的路径
            String s = System.getProperty("user.dir");//C:\whzy\locationsystem
            String filePath =s.split(":")[0]+":\\img";
            UploadFileUtil.isChartPathExist(filePath);
            //String filePath = "C:\\whzy\\locationsystem\\src\\main\\resources\\static\\person";
            //获取原始图片的拓展名
            String originalFilename = file.getOriginalFilename();
            //新的文件名字
            String newFileName = UUID.randomUUID()+originalFilename;
            //封装上传文件位置的全路径
            File targetFile = new File(filePath,newFileName);
            //把本地文件上传到封装上传文件位置的全路径
            try {
                file.transferTo(targetFile);
                personType.setLogo(targetFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("上传人员类型logo失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }

        int insert = personTypeService.insertSelective(personType);
        if (insert>0){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("人员类型添加成功");
            List<PersonType> list = new ArrayList<>();
            list.add(personType);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("人员类型添加失败");
            List<PersonType> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
    * 查询所有人员类型
    * 分页
    * */
    @RequestMapping(value = "getPersonTypesPage",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersonTypesPage(HttpServletRequest request,
                                 @RequestParam(defaultValue = "1") Integer pageIndex,
                                 @RequestParam(defaultValue = "10") Integer pageSize){

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
        PageInfo<PersonType> pageInfo=personTypeService.getPersonTypesByUserId(pageIndex,pageSize,user.getId());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list=new ArrayList<>();
        list.add(pageInfo);
        resultBean.setData(list);
        resultBean.setSize(pageInfo.getSize());
        return resultBean;
    }
    /*
     * 查询所有人员类型
     * 不分页
     * */
    @RequestMapping(value = "getPersonTypes",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersonTypes(HttpServletRequest request){
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
        List<PersonType> personTypeList=personTypeService.getPersonTypes(user.getId());
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(personTypeList);
        resultBean.setSize(personTypeList.size());
        return resultBean;
    }

    /*
    * 根据类型查询相关人员
    * 分页
    * */
    @RequestMapping(value = "getPersonsByTypePage",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersonsByteTypePage(HttpServletRequest request,
                                         @RequestParam("person_typeid") Integer typeid,
                                         @RequestParam(defaultValue = "1") Integer pageIndex,
                                         @RequestParam(defaultValue = "10") Integer pageSize){

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
        if (typeid==null||"".equals(typeid)|| !StringUtils.isNumeric(String.valueOf(typeid))){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("查询参数有误");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        PageInfo<Person> pageInfo=personService.getPersonsByteTypePage(pageIndex,pageSize,typeid,user.getId());

        List<PersonVO> personVOList=new ArrayList<>();
        for (Person person : pageInfo.getList()) {
            PersonVO personVO=new PersonVO();
            personVO.setId(person.getId());
            personVO.setIdCard(person.getIdCard());
            personVO.setImg(person.getImg());
            personVO.setPersonHeight(person.getPersonHeight());
            personVO.setPersonPhone(person.getPersonPhone());
            personVO.setPersonName(person.getPersonName());
            personVO.setPersonSex(person.getPersonSex());
            personVO.setTagAddress(person.getTagAddress());
            personVO.setUserId(person.getUserId());
            personVO.setPersonTypeid(person.getPersonTypeid());
            //人员类型名字
            PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
            if (personType!=null){
                personVO.setPersonTypeName(personType.getTypeName());
            }
            //人员所属组织
            if (person.getDepId()!=null){
                Dep dep = depService.selectByPrimaryKey(person.getDepId());
                if (dep!=null){
                    personVO.setDepName(dep.getName());
                    personVO.setDepId(dep.getId());
                }
            }
            personVOList.add(personVO);
        }
        PageInfo<PersonVO> page= new PageInfo<>(personVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(personVOList);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());

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
     * 根据类型查询相关人员
     * 不分页
     * */
    @RequestMapping(value = "getPersonsByType",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersonsByType(HttpServletRequest request,
                                             @RequestParam("person_typeid") Integer typeid){
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
        if (typeid==null||"".equals(typeid)|| !StringUtils.isNumeric(String.valueOf(typeid))){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("查询参数有误");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        List<Person> personTypeList=personService.getPersonsByType(typeid,user.getId());
        List<PersonVO> personVOList=new ArrayList<>();
        for (Person person : personTypeList) {
            PersonVO personVO=new PersonVO();
            personVO.setId(person.getId());
            personVO.setIdCard(person.getIdCard());
            personVO.setImg(person.getImg());
            personVO.setPersonHeight(person.getPersonHeight());
            personVO.setPersonPhone(person.getPersonPhone());
            personVO.setPersonName(person.getPersonName());
            personVO.setPersonSex(person.getPersonSex());
            personVO.setTagAddress(person.getTagAddress());
            personVO.setUserId(person.getUserId());
            personVO.setPersonTypeid(person.getPersonTypeid());
            //人员类型名字
            PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
            if (personType!=null){
                personVO.setPersonTypeName(personType.getTypeName());
            }
            //人员所属组织
            if (person.getDepId()!=null){
                Dep dep = depService.selectByPrimaryKey(person.getDepId());
                if (dep!=null){
                    personVO.setDepName(dep.getName());
                    personVO.setDepId(dep.getId());
                }
            }
            personVOList.add(personVO);
        }

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(personVOList);
        resultBean.setSize(personTypeList.size());
        return resultBean;
    }
/*
* 删除人员,对应的轨迹记录,报警记录,都要删除
* */
@RequestMapping(value = "deletePerson",method = RequestMethod.POST)
@ResponseBody
public ResultBean deletePerson(HttpServletRequest request,
                               @RequestParam("personid") Integer personid){
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
    Person person = personService.selectByPrimaryKey(personid);
    if (person==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("该人员不存在");
        List<Person> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    int delete = personService.deleteByPrimaryKey(personid);
    if (delete>0){

        //更新标签,使标签变为未使用状态
        Tag tag = tagService.getTagByAddress(person.getTagAddress());
        if (tag!=null){
            tag.setUsed("0");
            int updatetag = tagService.updateByPrimaryKeySelective(tag);

            //把该标签在缓存中的记录清掉
            SystemMap.getUsermap().remove(tag.getAddress());
            SystemMap.getTagAndPersonMap().remove(tag.getAddress());
            SystemMap.getCountmap().remove(tag.getAddress());

            //删除轨迹,报警,电子围栏,心率记录等
            List<String> Tablelist = tableMapper.listTable();
            for (String tableName : Tablelist) {
                if (tableName.contains("tag_history")){
                    int deleteHistory=tagHistoryService.deleteHistory(tableName,tag.getAddress());
                }
                if (tableName.contains("tag_status")){
                    int deletetagStatus=tagStatusService.deletetagStatus(tableName,tag.getAddress());
                }
                if (tableName.contains("frence_history")){
                    int deleteHistory=frenceHistoryService.deleteHistory(tableName,tag.getAddress());
                }
                if (tableName.contains("heart_rate_history")){
                    int deleteHistory=heartRateHistoryService.deleteHistory(tableName,tag.getAddress());
                }
            }
            if (updatetag>0){
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("成功删除人员");
                List<Person> list = new ArrayList<>();
                list.add(person);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("成功删除人员");
        List<Person> list = new ArrayList<>();
        list.add(person);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }else {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("删除失败");
        List<Person> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}
    /*
     * 查看人员图片
     * 图片下载
     * */
    @RequestMapping(value = "querypersonImg/{personId}",method = RequestMethod.GET)
    public void queryImg(@PathVariable Integer personId, HttpServletResponse response,
                         HttpServletRequest request){
        Person person = personService.selectByPrimaryKey(personId);
        if (person==null){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("查询不到相关信息");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String url=person.getImg();
        if (url==null || "".equals(url)){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("暂无图片");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        FileInputStream hFile = null;      //得到文件大小
        try {
            hFile = new FileInputStream(url);
            int i=hFile.available();
            byte data[]=new byte[i];        //读数据

            hFile.read(data);         //得到向客户端输出二进制数据的对象

            OutputStream toClient=response.getOutputStream();         //输出数据

            toClient.write(data);

            toClient.flush();
            toClient.close();

            hFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //重新设置session存活时间
            //request.getSession().setMaxInactiveInterval(30*60);
        }
    }

    /*
     * 查看人员类型图片
     * 图片下载
     * */
    @RequestMapping(value = "querypersonTypeImg/{personTypeId}",method = RequestMethod.GET)
    public void querypersonTypeImg(@PathVariable Integer personTypeId, HttpServletResponse response,
                         HttpServletRequest request){
        PersonType personType = personTypeService.selectByPrimaryKey(personTypeId);
        if (personType==null){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("查询不到相关信息");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String url=personType.getLogo();
        if (url==null || "".equals(url)){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("暂无图片");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        FileInputStream hFile = null;      //得到文件大小
        try {
            hFile = new FileInputStream(url);
            int i=hFile.available();
            byte data[]=new byte[i];        //读数据

            hFile.read(data);         //得到向客户端输出二进制数据的对象

            OutputStream toClient=response.getOutputStream();         //输出数据

            toClient.write(data);

            toClient.flush();
            toClient.close();

            hFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //重新设置session存活时间
            //request.getSession().setMaxInactiveInterval(30*60);
        }
    }

    /*
    * 修改人员信息
    * */
    @RequestMapping(value = "UpdatePerson",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultBean UpdatePerson(@Valid Person person, BindingResult result,
                                HttpServletRequest request, @RequestParam(value="image",required=false)MultipartFile file){

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

        //有必填项没填
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
        if (person.getId()==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("id不能为空");
            List<Myuser> list = new ArrayList<>();     
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Person sqlPerson2=personService.selectByPrimaryKey(person.getId());
        if (!sqlPerson2.getIdCard().equals(person.getIdCard())){
            Person personByOnlyAddress = personService.getPersonByOnlyAddress(person.getIdCard());
            if (personByOnlyAddress!=null){
                if (sqlPerson2!=null){
                    resultBean = new ResultBean();
                    resultBean.setCode(-1);
                    resultBean.setMsg("身份证号码有误");
                    List<Myuser> list = new ArrayList<>();
                    resultBean.setData(list);
                    resultBean.setSize(list.size());
                    return resultBean;
                }
            }

        }
        //设置人员所属管理者
        person.setUserId(user.getId());

        if (file==null){
            Person person1 = personService.selectByPrimaryKey(person.getId());
            if (person1!=null){
                person.setImg(person1.getImg());
            }
        }

        //设置图片路径
        //获取文件名加后缀
        if (file!=null){
            //保存图片的路径
            String s = System.getProperty("user.dir");//C:\whzy\locationsystem
            String filePath =s.split(":")[0]+":\\img";
            UploadFileUtil.isChartPathExist(filePath);
          //  String filePath = "C:\\whzy\\locationsystem\\src\\main\\resources\\static\\person";
            //获取原始图片的拓展名
            String originalFilename = file.getOriginalFilename();
            //新的文件名字
            String newFileName = UUID.randomUUID()+originalFilename;
            //封装上传文件位置的全路径
            File targetFile = new File(filePath,newFileName);
            //把本地文件上传到封装上传文件位置的全路径
            try {
                file.transferTo(targetFile);
                person.setImg(targetFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("上传人员头像失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        Person sqlPerson = personService.selectByPrimaryKey(person.getId());
        if (sqlPerson!=null){
            if (!sqlPerson.getTagAddress().equals(person.getTagAddress())){
                Tag tag = tagService.getTagByOnlyAddress(sqlPerson.getTagAddress());
                if (tag!=null){
                    tag.setUsed("0");
                    //把该标签在缓存中的记录清掉
                    SystemMap.getUsermap().remove(tag.getAddress());
                    SystemMap.getTagAndPersonMap().remove(tag.getAddress());
                    SystemMap.getCountmap().remove(tag.getAddress());

                    tagService.updateByPrimaryKeySelective(tag);
                }
                Tag sqltag = tagService.getTagByOnlyAddress(person.getTagAddress());
                if (sqltag!=null){
                    if (sqltag.getAddress()!=null){
                        //把该标签放到缓存中
                        Map<String, Integer> usermap = SystemMap.getUsermap();
                        usermap.put(sqltag.getAddress(),tag.getUserId());
                        SystemMap.getTagAndPersonMap().put(sqltag.getAddress(),person.getIdCard());
                        //设置次数
                        SystemMap.getCountmap().put(sqltag.getAddress(),0);
                    }


                    sqltag.setUsed("1");
                    tagService.updateByPrimaryKeySelective(sqltag);

                }
            }
        }
        int update = personService.updateByPrimaryKeySelective(person);
        if (update>0){
            //把该标签放到缓存中
            Map<String, Integer> usermap = SystemMap.getUsermap();
            usermap.put(person.getTagAddress(),person.getUserId());
            SystemMap.getTagAndPersonMap().put(person.getTagAddress(),person.getIdCard());
            //设置次数
            SystemMap.getCountmap().put(person.getTagAddress(),0);

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("人员修改成功");
            List<Person> list = new ArrayList<>();
            list.add(person);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("人员修改失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
/*
*修改人员类型
* */
@RequestMapping(value = "UpdatePersonType",method = {RequestMethod.POST,RequestMethod.GET})
@ResponseBody
public ResultBean UpdatePersonType(@Valid PersonType personType, BindingResult result,
                                HttpServletRequest request,@RequestParam(value="image",required=false)MultipartFile file){
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

    //有必填项没填
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
    if (personType.getId()==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("id不能为空");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //设置人员所属管理者
    personType.setUserId(user.getId());
    //设置时间
    personType.setCreateTime(new Date());

    if (file==null){
        PersonType personType1 = personTypeService.selectByPrimaryKey(personType.getId());
        if (personType1!=null){
            personType.setLogo(personType1.getLogo());
        }
    }

    //设置图片路径
    //获取文件名加后缀
    if (file!=null){
        System.out.println("文件不为空");
        //保存图片的路径
        String s = System.getProperty("user.dir");//C:\whzy\locationsystem
        String filePath =s.split(":")[0]+":\\img";
        UploadFileUtil.isChartPathExist(filePath);
        //String filePath = "C:\\whzy\\locationsystem\\src\\main\\resources\\static\\person";
        //获取原始图片的拓展名
        String originalFilename = file.getOriginalFilename();
        //新的文件名字
        String newFileName = UUID.randomUUID()+originalFilename;
        //封装上传文件位置的全路径
        File targetFile = new File(filePath,newFileName);
        //把本地文件上传到封装上传文件位置的全路径
        try {
            file.transferTo(targetFile);
            personType.setLogo(targetFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("上传人员类型logo失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
    int update = personTypeService.updateByPrimaryKey(personType);
    if (update>0){
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("人员类型修改成功");
        List<PersonType> list = new ArrayList<>();
        list.add(personType);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }else {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("人员类型修改失败");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}
    /*
     * 模糊查询,根据输入信息查询相关人员
     * */
    @RequestMapping(value = "getPersonsByMsg",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersonsByMsg(HttpServletRequest request,
                                      @RequestParam(defaultValue = "1") Integer pageIndex,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(defaultValue = "") String msg){

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
        if (msg==null||"".equals(msg)){

            PageInfo<Person> pageInfo=personService.getPersonsByUserId(pageIndex,pageSize,user.getId());

            List<PersonVO> personVOList=new ArrayList<>();
            for (Person person : pageInfo.getList()) {
                PersonVO personVO=new PersonVO();
                personVO.setId(person.getId());
                personVO.setIdCard(person.getIdCard());
                personVO.setImg(person.getImg());
                personVO.setPersonHeight(person.getPersonHeight());
                personVO.setPersonPhone(person.getPersonPhone());
                personVO.setPersonName(person.getPersonName());
                personVO.setPersonSex(person.getPersonSex());
                personVO.setTagAddress(person.getTagAddress());
                personVO.setUserId(person.getUserId());
                personVO.setPersonTypeid(person.getPersonTypeid());
                //人员类型名字
                PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
                if (personType!=null){
                    personVO.setPersonTypeName(personType.getTypeName());
                }
                //人员所属组织
                if (person.getDepId()!=null){
                    Dep dep = depService.selectByPrimaryKey(person.getDepId());
                    if (dep!=null){
                        personVO.setDepName(dep.getName());
                        personVO.setDepId(dep.getId());
                    }
                }
                personVOList.add(personVO);
            }
            PageInfo<PersonVO> page= new PageInfo<>(personVOList);
            page.setPageNum(pageInfo.getPageNum());
            page.setSize(pageInfo.getSize());
            page.setSize(pageInfo.getSize());
            page.setStartRow(pageInfo.getStartRow());
            page.setEndRow(pageInfo.getEndRow());
            page.setTotal(pageInfo.getTotal());
            page.setPages(pageInfo.getPages());
            page.setList(personVOList);
            page.setPrePage(pageInfo.getPrePage());
            page.setNextPage(pageInfo.getNextPage());
            page.setIsFirstPage(pageInfo.isIsFirstPage());
            page.setIsLastPage(pageInfo.isIsLastPage());

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List list=new ArrayList<>();
            list.add(page);
            resultBean.setData(list);
            resultBean.setSize(page.getSize());
            return resultBean;
        }
        PageInfo<Person> pageInfo=personService.getPersonsByMsg(pageIndex,pageSize,user.getId(),msg);
        List<PersonType> personTypeList=personTypeMapper.getPersonsTypeByMsg(user.getId(), msg);
        // System.out.println("人员类型表查询结果:"+personTypeList.size());
        if (personTypeList.size()>0){
            for (PersonType personType : personTypeList) {
                List<Person> personList=personMapper.getPersonsBypersonTypeId(user.getId(),personType.getId());
                for (Person person : personList) {
                    pageInfo.getList().add(person);
                }
            }
        }

        List<PersonVO> personVOList = new ArrayList<>();
        for (Person person : pageInfo.getList()) {
            PersonVO personVO = new PersonVO();
            personVO.setId(person.getId());
            personVO.setIdCard(person.getIdCard());
            personVO.setImg(person.getImg());
            personVO.setPersonHeight(person.getPersonHeight());
            personVO.setPersonPhone(person.getPersonPhone());
            personVO.setPersonName(person.getPersonName());
            personVO.setPersonSex(person.getPersonSex());
            personVO.setTagAddress(person.getTagAddress());
            personVO.setUserId(person.getUserId());
            personVO.setPersonTypeid(person.getPersonTypeid());
            //人员类型名字
            PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
            if (personType != null) {
                personVO.setPersonTypeName(personType.getTypeName());
            }
            //人员所属组织
            if (person.getDepId()!=null){
                Dep dep = depService.selectByPrimaryKey(person.getDepId());
                if (dep!=null){
                    personVO.setDepName(dep.getName());
                    personVO.setDepId(dep.getId());
                }
            }
            personVOList.add(personVO);
        }
        PageInfo<PersonVO> page= new PageInfo<>(personVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(personVOList);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());

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
* 根据标签类型查看所有相关人员信息
*
* */
@RequestMapping(value = "getPersonsByTagTypeId",method = RequestMethod.GET)
@ResponseBody
public ResultBean getPersonsByTagTypeId(HttpServletRequest request,
                                  @RequestParam(defaultValue = "") String tagTypeId){
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
    if (tagTypeId==null||"".equals(tagTypeId)|| !StringUtils.isNumeric(String.valueOf(tagTypeId))){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("查询参数有误");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    List<Tag> tagList=tagService.getTagsByType(Integer.parseInt(tagTypeId),user.getId());
    List<PersonVO> personVOList=new ArrayList<>();
    for (Tag tag : tagList) {
        Person person = personService.getPersonByOnlyAddress(tag.getAddress());
        if (person!=null){
            PersonVO personVO=new PersonVO();
            personVO.setId(person.getId());
            personVO.setIdCard(person.getIdCard());
            personVO.setImg(person.getImg());
            personVO.setPersonHeight(person.getPersonHeight());
            personVO.setPersonPhone(person.getPersonPhone());
            personVO.setPersonName(person.getPersonName());
            personVO.setPersonSex(person.getPersonSex());
            personVO.setTagAddress(person.getTagAddress());
            personVO.setUserId(person.getUserId());
            personVO.setPersonTypeid(person.getPersonTypeid());
            //人员类型名字
            PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
            if (personType!=null){
                personVO.setPersonTypeName(personType.getTypeName());
            }
            //人员所属组织
            if (person.getDepId()!=null){
                Dep dep = depService.selectByPrimaryKey(person.getDepId());
                if (dep!=null){
                    personVO.setDepName(dep.getName());
                    personVO.setDepId(dep.getId());
                }
            }
            personVOList.add(personVO);
        }
    }

    resultBean = new ResultBean();
    resultBean.setCode(1);
    resultBean.setMsg("操作成功");
    resultBean.setData(personVOList);
    resultBean.setSize(personVOList.size());
    return resultBean;
}
/*
*
* 根据组织部门id查询相关人员
* */
@RequestMapping(value = "getPersonsByDepId",method = RequestMethod.GET)
@ResponseBody
public ResultBean getPersonsByDepId(HttpServletRequest request,
                                    @RequestParam(defaultValue = "1") Integer pageIndex,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(defaultValue = "") Integer depId) {
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
    if (depId == null || "".equals(depId) || !StringUtils.isNumeric(String.valueOf(depId))) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("查询参数有误");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //得到该组织id下的子节点
    List<Integer> depIds = depService.getDepIdsByParentId(user.getId(), depId);
    depIds.add(depId);
    PageInfo<Person> pageInfo=personService.getPersonsByDepIdPage(user.getId(),depIds,pageIndex,pageSize);

    List<PersonVO> personVOList=new ArrayList<>();
    for (Person person : pageInfo.getList()) {
        PersonVO personVO=new PersonVO();
        personVO.setId(person.getId());
        personVO.setIdCard(person.getIdCard());
        personVO.setImg(person.getImg());
        personVO.setPersonHeight(person.getPersonHeight());
        personVO.setPersonPhone(person.getPersonPhone());
        personVO.setPersonName(person.getPersonName());
        personVO.setPersonSex(person.getPersonSex());
        personVO.setTagAddress(person.getTagAddress());
        personVO.setUserId(person.getUserId());
        personVO.setPersonTypeid(person.getPersonTypeid());
        //人员类型名字
        PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
        if (personType!=null){
            personVO.setPersonTypeName(personType.getTypeName());
        }
        //人员所属组织
        if (person.getDepId()!=null){
            Dep dep = depService.selectByPrimaryKey(person.getDepId());
            if (dep!=null){
                personVO.setDepName(dep.getName());
                personVO.setDepId(dep.getId());
            }
        }
        personVOList.add(personVO);
    }



    PageInfo<PersonVO> page= new PageInfo<>(personVOList);
    page.setPageNum(pageInfo.getPageNum());
    page.setSize(pageInfo.getSize());
    page.setSize(pageInfo.getSize());
    page.setStartRow(pageInfo.getStartRow());
    page.setEndRow(pageInfo.getEndRow());
    page.setTotal(pageInfo.getTotal());
    page.setPages(pageInfo.getPages());
    page.setList(personVOList);
    page.setPrePage(pageInfo.getPrePage());
    page.setNextPage(pageInfo.getNextPage());
    page.setIsFirstPage(pageInfo.isIsFirstPage());
    page.setIsLastPage(pageInfo.isIsLastPage());

    resultBean = new ResultBean();
    resultBean.setCode(1);
    resultBean.setMsg("操作成功");
    List list=new ArrayList<>();
    list.add(page);
    resultBean.setData(list);
    resultBean.setSize(page.getSize());
    return resultBean;
}

}
