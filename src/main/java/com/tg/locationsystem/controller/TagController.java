package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.config.Operation;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.mapper.GoodsMapper;
import com.tg.locationsystem.mapper.PersonMapper;
import com.tg.locationsystem.mapper.TagTypeMapper;
import com.tg.locationsystem.pojo.*;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.PoiUtils;
import com.tg.locationsystem.utils.SystemMap;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map;

/**
 * @author hyy
 * @ Date2019/6/27
 */
@Controller
@RequestMapping("tag")
public class TagController {
    @Autowired
    private ITagService tagService;
    @Autowired
    private ITagTypeService tagTypeService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private IGoodsTypeService goodsTypeService;
    @Autowired
    private IPersonTypeService personTypeService;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private TagTypeMapper tagTypeMapper;
    @Autowired
    private IMapService mapService;
    public static final int UP_TIME=15;

    /*
    * 添加标签
    * */
    @RequestMapping(value = "AddTag",method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("tag_add")
    @Operation("添加标签")
    public ResultBean AddTag(@Valid Tag tag, BindingResult result,
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

        //标签已经存在
        Tag mytag=tagService.getTagByAddress(tag.getAddress());
        if (mytag!=null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该标签已经存在");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //设置标签所属以及使用状态 0:未使用,未绑定 1:使用,绑定
        tag.setUsed("0");
        tag.setUserId(user.getId());
        tag.setLastonline(new Date());
        tag.setLastoffline(new Date());
        //添加
        int insert = tagService.insertSelective(tag);
        if (insert>0){

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("添加标签成功");
            List<Tag> list = new ArrayList<>();
            list.add(tag);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("添加标签失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
    /*
     * 查看所有标签,分页
     * */
    @RequestMapping(value = "getTags",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getTags(HttpServletRequest request,
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

        PageInfo<Tag> pageInfo=tagService.getTagsByUserId(pageIndex,pageSize,user.getId());
        List<TagVO> tagVOList=new ArrayList<>();
        for (Tag tag : pageInfo.getList()) {
            TagVO tagVO=new TagVO();
            tagVO.setAddress(tag.getAddress());
            tagVO.setId(tag.getId());
            tagVO.setLastonline(tag.getLastonline());
            tagVO.setUserId(tag.getUserId());
            if (tag.getX()!=null){
                tagVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagVO.setZ(tag.getZ());
            }
            tagVO.setElectric(tag.getElectric());
            tagVO.setAddressBroadcast(tag.getAddressBroadcast());
            //在线离线
            if ("1".equals(tag.getIsonline())){
                tagVO.setIsonline("在线");
            }
            if ("0".equals(tag.getIsonline())){
                tagVO.setIsonline("离线");
            }

            //是否使用
            if ("0".equals(tag.getUsed())){
                tagVO.setUsed("未使用");
                tagVO.setUsername("无");
            }
            if ("1".equals(tag.getUsed())){
                tagVO.setUsed("已使用");
                //使用者
                if (tag.getAddress()!=null){
                    Person person= personMapper.getPersonByOnlyAddress(tag.getAddress());
                    if (person!=null){
                        tagVO.setType("person");
                        tagVO.setUsername(person.getPersonName());
                    }else {
                        Goods goods=goodsMapper.getGoodsByOnlyAddress(tag.getAddress());
                        if (goods!=null){
                            tagVO.setType("goods");
                            tagVO.setUsername(goods.getGoodsName());
                        }
                    }
                }
            }
            //标签类型
            if (tag.getTagTypeid()!=null){
                TagType tagType = tagTypeMapper.selectByPrimaryKey(tag.getTagTypeid());
                if (tagType!=null){
                    tagVO.setTagTypename(tagType.getName());
                }
            }
            tagVOList.add(tagVO);
        }

        PageInfo<TagVO> page= new PageInfo<>(tagVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(tagVOList);
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
    * 查看所有已使用标签信息
    * 分页
    * */
    @RequestMapping(value = "getUsedTagsPage",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getUsedTagsPage(HttpServletRequest request,
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

        PageInfo<Tag> pageInfo=tagService.getUsedTagsPage(pageIndex,pageSize,user.getId());
        List<TagVO> tagVOList=new ArrayList<>();
        for (Tag tag : pageInfo.getList()) {
            TagVO tagVO=new TagVO();
            tagVO.setAddress(tag.getAddress());
            tagVO.setId(tag.getId());
            tagVO.setLastonline(tag.getLastonline());
            tagVO.setUserId(tag.getUserId());
            if (tag.getX()!=null){
                tagVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagVO.setZ(tag.getZ());
            }
            tagVO.setElectric(tag.getElectric());
            tagVO.setAddressBroadcast(tag.getAddressBroadcast());
            tagVO.setUsed("已使用");
            //在线离线
            if ("1".equals(tag.getIsonline())){
                tagVO.setIsonline("在线");
            }
            if ("0".equals(tag.getIsonline())){
                tagVO.setIsonline("离线");
            }

            //标签类型
            if (tag.getTagTypeid()!=null){
                TagType tagType = tagTypeMapper.selectByPrimaryKey(tag.getTagTypeid());
                if (tagType!=null){
                    tagVO.setTagTypename(tagType.getName());
                }
            }
            //使用者
            if (tag.getAddress()!=null){
                Person person= personMapper.getPersonByOnlyAddress(tag.getAddress());
                if (person!=null){
                    tagVO.setType("person");
                    tagVO.setUsername(person.getPersonName());
                }else {
                    Goods goods=goodsMapper.getGoodsByOnlyAddress(tag.getAddress());
                    if (goods!=null){
                        tagVO.setType("goods");
                        tagVO.setUsername(goods.getGoodsName());
                    }
                }
                tagVOList.add(tagVO);
            }
        }
        PageInfo<TagVO> page= new PageInfo<>(tagVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(tagVOList);
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
     * 查看所有已使用标签信息
     * 不分页
     * */
    @RequestMapping(value = "getUsedTags",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getUsedTags(HttpServletRequest request){
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

        List<TagVO> tagList= tagService.getUsedTags(user.getId());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(tagList);
        resultBean.setSize(tagList.size());
        return resultBean;
    }

    /*
     * 查看所有未使用标签信息
     * 分页
     * */
    @RequestMapping(value = "getNoUsedTagsPage",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getNoUsedTagsPage(HttpServletRequest request,
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

        PageInfo<Tag> pageInfo=tagService.getNoUsedTagsPage(pageIndex,pageSize,user.getId());
        List<TagVO> tagVOList=new ArrayList<>();
        for (Tag tag : pageInfo.getList()) {
            TagVO tagVO=new TagVO();
            tagVO.setAddress(tag.getAddress());
            tagVO.setId(tag.getId());
            tagVO.setLastonline(tag.getLastonline());
            tagVO.setUserId(tag.getUserId());
            if (tag.getX()!=null){
                tagVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagVO.setZ(tag.getZ());
            }
            tagVO.setElectric(tag.getElectric());
            tagVO.setAddressBroadcast(tag.getAddressBroadcast());
            tagVO.setUsed("未使用");
            //在线离线
            if ("1".equals(tag.getIsonline())){
                tagVO.setIsonline("在线");
            }
            if ("0".equals(tag.getIsonline())){
                tagVO.setIsonline("离线");
            }

            //标签类型
            if (tag.getTagTypeid()!=null){
                TagType tagType = tagTypeMapper.selectByPrimaryKey(tag.getTagTypeid());
                if (tagType!=null){
                    tagVO.setTagTypename(tagType.getName());
                }
            }
            //使用者
            if (tag.getAddress()!=null){
                Person person= personMapper.getPersonByOnlyAddress(tag.getAddress());
                if (person!=null){
                    tagVO.setType("person");
                    tagVO.setUsername(person.getPersonName());
                }else {
                    Goods goods=goodsMapper.getGoodsByOnlyAddress(tag.getAddress());
                    if (goods!=null){
                        tagVO.setType("goods");
                        tagVO.setUsername(goods.getGoodsName());
                    }
                }
                tagVOList.add(tagVO);
            }
        }
        PageInfo<TagVO> page= new PageInfo<>(tagVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(tagVOList);
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
     * 查看所有未使用标签信息
     * 不分页
     * */
    @RequestMapping(value = "getNoUsedTags",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getNoUsedTags(HttpServletRequest request){
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

        List<TagVO> tagList= tagService.getNoUsedTags(user.getId());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(tagList);
        resultBean.setSize(tagList.size());
        return resultBean;
    }

    /*
     * 新增标签类型
     * */

    @RequestMapping(value = "AddTagType",method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("tag_add")
    @Operation("新增标签类型")
    public ResultBean AddPersonType(@Valid TagType tagType, BindingResult result,
                                    HttpServletRequest request, MultipartFile file){


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
    //权限不够
        if (!"0".equals(user.getCreateUser())){
            resultBean =new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("权限不足,无法添加标签类型");
            List<Myuser> list=new ArrayList<>();
            list.add(user);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //标签类型已经存在
        TagType mytagtype=tagTypeService.getTagTypeByName(tagType.getName(),user.getId());
        if (mytagtype!=null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("类型已经存在,无法重复添加");
            List<PersonType> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        //设置标签类型创建时间和所属管理者
        tagType.setCreateTime(new Date());
        tagType.setUserId(user.getId());

        //设置图片路径
        //获取文件名加后缀
        if (file!=null){
            //保存图片的路径
            String filePath = "C:\\whzy\\locationsystem\\src\\main\\resources\\static\\tag";
            //获取原始图片的拓展名
            String originalFilename = file.getOriginalFilename();
            //新的文件名字
            String newFileName = UUID.randomUUID()+originalFilename;
            //封装上传文件位置的全路径
            File targetFile = new File(filePath,newFileName);
            //把本地文件上传到封装上传文件位置的全路径
            try {
                file.transferTo(targetFile);
                tagType.setImg(targetFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("标签图片上传失败");
                List<TagType> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        int insert = tagTypeService.insertSelective(tagType);

        if (insert>0){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("标签类型添加成功");
            List<TagType> list = new ArrayList<>();
            list.add(tagType);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签类型添加失败");
            List<TagType> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }
    /*
     * 查询所有标签类型
     * 分页
     * */
    @RequestMapping(value = "getTagTypesPage",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getTagTypesPage(HttpServletRequest request,
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

        PageInfo<TagType> pageInfo=tagTypeService.getTagTypesByUserId(pageIndex,pageSize);

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
     * 查询所有标签类型
     * 不分页
     * */
    @RequestMapping(value = "getTagTypes",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getTagTypes(HttpServletRequest request){
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

        List<TagType> tagTypes=tagTypeService.getTagTypes();
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(tagTypes);
        resultBean.setSize(tagTypes.size());
        return resultBean;
    }

    /*
     * 根据类型查询相关标签
     * 分页
     * */
    @RequestMapping(value = "getTagsByTypePage",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getTagsByTypePage(HttpServletRequest request,
                                             @RequestParam("tag_typeid") Integer typeid,
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


        PageInfo<Tag> pageInfo=tagService.getTagsByTypePage(pageIndex,pageSize,typeid,user.getId());

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
     * 根据类型查询相关标签
     * 不分页
     * */
    @RequestMapping(value = "getTagsByType",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getTagsByType(HttpServletRequest request,
                                        @RequestParam("tag_typeid") Integer typeid){
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

        List<Tag> tagList=tagService.getTagsByType(typeid,user.getId());
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(tagList);
        resultBean.setSize(tagList.size());
        return resultBean;
    }

    /*
     * 查看所有的标签信息
     * */
    @RequestMapping(value = "getAllTagLocation",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public AllTagLocationResult getAllTagLocation(HttpServletRequest request
                                  ){
        AllTagLocationResult allTag;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //System.out.println("过来一次标签请求：");
        //未登录
        if (user==null){
            allTag = new AllTagLocationResult();
            allTag.setCode(5);
            allTag.setMsg("还未登录");
            return allTag;
        }
        //得到用户下所有的标签
        List<Tag> usedTags = tagService.getUserTags(user.getId());
       // System.out.println(usedTags.size());
        allTag=new AllTagLocationResult();
        List<Location> personLocation =new ArrayList<>();
        List<GoodsLocation> goodsLocationList=new ArrayList<>();
        DateFormat sdf11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Tag tag : usedTags) {
            //System.out.println(tag.toString()+"1111111");
            if (tag.getAddress()!=null&&!"".equals(tag.getAddress())){
                //System.out.println(tag.toString()+"2222222");
                Map<String, String> map = SystemMap.getMap();
                String s = map.get(tag.getAddress());
               // System.out.println(s+"aaaaaaaaaaasd");
                if (s!=null&&!"".equals(s)){
                   // System.out.println(s+"bbbbbbbbbbbb");
                    String[] split = s.split(",");
                    tag.setX(Double.parseDouble(split[0]));
                    tag.setY(Double.parseDouble(split[1]));
                    tag.setZ(Double.parseDouble(split[2]));

                    long now = System.currentTimeMillis()/1000;
                    try {
                        //System.out.println("split长度:"+split.length);
                        if (split.length>3){
                            if (split[3]!=null&&!"".equals(split[3])){
                                //?for input string ""
                                long time = sdf11.parse(split[3]).getTime()/1000;
                                //System.out.println("当前时间-系统时间:"+(now-time));
                                if (now-time<UP_TIME){
                                    tag.setIsonline("1");
                                }else {
                                    tag.setIsonline("0");
                                }
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        tag.setLastonline(sdf11.parse(split[3]));
                    } catch (ParseException e) {
                        System.out.println("时间转换失败");
                    }

                }
                Person person= personService.getPersonByAddress(user.getId(),tag.getAddress());
                if (person!=null){
                   // System.out.println(person.toString());
                    Location location=new Location();
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

                    location.setTag(tag);
                    personLocation.add(location);
                }
            }
        }
        allTag.setPersonlocationList(personLocation);

        for (Tag tag : usedTags) {
            if (tag.getAddress()!=null&&!"".equals(tag.getAddress())){
                Goods goods=goodsService.getGoodsByAddress(user.getId(),tag.getAddress());

                if(goods!=null){
                    GoodsLocation location=new GoodsLocation();
                    location.setId(goods.getId());
                    location.setAddTime(goods.getAddTime());
                    location.setGoodsName(goods.getGoodsName());
                    location.setImg(goods.getImg());
                    location.setGoodsType(goodsTypeService.selectByPrimaryKey(goods.getGoodsTypeid()));
                    location.setUserId(goods.getUserId());
                    location.setTagAddress(goods.getTagAddress());
                    location.setGoodsIdcard(goods.getGoodsIdcard());
                    //location.setRank(goods.getRank());

                    location.setTag(tag);
                    goodsLocationList.add(location);
                }
            }

        }
        allTag.setGoodsLocations(goodsLocationList);

        allTag.setCode(1);
        allTag.setMsg("操作成功");
        return allTag;

    }

    /*
    * 查询在线标签
    * */
    @RequestMapping(value = "getOnlineTag",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public AllTagLocationResult getOnlineTag(HttpServletRequest request){
        AllTagLocationResult allTag;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            //未登录
            if (user==null){
                allTag = new AllTagLocationResult();
                allTag.setCode(5);
                allTag.setMsg("还未登录");
                return allTag;
            }
        }
        List<Tag> tagList=tagService.getOnlineTag(user.getId());
        allTag=new AllTagLocationResult();
        List<Location> personLocation =new ArrayList<>();
        List<GoodsLocation> goodsLocationList=new ArrayList<>();
        DateFormat sdf11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Tag tag : tagList) {
            //System.out.println(tag.toString()+"1111111");
            if (tag.getAddress()!=null&&!"".equals(tag.getAddress())){
                //System.out.println(tag.toString()+"2222222");
                Map<String, String> map = SystemMap.getMap();
                String s = map.get(tag.getAddress());
                // System.out.println(s+"aaaaaaaaaaasd");
                if (s!=null&&!"".equals(s)){
                    // System.out.println(s+"bbbbbbbbbbbb");
                    String[] split = s.split(",");
                    tag.setX(Double.parseDouble(split[0]));
                    tag.setY(Double.parseDouble(split[1]));
                    tag.setZ(Double.parseDouble(split[2]));

                    long now = System.currentTimeMillis()/1000;
                    try {
                        long time = sdf11.parse(split[3]).getTime()/1000;
                        System.out.println("当前时间-系统时间:"+(now-time));
                        if (now-time<UP_TIME){
                            tag.setIsonline("1");
                        }else {
                            tag.setIsonline("0");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        tag.setLastonline(sdf11.parse(split[3]));
                    } catch (ParseException e) {
                        System.out.println("时间转换失败");
                    }

                }
                Person person= personService.getPersonByAddress(user.getId(),tag.getAddress());
                if (person!=null){
                    // System.out.println(person.toString());
                    Location location=new Location();
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

                    location.setTag(tag);
                    personLocation.add(location);
                }
            }
        }
        allTag.setPersonlocationList(personLocation);

        for (Tag tag : tagList) {
            if (tag.getAddress()!=null&&!"".equals(tag.getAddress())){
                Goods goods=goodsService.getGoodsByAddress(user.getId(),tag.getAddress());

                if(goods!=null){
                    GoodsLocation location=new GoodsLocation();
                    location.setId(goods.getId());
                    location.setAddTime(goods.getAddTime());
                    location.setGoodsName(goods.getGoodsName());
                    location.setImg(goods.getImg());
                    location.setGoodsType(goodsTypeService.selectByPrimaryKey(goods.getGoodsTypeid()));
                    location.setUserId(goods.getUserId());
                    location.setTagAddress(goods.getTagAddress());
                    location.setGoodsIdcard(goods.getGoodsIdcard());
                    //location.setRank(goods.getRank());

                    location.setTag(tag);
                    goodsLocationList.add(location);
                }
            }

        }
        allTag.setGoodsLocations(goodsLocationList);

        allTag.setCode(1);
        allTag.setMsg("操作成功");
        return allTag;
    }
    /*
     * 查询离线标签
     * */
    @RequestMapping(value = "getOfflineTag",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public AllTagLocationResult getOfflineTag(HttpServletRequest request){
        AllTagLocationResult allTag;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            //未登录
            if (user==null){
                allTag = new AllTagLocationResult();
                allTag.setCode(5);
                allTag.setMsg("还未登录");
                return allTag;
            }
        }

        List<Tag> tagList=tagService.getOfflineTag(user.getId());
        allTag=new AllTagLocationResult();
        List<Location> personLocation =new ArrayList<>();
        List<GoodsLocation> goodsLocationList=new ArrayList<>();
        DateFormat sdf11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Tag tag : tagList) {
            //System.out.println(tag.toString()+"1111111");
            if (tag.getAddress()!=null&&!"".equals(tag.getAddress())){
                //System.out.println(tag.toString()+"2222222");
                Map<String, String> map = SystemMap.getMap();
                String s = map.get(tag.getAddress());
                // System.out.println(s+"aaaaaaaaaaasd");
                if (s!=null&&!"".equals(s)){
                    // System.out.println(s+"bbbbbbbbbbbb");
                    String[] split = s.split(",");
                    tag.setX(Double.parseDouble(split[0]));
                    tag.setY(Double.parseDouble(split[1]));
                    tag.setZ(Double.parseDouble(split[2]));

                    long now = System.currentTimeMillis()/1000;
                    try {
                        long time = sdf11.parse(split[3]).getTime()/1000;
                        System.out.println("当前时间-系统时间:"+(now-time));
                        if (now-time<UP_TIME){
                            tag.setIsonline("1");
                        }else {
                            tag.setIsonline("0");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        tag.setLastonline(sdf11.parse(split[3]));
                    } catch (ParseException e) {
                        System.out.println("时间转换失败");
                    }

                }
                Person person= personService.getPersonByAddress(user.getId(),tag.getAddress());
                if (person!=null){
                    // System.out.println(person.toString());
                    Location location=new Location();
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

                    location.setTag(tag);
                    personLocation.add(location);
                }
            }
        }
        allTag.setPersonlocationList(personLocation);

        for (Tag tag : tagList) {
            if (tag.getAddress()!=null&&!"".equals(tag.getAddress())){
                Goods goods=goodsService.getGoodsByAddress(user.getId(),tag.getAddress());

                if(goods!=null){
                    GoodsLocation location=new GoodsLocation();
                    location.setId(goods.getId());
                    location.setAddTime(goods.getAddTime());
                    location.setGoodsName(goods.getGoodsName());
                    location.setImg(goods.getImg());
                    location.setGoodsType(goodsTypeService.selectByPrimaryKey(goods.getGoodsTypeid()));
                    location.setUserId(goods.getUserId());
                    location.setTagAddress(goods.getTagAddress());
                    location.setGoodsIdcard(goods.getGoodsIdcard());
                    //location.setRank(goods.getRank());

                    location.setTag(tag);
                    goodsLocationList.add(location);
                }
            }

        }

        allTag.setCode(1);
        allTag.setMsg("操作成功");
        return allTag;
    }

    /*
    * 模糊查询
    * 一部分标签add查询标签
    * 不分页
    * */
    @RequestMapping(value = "getTagByCondition",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getTagByCondition(HttpServletRequest request,
                                        QueryTagCondition queryTagCondition){
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

        List<TagVO> tagList=tagService.getTagByCondition(user.getId(),queryTagCondition);

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(tagList);
        resultBean.setSize(tagList.size());
        return resultBean;
    }
    /*
     * 模糊查询
     * 一部分标签add查询标签
     * 分页
     * */
    @RequestMapping(value = "getTagByConditionPage",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getTagByConditionPage(HttpServletRequest request,
                                            @RequestParam(defaultValue = "1") Integer pageIndex,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                        QueryTagCondition queryTagCondition){
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

        PageInfo<Tag> pageInfo=tagService.getTagByConditionPage(pageIndex,pageSize,user.getId(),queryTagCondition);
        List<TagVO> tagVOList=new ArrayList<>();
        for (Tag tag : pageInfo.getList()) {
            TagVO tagVO=new TagVO();
            tagVO.setAddress(tag.getAddress());
            tagVO.setId(tag.getId());
            tagVO.setLastonline(tag.getLastonline());
            tagVO.setUserId(tag.getUserId());
            if (tag.getX()!=null){
                tagVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagVO.setZ(tag.getZ());
            }
            tagVO.setElectric(tag.getElectric());
            //在线离线
            if ("1".equals(tag.getIsonline())){
                tagVO.setIsonline("在线");
            }
            if ("0".equals(tag.getIsonline())){
                tagVO.setIsonline("离线");
            }

            //是否使用
            if ("0".equals(tag.getUsed())){
                tagVO.setUsed("未使用");
                tagVO.setUsername("无");
            }
            if ("1".equals(tag.getUsed())){
                tagVO.setUsed("已使用");
                //使用者
                if (tag.getAddress()!=null){
                    Person person= personMapper.getPersonByOnlyAddress(tag.getAddress());
                    if (person!=null){
                        tagVO.setType("person");
                        tagVO.setUsername(person.getPersonName());
                    }else {
                        Goods goods=goodsMapper.getGoodsByOnlyAddress(tag.getAddress());
                        if (goods!=null){
                            tagVO.setType("goods");
                            tagVO.setUsername(goods.getGoodsName());
                        }
                    }
                }
            }
            //标签类型
            if (tag.getTagTypeid()!=null){
                TagType tagType = tagTypeMapper.selectByPrimaryKey(tag.getTagTypeid());
                if (tagType!=null){
                    tagVO.setTagTypename(tagType.getName());
                }
            }
            tagVOList.add(tagVO);
        }
        PageInfo<TagVO> page= new PageInfo<>(tagVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(tagVOList);
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
     * 查看某个地图下所有的标签信息
     * */
    @RequestMapping(value = "getAllTagLocationByMap",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public AllTagLocationResult getAllTagLocationByMap(HttpServletRequest request,
                                                       @RequestParam(defaultValue = "",required = false) String MapUUID
    ){
        AllTagLocationResult allTag;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //System.out.println("过来一次标签请求：");
        //未登录
        if (user==null){
            allTag = new AllTagLocationResult();
            allTag.setCode(5);
            allTag.setMsg("还未登录");
            return allTag;
        }
        if (MapUUID==null||"".equals(MapUUID)){
            allTag = new AllTagLocationResult();
            allTag.setCode(-1);
            allTag.setMsg("该地图不存在");
            return allTag;
        }

        //得到地图下所有的标签
        List<Tag> usedTags=tagService.getTagsByMapUUID(MapUUID);
        allTag=new AllTagLocationResult();
        List<Location> personLocation =new ArrayList<>();
        List<GoodsLocation> goodsLocationList=new ArrayList<>();
        DateFormat sdf11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Tag tag : usedTags) {
            //System.out.println(tag.toString()+"1111111");
            if (tag.getAddress()!=null&&!"".equals(tag.getAddress())){
                //System.out.println(tag.toString()+"2222222");
                Map<String, String> map = SystemMap.getMap();
                String s = map.get(tag.getAddress());
                // System.out.println(s+"aaaaaaaaaaasd");
                if (s!=null&&!"".equals(s)){
                    // System.out.println(s+"bbbbbbbbbbbb");
                    String[] split = s.split(",");
                    tag.setX(Double.parseDouble(split[0]));
                    tag.setY(Double.parseDouble(split[1]));
                    tag.setZ(Double.parseDouble(split[2]));

                    long now = System.currentTimeMillis()/1000;
                    try {
                        //System.out.println("split长度:"+split.length);
                        if (split.length>3){
                            if (split[3]!=null&&!"".equals(split[3])){
                                //?for input string ""
                                long time = sdf11.parse(split[3]).getTime()/1000;
                                //System.out.println("当前时间-系统时间:"+(now-time));
                                if (now-time<UP_TIME){
                                    tag.setIsonline("1");
                                }else {
                                    tag.setIsonline("0");
                                }
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        tag.setLastonline(sdf11.parse(split[3]));
                    } catch (ParseException e) {
                        System.out.println("时间转换失败");
                    }

                }
                Person person= personService.getPersonByAddress(user.getId(),tag.getAddress());
                if (person!=null){
                    // System.out.println(person.toString());
                    Location location=new Location();
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

                    location.setTag(tag);
                    personLocation.add(location);
                }
            }
        }

        allTag.setPersonlocationList(personLocation);

        for (Tag tag : usedTags) {
            if (tag.getAddress()!=null&&!"".equals(tag.getAddress())){
                Goods goods=goodsService.getGoodsByAddress(user.getId(),tag.getAddress());

                if(goods!=null){
                    GoodsLocation location=new GoodsLocation();
                    location.setId(goods.getId());
                    location.setAddTime(goods.getAddTime());
                    location.setGoodsName(goods.getGoodsName());
                    location.setImg(goods.getImg());
                    location.setGoodsType(goodsTypeService.selectByPrimaryKey(goods.getGoodsTypeid()));
                    location.setUserId(goods.getUserId());
                    location.setTagAddress(goods.getTagAddress());
                    location.setGoodsIdcard(goods.getGoodsIdcard());
                    //location.setRank(goods.getRank());

                    location.setTag(tag);
                    goodsLocationList.add(location);
                }
            }

        }
        allTag.setGoodsLocations(goodsLocationList);

        allTag.setCode(1);
        allTag.setMsg("操作成功");
        allTag.setSize(personLocation.size()+goodsLocationList.size());
        return allTag;
    }

    /*
    *
    * 删除标签
    * */
    @RequestMapping(value = "delTag",method = RequestMethod.POST)
    @ResponseBody
    @Operation("删除标签")
    public ResultBean delTag(HttpServletRequest request,
                             @RequestParam("") String tagAddress){
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
        if (tagAddress==null||"".equals(tagAddress)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签address不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Tag tag = tagService.getTagByOnlyAddress(tagAddress);
        if (tag==null){


            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if ("1".equals(tag.getUsed())){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该标签被别人使用,请先解绑");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        int del = tagService.deleteByPrimaryKey(tag.getId());
        if (del>0){
            //把该标签在缓存中的记录清掉
            SystemMap.getUsermap().remove(tag.getAddress());
            SystemMap.getTagAndPersonMap().remove(tag.getAddress());
            SystemMap.getCountmap().remove(tag.getAddress());


            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List<Tag> list=new ArrayList<>();
            list.add(tag);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签删除失败");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }

    /*
     * 查看标签在线时长top10
     * 不分页
     * */
    @RequestMapping(value = "getLongTimeTagTop10",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("tag_select")
    public ResultBean getLongTimeTagTop10(HttpServletRequest request){
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
        List<TagVO2> tagVO2List = new ArrayList<>();
        List<Tag> tagList= tagService.getUserTags(user.getId());
        for (Tag tempTag : tagList) {
            TagVO2 tagVO2 = new TagVO2();
            tagVO2.setAddress(tempTag.getAddress());
            tagVO2.setId(tempTag.getId());
            tagVO2.setIsonline(tempTag.getIsonline());
            tagVO2.setMapKey(tempTag.getMapKey());
            tagVO2.setUserId(tempTag.getUserId());
            //时间非空处理
            tagVO2.setOnlineTime(tempTag.getLastonline().getTime() - tempTag.getLastoffline().getTime());
            tagVO2List.add(tagVO2);
        }

        Collections.sort(tagVO2List, new Comparator<TagVO2>() {
            @Override
            public int compare(TagVO2 o1, TagVO2 o2) {
                int i = (int) (o2.getOnlineTime() - o1.getOnlineTime());
                if (i == 0){
                    return o2.getId() - o1.getId();
                }
                return i;
            }
        });

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(tagVO2List.subList(0,tagVO2List.size() >= 10 ? 10 : tagVO2List.size()));
        resultBean.setSize(tagVO2List.size() >= 10 ? 10 : tagVO2List.size());
        return resultBean;
    }

    /*
    * 批量添加标签
    *
    * */
    @RequestMapping(value = "AddTagBatch",method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("tag_addBatch")
    @Operation("批量添加标签")
    public ResultBean importEmployee(@RequestParam(value="AddTagBatch",required=false) MultipartFile file,
                                     HttpServletRequest request) throws IOException {
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
        if (file==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("上传文件不能为空");
            List<Tag> list=new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        if (!file.getOriginalFilename().endsWith(".xls") && !file.getOriginalFilename().endsWith(".xlsx")) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("上传文件格式不正确");
            List<Tag> list=new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        List<Tag> tags =
                PoiUtils.importTagByExcel
                        (file);//解析上传的excel，并获取到里面单元格的值
            if (tags==null||tags.size()==0){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("批量添加失敗");
                List<Tag> list=new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            List<Tag> tagList=new ArrayList<>();
        for (Tag tag : tags) {
            //标签已经存在
            Tag mytag=tagService.getTagByAddress(tag.getAddress());
            if (mytag!=null){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("该标签已经存在");
                List<String> list = new ArrayList<>();
                list.add(tag.getAddress());
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
           tagList.add(tag);
        }
        for (Tag tag : tagList) {
            //设置标签所属以及使用状态 0:未使用,未绑定 1:使用,绑定
            tag.setUsed("0");
            tag.setUserId(user.getId());
            tag.setLastonline(new Date());
            tag.setLastoffline(new Date());
            //添加
            tagService.insertSelective(tag);
        }

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("添加标签成功");
        resultBean.setData(tagList);
        resultBean.setSize(tagList.size());
        return resultBean;
    }
}
