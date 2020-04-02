package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.mapper.*;
import com.tg.locationsystem.pojo.*;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.StringUtils;
import com.tg.locationsystem.utils.SystemMap;
import org.apache.ibatis.annotations.Param;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author hyy
 * @ Date2019/7/25
 */
@Controller
@RequestMapping("alert")
public class AlertController {
@Autowired
private ITagStatusService tagStatusService;
@Autowired
private IHeartRateSetService heartRateSetService;
@Autowired
private IHeartRateHistoryService heartRateHistoryService;
@Autowired
private PersonMapper personMapper;
@Autowired
private GoodsTypeMapper goodsTypeMapper;
@Autowired
private GoodsMapper goodsMapper;
@Autowired
private PersonTypeMapper personTypeMapper;
@Autowired
private TagMapper tagMapper;
@Autowired
private IMapService mapService;
@Autowired
private IAlertSetService alertSetService;
@Autowired
private IFrenceHistoryService frenceHistoryService;

@Autowired
private IEleCallSetService eleCallSetService;
    DateFormat sdf
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /*
     * 查看所有报警信息
     * */
    @RequestMapping(value = "getTagStatus",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("alert_tag_select")
    public ResultBean getTagStatus(HttpServletRequest request,
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
        System.out.println(user.toString());
        PageInfo<TagStatus> pageInfo=tagStatusService.getTagStatusByUserId(pageIndex,pageSize,user.getId());

        List<TagStatusVO> tagStatusVOList=new ArrayList<>();
        for (TagStatus tagStatus : pageInfo.getList()) {
            TagStatusVO tagStatusVO=new TagStatusVO();
            tagStatusVO.setId(tagStatus.getId());
            tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
            tagStatusVO.setData(tagStatus.getData());
            tagStatusVO.setAlertType(tagStatus.getAlertType());
            tagStatusVO.setAddTime(tagStatus.getAddTime());
            tagStatusVO.setUserId(tagStatus.getUserId());
            tagStatusVO.setMapkey(tagStatus.getMapKey());
            tagStatusVO.setIsdeal(tagStatus.getIsdeal());
            tagStatusVO.setVedio(tagStatus.getVedio());
            //type name
            Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
            if (person!=null){
                PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
                if (personType!=null){
                    tagStatusVO.setType(personType.getTypeName());
                }
                tagStatusVO.setName(person.getPersonName());
                tagStatusVO.setImg(person.getImg());
            }else {
                Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
                if (goods!=null){
                    GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                    if (goodsType!=null){
                        tagStatusVO.setType(goodsType.getName());
                    }
                    tagStatusVO.setName(goods.getGoodsName());
                    tagStatusVO.setImg(goods.getImg());
                }
            }
            Person sqlperson = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
            Tag tag = tagMapper.getTagByOnlyAddress(sqlperson.getTagAddress());
            if (tag!=null){
                if (tag.getX()!=null){
                    tagStatusVO.setX(tag.getX());
                }
                if (tag.getY()!=null){
                    tagStatusVO.setY(tag.getY());
                }
                if (tag.getZ()!=null){
                    tagStatusVO.setZ(tag.getZ());
                }
            }
            tagStatusVOList.add(tagStatusVO);
        }
        PageInfo<TagStatusVO> page= new PageInfo<>(tagStatusVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(tagStatusVOList);
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
     * 查看所有报警信息
     * 不分页
     * */
    @RequestMapping(value = "getTagStatusNoPg",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("alert_tag_select")
    public ResultBean getTagStatusNoPg(HttpServletRequest request) {
        //System.out.println(System.currentTimeMillis());
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
        List<TagStatus> tagStatusList=tagStatusService.getTagStatusByUserIdNoPg(user.getId());

        Map<String,List<TagStatus>> tagByPersonIdcard = tagStatusList.stream().collect(groupingBy(TagStatus::getPersonIdcard));

        //System.out.println(System.currentTimeMillis());
        List<TagStatusVO> tagStatusVOList=new ArrayList<>();

        for (String personIdcard : tagByPersonIdcard.keySet()){
            String name = "";
            Person person = personMapper.getPersonByIdCard(personIdcard);
            if (person!=null){
                name = person.getPersonName();
            }else {
                Goods goods = goodsMapper.getGoodsByByIdCard(personIdcard);
                if (goods!=null){
                    name = goods.getGoodsName();
                }
            }
            for (TagStatus tagStatus : tagByPersonIdcard.get(personIdcard)){
                TagStatusVO tagStatusVO=new TagStatusVO();
                tagStatusVO.setId(tagStatus.getId());
                tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
                tagStatusVO.setData(tagStatus.getData());
                tagStatusVO.setAlertType(tagStatus.getAlertType());
                tagStatusVO.setAddTime(tagStatus.getAddTime());
                tagStatusVO.setUserId(tagStatus.getUserId());
                tagStatusVO.setMapkey(tagStatus.getMapKey());
                tagStatusVO.setIsdeal(tagStatus.getIsdeal());
                tagStatusVO.setVedio(tagStatus.getVedio());
                tagStatusVO.setName(name);
                tagStatusVOList.add(tagStatusVO);
            }
        }

//        for (TagStatus tagStatus : tagStatusList) {
//            TagStatusVO tagStatusVO=new TagStatusVO();
//            tagStatusVO.setId(tagStatus.getId());
//            tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
//            tagStatusVO.setData(tagStatus.getData());
//            tagStatusVO.setAlertType(tagStatus.getAlertType());
//            tagStatusVO.setAddTime(tagStatus.getAddTime());
//            tagStatusVO.setUserId(tagStatus.getUserId());
//            tagStatusVO.setMapkey(tagStatus.getMapKey());
//            tagStatusVO.setIsdeal(tagStatus.getIsdeal());
//            tagStatusVO.setVedio(tagStatus.getVedio());
//            //type name
//            Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
//            if (person!=null){
//                tagStatusVO.setName(person.getPersonName());
//                tagStatusVO.setImg(person.getImg());
//            }else {
//                Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
//                if (goods!=null){
//                    tagStatusVO.setName(goods.getGoodsName());
//                    tagStatusVO.setImg(goods.getImg());
//                }
//            }
//
//            tagStatusVOList.add(tagStatusVO);
//        }
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(tagStatusVOList);
        resultBean.setSize(tagStatusVOList.size());
        //System.out.println(System.currentTimeMillis());
        return resultBean;
    }


    /*
     * 根据报警类型查看相关报警信息
     * */
    @RequestMapping(value = "getTagStatusByType",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("alert_tag_select")
    public ResultBean getTagStatusByType(HttpServletRequest request,
                                         @RequestParam(defaultValue = "") String typeid,
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
        if (typeid==null||"".equals(typeid)){

            PageInfo<TagStatus> pageInfo=tagStatusService.getTagStatusByUserId(pageIndex,pageSize,user.getId());
            List<TagStatusVO> tagStatusVOList=new ArrayList<>();
            for (TagStatus tagStatus : pageInfo.getList()) {
                TagStatusVO tagStatusVO=new TagStatusVO();
                tagStatusVO.setId(tagStatus.getId());
                tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
                tagStatusVO.setData(tagStatus.getData());
                tagStatusVO.setAlertType(tagStatus.getAlertType());
                tagStatusVO.setAddTime(tagStatus.getAddTime());
                tagStatusVO.setUserId(tagStatus.getUserId());
                tagStatusVO.setMapkey(tagStatus.getMapKey());
                tagStatusVO.setIsdeal(tagStatus.getIsdeal());
                tagStatusVO.setVedio(tagStatus.getVedio());
                if ("0".equals(tagStatus.getIsdeal())){
                    tagStatusVO.setIsdeal("未处理");
                }
                if ("1".equals(tagStatus.getIsdeal())){
                    tagStatusVO.setIsdeal("已处理");
                }

                //type name
                Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
                if (person!=null){
                    PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
                    if (personType!=null){
                        tagStatusVO.setType(personType.getTypeName());
                    }
                    tagStatusVO.setName(person.getPersonName());
                    tagStatusVO.setImg(person.getImg());
                }else {
                    Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
                    if (goods!=null){
                        GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                        if (goodsType!=null){
                            tagStatusVO.setType(goodsType.getName());
                        }
                        tagStatusVO.setName(goods.getGoodsName());
                        tagStatusVO.setImg(goods.getImg());
                    }
                }
                Person sqlperson = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
                Tag tag = tagMapper.getTagByOnlyAddress(sqlperson.getTagAddress());
                if (tag!=null){
                    if (tag.getX()!=null){
                        tagStatusVO.setX(tag.getX());
                    }
                    if (tag.getY()!=null){
                        tagStatusVO.setY(tag.getY());
                    }
                    if (tag.getZ()!=null){
                        tagStatusVO.setZ(tag.getZ());
                    }
                }
                tagStatusVOList.add(tagStatusVO);
            }
            PageInfo<TagStatusVO> page= new PageInfo<>(tagStatusVOList);
            page.setPageNum(pageInfo.getPageNum());
            page.setSize(pageInfo.getSize());
            page.setSize(pageInfo.getSize());
            page.setStartRow(pageInfo.getStartRow());
            page.setEndRow(pageInfo.getEndRow());
            page.setTotal(pageInfo.getTotal());
            page.setPages(pageInfo.getPages());
            page.setList(tagStatusVOList);
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
        PageInfo<TagStatus> pageInfo=tagStatusService.getTagStatusByType(pageIndex,pageSize,user.getId(),typeid);
        List<TagStatusVO> tagStatusVOList=new ArrayList<>();

        for (TagStatus tagStatus : pageInfo.getList()) {
            TagStatusVO tagStatusVO=new TagStatusVO();
            tagStatusVO.setId(tagStatus.getId());
            tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
            tagStatusVO.setData(tagStatus.getData());
            tagStatusVO.setAlertType(tagStatus.getAlertType());
            tagStatusVO.setAddTime(tagStatus.getAddTime());
            tagStatusVO.setUserId(tagStatus.getUserId());
            tagStatusVO.setMapkey(tagStatus.getMapKey());
            tagStatusVO.setIsdeal(tagStatus.getIsdeal());
            tagStatusVO.setX(tagStatus.getX());
            tagStatusVO.setY(tagStatus.getY());
            tagStatusVO.setZ(tagStatus.getZ());
            tagStatusVO.setVedio(tagStatus.getVedio());
            if ("0".equals(tagStatus.getIsdeal())){
                tagStatusVO.setIsdeal("未处理");
            }
            if ("1".equals(tagStatus.getIsdeal())){
                tagStatusVO.setIsdeal("已处理");
            }
            //type name
            Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
            if (person!=null){
                PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
                if (personType!=null){
                    tagStatusVO.setType(personType.getTypeName());
                }
                tagStatusVO.setName(person.getPersonName());
                tagStatusVO.setImg(person.getImg());
            }else {
                Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
                if (goods!=null){
                    GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                    if (goodsType!=null){
                        tagStatusVO.setType(goodsType.getName());
                    }
                    tagStatusVO.setName(goods.getGoodsName());
                    tagStatusVO.setImg(goods.getImg());
                }
            }


            tagStatusVOList.add(tagStatusVO);
        }
        PageInfo<TagStatusVO> page= new PageInfo<>(tagStatusVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(tagStatusVOList);
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
    * 设置心率
    *
    * */
    @RequestMapping(value = "setHeartRate",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("systemSet_heartRange")
    public ResultBean setHeartRate(@Valid HeartRateSet heartRateSet , BindingResult result,HttpServletRequest request
                                   ) {
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
        int data = heartRateSet.getMaxData() - heartRateSet.getMinData();
        if (data<=0){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("最小心率不能大于等于最大心率");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        //System.out.println(heartRateSet);
        HeartRateSet heart=heartRateSetService.getHeartRateSet(user.getId());
        //存在就更新,否则就创建
        if (heart==null){
            heart.setMaxData(heartRateSet.getMaxData());
            heart.setMinData(heartRateSet.getMinData());
            heart.setUpdateTime(new Date());
            heart.setUserId(user.getId());
            int insert = heartRateSetService.insertSelective(heartRateSet);
            if (insert>0){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("设置心率成功");
                List list=new ArrayList<>();
                list.add(heart);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("设置心率失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            heart.setMaxData(heartRateSet.getMaxData());
            heart.setMinData(heartRateSet.getMinData());
            heart.setUpdateTime(new Date());
            int update = heartRateSetService.updateByPrimaryKeySelective(heart);
            if (update>0){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("设置心率成功");
                List list=new ArrayList<>();
                list.add(heart);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("设置心率失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
    }
/*
* 根据标人员唯一标识 ,开始时间,结束时间查看心率记录
* */
@RequestMapping(value = "getHeartRateHistoryByAddAndTime",method = RequestMethod.GET)
@ResponseBody
@RequiresPermissions("date_heart")
public ResultBean getHeartRateHistoryByAddAndTime(HttpServletRequest request,
                                     @Valid HeartRateHistoryCondition historyCondition,BindingResult result,
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
    //2019-08-05 19:00:00
    String startTime = historyCondition.getStartTime();
    String endTime = historyCondition.getEndTime();
    try {
        long start = sdf.parse(startTime).getTime() / 1000;
        long end = sdf.parse(endTime).getTime() / 1000;
        System.out.println("心率记录:"+(end-start));
        if (end-start<0){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("起始时间大于结束时间");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        List<HeartRateHistory> histories=heartRateHistoryService.getheartRateHistoryByCondition(historyCondition.getPersonIdcard(),historyCondition.getStartTime(),historyCondition.getEndTime());
        if (histories.size()==0) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该标签不存在或无心率记录");
            List<Path> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(histories);
        resultBean.setSize(histories.size());
        return resultBean;
    } catch (ParseException e) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("时间转换失败");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

}

    @RequestMapping(value = "getHeartRateHistoryByAddData",method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("date_heart")
    public ResultBean getHeartRateHistoryByAddAndTimeData(HttpServletRequest request,
                                                          @RequestParam(value = "personIdcards[]",required=true) List<String> personIdcards) {

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
        if (personIdcards == null || personIdcards.size() == 0) {
            List<String> errorlist=new ArrayList<>();
            resultBean =new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("请选择查看人员");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }
        List<HeartRateHistory> histories=heartRateHistoryService.getheartRateHistoryByPersonIdcards(personIdcards);
        if (histories.size()==0) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该标签不存在或无心率记录");
            List<Path> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(histories);
        resultBean.setSize(histories.size());
        return resultBean;

    }




/*
* 将报警类型转为已处理
* */
@RequestMapping(value = "dealTagStatus",method = RequestMethod.POST)
@ResponseBody
@RequiresPermissions("alert_tag_update")
public ResultBean dealTagStatus(HttpServletRequest request,
                                @Param("") Integer tagStatusid) {

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
    if (tagStatusid == null) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("标签报警id不能为空");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    TagStatus tagStatus = tagStatusService.selectByPrimaryKey(tagStatusid);
    if (tagStatus != null) {
        tagStatus.setIsdeal("1");
        int i = tagStatusService.updateByPrimaryKeySelective(tagStatus);
        if (i > 0) {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("标签报警处理成功");
            List<TagStatus> list = new ArrayList<>();
            list.add(tagStatus);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签报警处理失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
    resultBean = new ResultBean();
    resultBean.setCode(-1);
    resultBean.setMsg("标签报警处理失败");
    List<Myuser> list = new ArrayList<>();
    resultBean.setData(list);
    resultBean.setSize(list.size());
    return resultBean;
}
/*
* 关键字搜索报警记录
* */
@RequestMapping(value = "getTagStatusByCondition",method = RequestMethod.GET)
@ResponseBody
@RequiresPermissions("alert_tag_select")
public ResultBean getTagStatusByCondition(HttpServletRequest request,
                                @RequestParam(defaultValue = "") String msg,
                                @RequestParam(defaultValue = "") String typeid,
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
    if (typeid==null||"".equals(typeid)){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("类型id不能为空");
        List<TagStatusVO> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    if ("".equals(msg)){
        PageInfo<TagStatus> pageInfo=tagStatusService.getTagStatusByTypeId(pageIndex,pageSize,user.getId(),typeid);

        List<TagStatusVO> tagStatusVOList=new ArrayList<>();
        for (TagStatus tagStatus : pageInfo.getList()) {
            TagStatusVO tagStatusVO=new TagStatusVO();
            tagStatusVO.setId(tagStatus.getId());
            tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
            tagStatusVO.setData(tagStatus.getData());
            tagStatusVO.setAlertType(tagStatus.getAlertType());
            tagStatusVO.setAddTime(tagStatus.getAddTime());
            tagStatusVO.setUserId(tagStatus.getUserId());
            tagStatusVO.setMapkey(tagStatus.getMapKey());
            tagStatusVO.setIsdeal(tagStatus.getIsdeal());
            tagStatusVO.setVedio(tagStatus.getVedio());
            if ("0".equals(tagStatus.getIsdeal())){
                tagStatusVO.setIsdeal("未处理");
            }
            if ("1".equals(tagStatus.getIsdeal())){
                tagStatusVO.setIsdeal("已处理");
            }
            //type name
            Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
            if (person!=null){
                PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
                if (personType!=null){
                    tagStatusVO.setType(personType.getTypeName());
                }
                tagStatusVO.setName(person.getPersonName());
                tagStatusVO.setImg(person.getImg());
            }else {
                Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
                if (goods!=null){
                    GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                    if (goodsType!=null){
                        tagStatusVO.setType(goodsType.getName());
                    }
                    tagStatusVO.setName(goods.getGoodsName());
                    tagStatusVO.setImg(goods.getImg());
                }
            }
            Person sqlperson = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
            Tag tag = tagMapper.getTagByOnlyAddress(sqlperson.getTagAddress());
            if (tag!=null){
                if (tag.getX()!=null){
                    tagStatusVO.setX(tag.getX());
                }
                if (tag.getY()!=null){
                    tagStatusVO.setY(tag.getY());
                }
                if (tag.getZ()!=null){
                    tagStatusVO.setZ(tag.getZ());
                }
            }
            tagStatusVOList.add(tagStatusVO);
        }
        PageInfo<TagStatusVO> page= new PageInfo<>(tagStatusVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(tagStatusVOList);
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
    PageInfo<TagStatus> pageInfo=tagStatusService.getTagStatusByCondition(pageIndex,pageSize,user.getId(),msg,typeid);

    List<TagStatusVO> tagStatusVOList=new ArrayList<>();
    for (TagStatus tagStatus : pageInfo.getList()) {
        TagStatusVO tagStatusVO=new TagStatusVO();
        tagStatusVO.setId(tagStatus.getId());
        tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
        tagStatusVO.setData(tagStatus.getData());
        tagStatusVO.setAlertType(tagStatus.getAlertType());
        tagStatusVO.setAddTime(tagStatus.getAddTime());
        tagStatusVO.setUserId(tagStatus.getUserId());
        tagStatusVO.setMapkey(tagStatus.getMapKey());
        tagStatusVO.setIsdeal(tagStatus.getIsdeal());
        tagStatusVO.setVedio(tagStatus.getVedio());
        if ("0".equals(tagStatus.getIsdeal())){
            tagStatusVO.setIsdeal("未处理");
        }
        if ("1".equals(tagStatus.getIsdeal())){
            tagStatusVO.setIsdeal("已处理");
        }
        //type name
        Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
        if (person!=null){
            PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
            if (personType!=null){
                tagStatusVO.setType(personType.getTypeName());
            }
            tagStatusVO.setName(person.getPersonName());
            tagStatusVO.setImg(person.getImg());
        }else {
            Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
            if (goods!=null){
                GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                if (goodsType!=null){
                    tagStatusVO.setType(goodsType.getName());
                }
                tagStatusVO.setName(goods.getGoodsName());
                tagStatusVO.setImg(goods.getImg());
            }
        }
        Person sqlperson = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
        Tag tag = tagMapper.getTagByOnlyAddress(sqlperson.getTagAddress());
        if (tag!=null){
            if (tag.getX()!=null){
                tagStatusVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagStatusVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagStatusVO.setZ(tag.getZ());
            }
        }
        tagStatusVOList.add(tagStatusVO);
    }
    PageInfo<TagStatusVO> page= new PageInfo<>(tagStatusVOList);
    page.setPageNum(pageInfo.getPageNum());
    page.setSize(pageInfo.getSize());
    page.setSize(pageInfo.getSize());
    page.setStartRow(pageInfo.getStartRow());
    page.setEndRow(pageInfo.getEndRow());
    page.setTotal(pageInfo.getTotal());
    page.setPages(pageInfo.getPages());
    page.setList(tagStatusVOList);
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
* 删除告警信息
* */
@RequestMapping(value = "deleteTagStatus",method = RequestMethod.POST)
@ResponseBody
@RequiresPermissions("alert_tag_delete")
public ResultBean deleteTagStatus(HttpServletRequest request,
                               @RequestParam("") Integer tagStatusid){
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
    if (tagStatusid==null||"".equals(tagStatusid)){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("告警记录id不能为空");
        List<TagStatus> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    int delete = tagStatusService.deleteByPrimaryKey(tagStatusid);
    if (delete>0){
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list=new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }else {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("删除告警记录失败");
        List<TagStatus> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

}
/*
* 查看所有已处理的报警
* */
@RequestMapping(value = "getAllTagStatusByDeal",method = RequestMethod.GET)
@ResponseBody
@RequiresPermissions("alert_tag_select")
public ResultBean getAllTagStatusByDeal(HttpServletRequest request,
                                        @RequestParam(defaultValue = "") String typeid,
                                        @RequestParam(defaultValue = "") String isdeal,
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
    if (typeid==null||"".equals(typeid)){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("类型id不能为空");
        List<TagStatusVO> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    if (isdeal==null||"".equals(isdeal)){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("处理参数不能为空");
        List<TagStatusVO> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    if (!"0".equals(isdeal)&&!"1".equals(isdeal)){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("参数有误");
        List<TagStatusVO> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    PageInfo<TagStatus> pageInfo=tagStatusService.getAllTagStatusByDeal(pageIndex,pageSize,user.getId(),typeid,isdeal);

    List<TagStatusVO> tagStatusVOList=new ArrayList<>();
    for (TagStatus tagStatus : pageInfo.getList()) {
        TagStatusVO tagStatusVO=new TagStatusVO();
        tagStatusVO.setId(tagStatus.getId());
        tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
        tagStatusVO.setData(tagStatus.getData());
        tagStatusVO.setAlertType(tagStatus.getAlertType());
        tagStatusVO.setAddTime(tagStatus.getAddTime());
        tagStatusVO.setUserId(tagStatus.getUserId());
        tagStatusVO.setMapkey(tagStatus.getMapKey());
        tagStatusVO.setIsdeal(tagStatus.getIsdeal());
        tagStatusVO.setVedio(tagStatus.getVedio());
        //type name
        Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
        if (person!=null){
            PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
            if (personType!=null){
                tagStatusVO.setType(personType.getTypeName());
            }
            tagStatusVO.setName(person.getPersonName());
            tagStatusVO.setImg(person.getImg());
        }else {
            Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
            if (goods!=null){
                GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                if (goodsType!=null){
                    tagStatusVO.setType(goodsType.getName());
                }
                tagStatusVO.setName(goods.getGoodsName());
                tagStatusVO.setImg(goods.getImg());
            }
        }
        Person sqlperson = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
        Tag tag = tagMapper.getTagByOnlyAddress(sqlperson.getTagAddress());
        if (tag!=null){
            if (tag.getX()!=null){
                tagStatusVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagStatusVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagStatusVO.setZ(tag.getZ());
            }
        }
        tagStatusVOList.add(tagStatusVO);
    }
    PageInfo<TagStatusVO> page= new PageInfo<>(tagStatusVOList);
    page.setPageNum(pageInfo.getPageNum());
    page.setSize(pageInfo.getSize());
    page.setSize(pageInfo.getSize());
    page.setStartRow(pageInfo.getStartRow());
    page.setEndRow(pageInfo.getEndRow());
    page.setTotal(pageInfo.getTotal());
    page.setPages(pageInfo.getPages());
    page.setList(tagStatusVOList);
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
* 设置sos报警开关
* */
@RequestMapping(value = "setSoS",method = RequestMethod.POST)
@ResponseBody
@RequiresPermissions("systemSet_sos")
public ResultBean setSoS(HttpServletRequest request,
                                        @RequestParam(defaultValue = "") String setSoS) {

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
    if (setSoS==null||"".equals(setSoS)){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("报警参数不能为空");
        List list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }


    AlertSet alertSet=null;
            alertSet=alertSetService.getAlertSetByUserId(user.getId());
    //若该开关不存在,生成一个开关,默认全部开启
    if (alertSet==null){
        alertSet=new AlertSet();
        alertSet.setUserId(user.getId());
        alertSet.setUpdateTime(new Date());
        alertSet.setSosAlert("1");
        alertSet.setHeartAlert("1");
        alertSet.setCutAlert("1");
        //生成开关
        alertSetService.insertSelective(alertSet);
        //将开关放到缓存
        if (!SystemMap.getSosList().contains(alertSet.getUserId())){
            SystemMap.getSosList().add(alertSet.getUserId());
        }
    }
    if ("1".equals(setSoS)){
        alertSet.setSosAlert(setSoS);
        //更新数据库
        int update = alertSetService.updateByPrimaryKeySelective(alertSet);
        if (update>0){
            //将开关放到缓存
            if (!SystemMap.getSosList().contains(alertSet.getUserId())){
                SystemMap.getSosList().add(alertSet.getUserId());
            }
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List<AlertSet> list=new ArrayList<>();
            list.add(alertSet);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("报警开关设置失败");
            List<AlertSet> list=new ArrayList<>();
            list.add(alertSet);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }else if ("0".equals(setSoS)){
        alertSet.setSosAlert(setSoS);
        //更新数据库
        int update = alertSetService.updateByPrimaryKeySelective(alertSet);
        if (update>0){
            //将开关放从缓存清除
                SystemMap.getSosList().remove(new Integer(alertSet.getUserId()));

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List<AlertSet> list=new ArrayList<>();
            list.add(alertSet);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("报警开关设置失败");
            List<AlertSet> list=new ArrayList<>();
            list.add(alertSet);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }else {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("报警开关设置失败");
        List<AlertSet> list=new ArrayList<>();
        list.add(alertSet);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}
    /*
     * 设置心率报警开关
     * */
    @RequestMapping(value = "setHeart",method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("systemSet_heart")
    public ResultBean setHeart(HttpServletRequest request,
                             @RequestParam(defaultValue = "") String setHeart) {
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
        if (setHeart==null||"".equals(setHeart)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("报警参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        AlertSet alertSet=null;
        alertSet=alertSetService.getAlertSetByUserId(user.getId());
        //若该开关不存在,生成一个开关,默认全部开启
        if (alertSet==null){
            alertSet=new AlertSet();
            alertSet.setUserId(user.getId());
            alertSet.setUpdateTime(new Date());
            alertSet.setSosAlert("1");
            alertSet.setHeartAlert("1");
            alertSet.setCutAlert("1");
            //生成开关
            alertSetService.insertSelective(alertSet);
            //将开关放到缓存
            if (!SystemMap.getSosList().contains(alertSet.getUserId())){
                SystemMap.getSosList().add(alertSet.getUserId());
            }
        }
        if ("1".equals(setHeart)){
            alertSet.setHeartAlert(setHeart);
            //更新数据库
            int update = alertSetService.updateByPrimaryKeySelective(alertSet);
            if (update>0){
                //将开关放到缓存
                if (!SystemMap.getHeartList().contains(alertSet.getUserId())){
                    SystemMap.getHeartList().add(alertSet.getUserId());
                }
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("操作成功");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("报警开关设置失败");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }

        }else if ("0".equals(setHeart)){
            alertSet.setHeartAlert(setHeart);
            //更新数据库
            int update = alertSetService.updateByPrimaryKeySelective(alertSet);
            if (update>0){
                //将开关放从缓存清除
                SystemMap.getHeartList().remove(new Integer(alertSet.getUserId()));

                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("操作成功");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("报警开关设置失败");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("报警开关设置失败");
            List<AlertSet> list=new ArrayList<>();
            list.add(alertSet);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
    /*
     * 设置剪断报警开关
     * */
    @RequestMapping(value = "setCut",method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("systemSet_cut")
    public ResultBean setCut(HttpServletRequest request,
                               @RequestParam(defaultValue = "") String setCut) {
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
        if (setCut==null||"".equals(setCut)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("报警参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        AlertSet alertSet=null;
                alertSet=alertSetService.getAlertSetByUserId(user.getId());
        //若该开关不存在,生成一个开关,默认全部开启
        if (alertSet==null){
            alertSet=new AlertSet();
            alertSet.setUserId(user.getId());
            alertSet.setUpdateTime(new Date());
            alertSet.setSosAlert("1");
            alertSet.setHeartAlert("1");
            alertSet.setCutAlert("1");
            //生成开关
            alertSetService.insertSelective(alertSet);
            //将开关放到缓存
            if (!SystemMap.getSosList().contains(alertSet.getUserId())){
                SystemMap.getSosList().add(alertSet.getUserId());
            }
        }
        if ("1".equals(setCut)){
            alertSet.setCutAlert(setCut);
            //更新数据库
            int update = alertSetService.updateByPrimaryKeySelective(alertSet);
            if (update>0){
                //将开关放到缓存
                if (!SystemMap.getCutList().contains(alertSet.getUserId())){
                    SystemMap.getCutList().add(alertSet.getUserId());
                }
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("操作成功");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("报警开关设置失败");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }

        }else if ("0".equals(setCut)){
            alertSet.setCutAlert(setCut);
            //更新数据库
            int update = alertSetService.updateByPrimaryKeySelective(alertSet);
            if (update>0){
                //将开关放从缓存清除
                SystemMap.getCutList().remove(new Integer(alertSet.getUserId()));

                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("操作成功");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("报警开关设置失败");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("报警开关设置失败");
            List<AlertSet> list=new ArrayList<>();
            list.add(alertSet);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
     * 设置低电量报警开关
     * */
    @RequestMapping(value = "setBattery",method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("systemSet_ele")
    public ResultBean setBattery(HttpServletRequest request,
                             @RequestParam(defaultValue = "") String setBattery) {
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
        if (setBattery==null||"".equals(setBattery)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("报警参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        AlertSet alertSet=null;
        alertSet=alertSetService.getAlertSetByUserId(user.getId());
        //若该开关不存在,生成一个开关,默认全部开启
        if (alertSet==null){
            alertSet=new AlertSet();
            alertSet.setUserId(user.getId());
            alertSet.setUpdateTime(new Date());
            alertSet.setSosAlert("1");
            alertSet.setHeartAlert("1");
            alertSet.setCutAlert("1");
            //生成开关
            alertSetService.insertSelective(alertSet);
            //将开关放到缓存
            if (!SystemMap.getBatteryList().contains(alertSet.getUserId())){
                SystemMap.getBatteryList().add(alertSet.getUserId());
            }
        }
        if ("1".equals(setBattery)){
            alertSet.setBatteryAlert(setBattery);
            //更新数据库
            int update = alertSetService.updateByPrimaryKeySelective(alertSet);
            if (update>0){
                //将开关放到缓存
                if (!SystemMap.getBatteryList().contains(alertSet.getUserId())){
                    SystemMap.getBatteryList().add(alertSet.getUserId());
                }
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("操作成功");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("报警开关设置失败");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }

        }else if ("0".equals(setBattery)){
            alertSet.setBatteryAlert(setBattery);
            //更新数据库
            int update = alertSetService.updateByPrimaryKeySelective(alertSet);
            if (update>0){
                //将开关放从缓存清除
                SystemMap.getBatteryList().remove(new Integer(alertSet.getUserId()));

                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("操作成功");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("报警开关设置失败");
                List<AlertSet> list=new ArrayList<>();
                list.add(alertSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("报警开关设置失败");
            List<AlertSet> list=new ArrayList<>();
            list.add(alertSet);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
    * 批量将未处理告警转为已处理
    * 1 已处理
    * 0 未处理
    * */
    @RequestMapping(value = "setDealBatch",method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("alert_tag_update")
    public ResultBean setDealBatch(HttpServletRequest request,
                             @RequestParam(defaultValue = "") String TagStatusIds) {
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
        if (TagStatusIds==null||"".equals(TagStatusIds)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("处理参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        String[]  ids = TagStatusIds.split(",");
        List<Integer> idsList=new ArrayList<>();
        for (String id : ids) {
            if (StringUtils.isNumeric(id)){
                idsList.add(Integer.parseInt(id));
               /* TagStatus tagStatus = tagStatusService.selectByPrimaryKey(Integer.parseInt(id));
                if (tagStatus!=null){
                    tagStatus.setIsdeal("1");
                    int i = tagStatusService.updateByPrimaryKeySelective(tagStatus);
                    if (i>0){
                        size++;
                    }
                }*/
            }
        }
        int update = tagStatusService.updateBatch(user.getId(),idsList);
        //System.out.println(update);
        if (update>0){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            resultBean.setSize(update);
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签报警处理失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }


    }
/*
* 查看所有的开关信息
* */
@RequestMapping(value = "getAllAlertSet",method = RequestMethod.GET)
@ResponseBody
@RequiresPermissions("systemSet_seeSwitch")
public ResultBean getAllAlertSet(HttpServletRequest request) {
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
    AlertSet alertSet=null;
    alertSet = alertSetService.getAlertSetByUserId(user.getId());
    if (alertSet==null){
        alertSet=new AlertSet();
        alertSet.setUserId(user.getId());
        alertSet.setUpdateTime(new Date());
        alertSet.setSosAlert("1");
        alertSet.setHeartAlert("1");
        alertSet.setCutAlert("1");
        //生成开关
        alertSetService.insertSelective(alertSet);
        //将开关放到缓存
        if (!SystemMap.getSosList().contains(user.getId())){
            SystemMap.getSosList().add(user.getId());
        }
        if (!SystemMap.getBatteryList().contains(user.getId())){
            SystemMap.getBatteryList().add(user.getId());
        }
        if (!SystemMap.getHeartList().contains(user.getId())){
            SystemMap.getHeartList().add(user.getId());
        }
        if (!SystemMap.getCutList().contains(user.getId())){
            SystemMap.getCutList().add(user.getId());
        }


    }
    HeartRateSet heartRateSet=null;
    heartRateSet = heartRateSetService.getHeartRateSet(user.getId());
    if (heartRateSet==null){
        heartRateSet =new HeartRateSet();
        heartRateSet.setMaxData(90);
        heartRateSet.setMinData(60);
        heartRateSet.setUpdateTime(new Date());
        heartRateSet.setUserId(user.getId());
        heartRateSetService.insertSelective(heartRateSet);
    }
    EleCallSet eleCallSet=null;
    eleCallSet= eleCallSetService.getEleCallSetByUserid(user.getId());
    if (eleCallSet==null){
        eleCallSet=new EleCallSet();
        eleCallSet.setTimeInterval(30);
        eleCallSet.setSetSwitch("0");
        eleCallSet.setUserId(user.getId());
        eleCallSet.setUpdateTime(new Date());
        eleCallSetService.insertSelective(eleCallSet);
    }
   AlertSetVO alertSetVO=new AlertSetVO();
    alertSetVO.setSosAlert(alertSet.getSosAlert());
    alertSetVO.setHeartAlert(alertSet.getHeartAlert());
    alertSetVO.setCutAlert(alertSet.getCutAlert());
    alertSetVO.setBatteryAlert(alertSet.getBatteryAlert());
    alertSetVO.setHeart_maxData(heartRateSet.getMaxData());
    alertSetVO.setHeart_minData(heartRateSet.getMinData());
    alertSetVO.setTimeInterval(eleCallSet.getTimeInterval());
    alertSetVO.setEleCallSwitch(eleCallSet.getSetSwitch());

    resultBean = new ResultBean();
    resultBean.setCode(1);
    List<AlertSetVO> alertSetVOList=new ArrayList<>();
    alertSetVOList.add(alertSetVO);
    resultBean.setData(alertSetVOList);
    resultBean.setMsg("操作成功");
    resultBean.setSize(alertSetVOList.size());
    return resultBean;
}

/*
* 根据人员名称,处理情况,报警类型搜索报警记录
* */
@RequestMapping(value = "getTagStatusBySomeCondition",method = RequestMethod.GET)
@ResponseBody
@RequiresPermissions("alert_tag_select")
public ResultBean getTagStatusBySomeCondition(HttpServletRequest request,
                                          QueryTagStatusVO queryTagStatusVO,
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
    if (queryTagStatusVO.getAlert_type()==null||"".equals(queryTagStatusVO.getAlert_type())){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("类型id不能为空");
        List<TagStatusVO> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    if (queryTagStatusVO.getIdCards() == null || "".equals(queryTagStatusVO.getIdCards())) {
        PageInfo<TagStatus> pageInfo=tagStatusService.getTagStatusByNoIdCards(pageIndex,pageSize,user.getId(),queryTagStatusVO.getAlert_type(),queryTagStatusVO.getIsDeal());
        List<TagStatusVO> tagStatusVOList=new ArrayList<>();
        for (TagStatus tagStatus : pageInfo.getList()) {
            TagStatusVO tagStatusVO=new TagStatusVO();
            tagStatusVO.setId(tagStatus.getId());
            tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
            tagStatusVO.setData(tagStatus.getData());
            tagStatusVO.setAlertType(tagStatus.getAlertType());
            tagStatusVO.setAddTime(tagStatus.getAddTime());
            tagStatusVO.setUserId(tagStatus.getUserId());
            tagStatusVO.setMapkey(tagStatus.getMapKey());
            tagStatusVO.setIsdeal(tagStatus.getIsdeal());
            tagStatusVO.setVedio(tagStatus.getVedio());
            if ("0".equals(tagStatus.getIsdeal())){
                tagStatusVO.setIsdeal("未处理");
            }
            if ("1".equals(tagStatus.getIsdeal())){
                tagStatusVO.setIsdeal("已处理");
            }
            //type name
            Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
            if (person!=null){
                PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
                if (personType!=null){
                    tagStatusVO.setType(personType.getTypeName());
                }
                tagStatusVO.setName(person.getPersonName());
                tagStatusVO.setImg(person.getImg());
            }else {
                Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
                if (goods!=null){
                    GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                    if (goodsType!=null){
                        tagStatusVO.setType(goodsType.getName());
                    }
                    tagStatusVO.setName(goods.getGoodsName());
                    tagStatusVO.setImg(goods.getImg());
                }
            }
            Person sqlperson = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
            Tag tag = tagMapper.getTagByOnlyAddress(sqlperson.getTagAddress());
            if (tag!=null){
                if (tag.getX()!=null){
                    tagStatusVO.setX(tag.getX());
                }
                if (tag.getY()!=null){
                    tagStatusVO.setY(tag.getY());
                }
                if (tag.getZ()!=null){
                    tagStatusVO.setZ(tag.getZ());
                }
            }
            tagStatusVOList.add(tagStatusVO);
        }
        PageInfo<TagStatusVO> page= new PageInfo<>(tagStatusVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(tagStatusVOList);
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
    String[] split = queryTagStatusVO.getIdCards().split(",");
    List<String> idCardsList=new ArrayList<>();
    for (String s : split) {
        idCardsList.add(s);
    }

    PageInfo<TagStatus> pageInfo=tagStatusService.getTagStatusBySomeCondition(pageIndex,pageSize,user.getId(),queryTagStatusVO,idCardsList);
    List<TagStatusVO> tagStatusVOList=new ArrayList<>();
    for (TagStatus tagStatus : pageInfo.getList()) {
        TagStatusVO tagStatusVO=new TagStatusVO();
        tagStatusVO.setId(tagStatus.getId());
        tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
        tagStatusVO.setData(tagStatus.getData());
        tagStatusVO.setAlertType(tagStatus.getAlertType());
        tagStatusVO.setAddTime(tagStatus.getAddTime());
        tagStatusVO.setUserId(tagStatus.getUserId());
        tagStatusVO.setMapkey(tagStatus.getMapKey());
        tagStatusVO.setIsdeal(tagStatus.getIsdeal());
        if ("0".equals(tagStatus.getIsdeal())){
            tagStatusVO.setIsdeal("未处理");
        }
        if ("1".equals(tagStatus.getIsdeal())){
            tagStatusVO.setIsdeal("已处理");
        }
        //type name
        Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
        if (person!=null){
            PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
            if (personType!=null){
                tagStatusVO.setType(personType.getTypeName());
            }
            tagStatusVO.setName(person.getPersonName());
            tagStatusVO.setImg(person.getImg());
        }else {
            Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
            if (goods!=null){
                GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                if (goodsType!=null){
                    tagStatusVO.setType(goodsType.getName());
                }
                tagStatusVO.setName(goods.getGoodsName());
                tagStatusVO.setImg(goods.getImg());
            }
        }
        Person sqlperson = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
        Tag tag = tagMapper.getTagByOnlyAddress(sqlperson.getTagAddress());
        if (tag!=null){
            if (tag.getX()!=null){
                tagStatusVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagStatusVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagStatusVO.setZ(tag.getZ());
            }
        }
        tagStatusVOList.add(tagStatusVO);
    }
    PageInfo<TagStatusVO> page= new PageInfo<>(tagStatusVOList);
    page.setPageNum(pageInfo.getPageNum());
    page.setSize(pageInfo.getSize());
    page.setSize(pageInfo.getSize());
    page.setStartRow(pageInfo.getStartRow());
    page.setEndRow(pageInfo.getEndRow());
    page.setTotal(pageInfo.getTotal());
    page.setPages(pageInfo.getPages());
    page.setList(tagStatusVOList);
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
* 查看用户所有的已处理或者未处理的报警
*
* */
@RequestMapping(value = "getAllTagStatusByIsDeal",method = RequestMethod.GET)
@ResponseBody
@RequiresPermissions("alert_tag_select")
public ResultBean getAllTagStatusByIsDeal(HttpServletRequest request,
                                        @RequestParam(defaultValue = "") String isdeal,
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
    if (isdeal == null || "".equals(isdeal)) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("处理参数不能为空");
        List<TagStatusVO> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    if (!"0".equals(isdeal) && !"1".equals(isdeal)) {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("参数有误");
        List<TagStatusVO> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    PageInfo<TagStatus> pageInfo=tagStatusService.getAllTagStatusByIsDeal(pageIndex,pageSize,user.getId(),isdeal);

    List<TagStatusVO> tagStatusVOList=new ArrayList<>();
    for (TagStatus tagStatus : pageInfo.getList()) {
        TagStatusVO tagStatusVO=new TagStatusVO();
        tagStatusVO.setId(tagStatus.getId());
        tagStatusVO.setPersonIdcard(tagStatus.getPersonIdcard());
        tagStatusVO.setData(tagStatus.getData());
        tagStatusVO.setAlertType(tagStatus.getAlertType());
        tagStatusVO.setAddTime(tagStatus.getAddTime());
        tagStatusVO.setUserId(tagStatus.getUserId());
        tagStatusVO.setMapkey(tagStatus.getMapKey());
        tagStatusVO.setIsdeal(tagStatus.getIsdeal());
        tagStatusVO.setVedio(tagStatus.getVedio());
        //type name
        Person person = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
        if (person!=null){
            PersonType personType = personTypeMapper.selectByPrimaryKey(person.getPersonTypeid());
            if (personType!=null){
                tagStatusVO.setType(personType.getTypeName());
            }
            tagStatusVO.setName(person.getPersonName());
            tagStatusVO.setImg(person.getImg());
        }else {
            Goods goods = goodsMapper.getGoodsByByIdCard(tagStatus.getPersonIdcard());
            if (goods!=null){
                GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                if (goodsType!=null){
                    tagStatusVO.setType(goodsType.getName());
                }
                tagStatusVO.setName(goods.getGoodsName());
                tagStatusVO.setImg(goods.getImg());
            }
        }
        Person sqlperson = personMapper.getPersonByIdCard(tagStatus.getPersonIdcard());
        Tag tag = tagMapper.getTagByOnlyAddress(sqlperson.getTagAddress());
        if (tag!=null){
            if (tag.getX()!=null){
                tagStatusVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagStatusVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagStatusVO.setZ(tag.getZ());
            }
        }
        tagStatusVOList.add(tagStatusVO);
    }
    PageInfo<TagStatusVO> page= new PageInfo<>(tagStatusVOList);
    page.setPageNum(pageInfo.getPageNum());
    page.setSize(pageInfo.getSize());
    page.setSize(pageInfo.getSize());
    page.setStartRow(pageInfo.getStartRow());
    page.setEndRow(pageInfo.getEndRow());
    page.setTotal(pageInfo.getTotal());
    page.setPages(pageInfo.getPages());
    page.setList(tagStatusVOList);
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
     *将所有未处理围栏告警设成已处理
     * */
    @RequestMapping(value = "setAllAlertDeal",method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("alert_tag_update")
    public ResultBean setAllAlertDeal(HttpServletRequest request) {
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

        int update= tagStatusService.setAllAlertDeal(user.getId());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(update);
        return resultBean;
    }
}
