package com.tg.locationsystem.controller;

import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.pojo.Path;
import com.tg.locationsystem.pojo.PathMap;
import com.tg.locationsystem.pojo.PathVO;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/3
 */
@Controller
@RequestMapping("path")
public class PathController {
    @Autowired
    private ITagHistoryService tagHistoryService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IMapService mapService;
    @Autowired
    private ITagService tagService;
    private  SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /*
    * 查看人员或物品轨迹
    * */
    @RequestMapping(value = "queryPath",method = RequestMethod.GET)
    @ResponseBody
    public Object queryPath(@Valid Path path, BindingResult result,
                                      HttpServletRequest request){

        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
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
        String[] split = path.getStartTime().split(" ");
        String[] split3 = split[0].split("-");
        StringBuffer sb2=new StringBuffer();
        //2019-07-23 12:00:00 得到20190723
        //后续:按月....split3.length-1
        for (int i = 0; i < split3.length; i++) {
            sb2.append(split3[i]);
        }
        String[] split1 = path.getEndTime().split(" ");
        String[] split4 = split1[0].split("-");
        StringBuffer sb4=new StringBuffer();
        //2019-07-24 23:50:00 得到20190724
        //后续,按月....split4.length-1
        for (int i = 0; i < split4.length; i++) {
            sb4.append(split4[i]);
        }
        int s = Integer.parseInt(sb2.toString());
        int en = Integer.parseInt(sb4.toString());
        //System.out.println("结束时间-开始时间:"+(en-s));
        if (en-s>0){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("当前不支持跨天查询");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }


        //得到开始和结束时间
        String start = path.getStartTime();
        String end = path.getEndTime();
        try {
            Date startTime = sdf.parse(start);
            long starttime = startTime.getTime()/1000;
            Date endTime = sdf.parse(end);
            long endtime = endTime.getTime()/1000;
           // System.out.println("起止时间:"+startTime+":"+endTime);
            //开始时间大于结束时间,时间输入有误
            if (starttime>endtime){
                System.out.println("开始时间-结束时间:"+(starttime>endtime));
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("时间参数有误");
                List<Path> list = new ArrayList<>();
                list.add(path);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            String date=split[0];
            String[] split2 = date.split("-");
            StringBuffer sb=new StringBuffer("tag_history_");
            for (int i = 0; i < split2.length; i++) {
                sb.append(split2[i]);
            }
            //得到表名
            String format=sb.toString();
            int i = tagHistoryService.existTable(format);
            if (i==0){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("当天没有轨迹数据");
                List<Path> list = new ArrayList<>();
                list.add(path);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            //System.out.println("表名:"+format);
            //根据标签address得到历史轨迹集合
            List<TagHistory> tagHistories=tagHistoryService.getTagHistoryByAdd(format,path.getPersonidcard());
           // System.out.println(path.getTagAddress()+":"+tagHistories.size());
            if (tagHistories.size()==0) {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("该标签不存在或无轨迹记录");
                List<Path> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            //根据传入的时间参数查询相关表,标签address查询历史记录
            List<TagHistory> tagHistoryList=tagHistoryService.getHistoryByAddAndTime(format,path.getPersonidcard(),path.getStartTime(),path.getEndTime());
           // System.out.println(format+":"+tagHistoryList.size());
            if (tagHistoryList.size()==0){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("该标签不存在或无轨迹记录");
                List<Path> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
          /*  for (int i = 0; i < tagHistoryList.size(); i++) {
                if (i==0||i==(tagHistoryList.size()-1)){
                    System.out.println(tagHistoryList.get(i).toString());
                }
            }*/
            String tag_history = StringUtils.getTag_history(tagHistoryList);
            //System.out.println("轨迹长度:"+tagHistoryList.size());
            String stsrt=sdf.format(tagHistoryList.get(0).getTime());
            String End=sdf.format(tagHistoryList.get(tagHistoryList.size()-1).getTime());
           /* System.out.println("开始时间:"+stsrt);
            System.out.println("结束时间:"+End);*/

            PathVO pathVO=new PathVO();
            pathVO.setPathCode(1);
            pathVO.setMsg("轨迹查询成功");
            pathVO.setPath(tag_history);
            pathVO.setStartTime(stsrt);
            pathVO.setEndTime(End);
            Person person = personService.getPersonByAddress(user.getId(), path.getPersonidcard());
            if (person!=null){
                pathVO.setImg(person.getImg());
            }
            Goods goods = goodsService.getGoodsByAddress(user.getId(), path.getPersonidcard());
            if (goods!=null){
                pathVO.setImg(goods.getImg());
            }
            pathVO.setTagHistoryList(tagHistoryList);

            return pathVO;

        } catch (ParseException e) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("时间数据处理失败");
            List<Path> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /* 
     * 查看人员或物品在某个地图的轨迹
     * */
    @RequestMapping(value = "queryPathByMap",method = RequestMethod.GET)
    @ResponseBody
    public Object queryPathByMap(@Valid PathMap pathMap, BindingResult result,
                                 HttpServletRequest request) {
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
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
        Map map = mapService.getMapByUuid(pathMap.getMapkey());
        if (map == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该地图不存在");
            List<Map> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        String[] split = pathMap.getStartTime().split(" ");
        String[] split3 = split[0].split("-");
        StringBuffer sb2 = new StringBuffer();
        //2019-07-23 12:00:00 得到20190723
        //后续:按月....split3.length-1
        for (int i = 0; i < split3.length; i++) {
            sb2.append(split3[i]);
        }
        String[] split1 = pathMap.getEndTime().split(" ");
        String[] split4 = split1[0].split("-");
        StringBuffer sb4 = new StringBuffer();
        //2019-07-24 23:50:00 得到20190724
        //后续,按月....split4.length-1
        for (int i = 0; i < split4.length; i++) {
            sb4.append(split4[i]);
        }
        int s = Integer.parseInt(sb2.toString());
        int en = Integer.parseInt(sb4.toString());
        //System.out.println("结束时间-开始时间:"+(en-s));
        if (en - s > 0) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("当前不支持跨天查询");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //得到开始和结束时间
        String start = pathMap.getStartTime();
        String end = pathMap.getEndTime();
        try {
            Date startTime = sdf.parse(start);
            long starttime = startTime.getTime() / 1000;
            Date endTime = sdf.parse(end);
            long endtime = endTime.getTime() / 1000;
            // System.out.println("起止时间:"+startTime+":"+endTime);
            //开始时间大于结束时间,时间输入有误
            if (starttime > endtime) {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("时间参数有误");
                List<PathMap> list = new ArrayList<>();
                list.add(pathMap);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            String date = split[0];
            String[] split2 = date.split("-");
            StringBuffer sb = new StringBuffer("tag_history_");
            for (int i = 0; i < split2.length; i++) {
                sb.append(split2[i]);
            }
            //得到表名
            String format=sb.toString();
            int i = tagHistoryService.existTable(format);
            if (i==0){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("没有当天的数据");
                List<PathMap> list = new ArrayList<>();
                list.add(pathMap);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            //根据标签address得到历史轨迹集合
            List<TagHistory> tagHistories=tagHistoryService.getTagHistoryByAddAndMap(format,pathMap.getPersonidcard(),pathMap.getMapkey());
            // System.out.println(path.getTagAddress()+":"+tagHistories.size());
            if (tagHistories.size()==0) {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("该标签不存在或无轨迹记录");
                List<Path> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            //根据传入的时间参数查询相关表,标签address查询历史记录
            List<TagHistory> tagHistoryList=tagHistoryService.getHistoryByAddAndTimeAndMap(format,pathMap.getPersonidcard(),pathMap.getStartTime(),pathMap.getEndTime(),pathMap.getMapkey());
            // System.out.println(format+":"+tagHistoryList.size());
            if (tagHistoryList.size()==0){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("该标签不存在或无轨迹记录");
                List<Path> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            String tag_history = StringUtils.getTag_history(tagHistoryList);
            //System.out.println("轨迹长度:"+tagHistoryList.size());
            String stsrt=sdf.format(tagHistoryList.get(0).getTime());
            String End=sdf.format(tagHistoryList.get(tagHistoryList.size()-1).getTime());
           /* System.out.println("开始时间:"+stsrt);
            System.out.println("结束时间:"+End);*/

            PathVO pathVO=new PathVO();
            pathVO.setPathCode(1);
            pathVO.setMsg("轨迹查询成功");
            pathVO.setPath(tag_history);
            pathVO.setStartTime(stsrt);
            pathVO.setEndTime(End);
            Person person = personService.getPersonByAddress(user.getId(), pathMap.getPersonidcard());
            if (person!=null){
                pathVO.setImg(person.getImg());
            }
            Goods goods = goodsService.getGoodsByAddress(user.getId(), pathMap.getPersonidcard());
            if (goods!=null){
                pathVO.setImg(goods.getImg());
            }
            pathVO.setTagHistoryList(tagHistoryList);

            return pathVO;
        } catch (ParseException e) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("时间数据处理失败");
            List<Path> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }
    }
