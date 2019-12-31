package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.pojo.HeartRateHistoryCondition;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.SystemMap;
import org.apache.ibatis.annotations.Param;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hyy
 * @ Date2019/8/12
 */
@Controller
@RequestMapping("call")
public class CallController {
    @Autowired
    private IEleCallSetService eleCallSetService;
    @Autowired
    private IStatisticsCallService statisticsCallService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IEleCallService eleCallService;
    @Autowired
    private IPersonService personService;
    DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    /*
    * 设置定时点名
    * */
    @RequestMapping(value = "setTime",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean setTime(@Valid EleCallSet eleCallSet, BindingResult result,
                              HttpServletRequest request) {
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
        if (eleCallSet.getTimeInterval()>60||eleCallSet.getTimeInterval()<1){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("定时时间参数不正确,1-60");
            List<EleCallSet> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        EleCallSet eleCallSet1=null;
        eleCallSet1=eleCallSetService.getEleCallSetByUserid(user.getId());
        if (eleCallSet1==null){
            eleCallSet1=new EleCallSet();
            eleCallSet1.setUserId(user.getId());
            eleCallSet1.setUpdateTime(new Date());
            eleCallSetService.insertSelective(eleCallSet1);
        }

        eleCallSet1.setUserId(user.getId());
        eleCallSet1.setUpdateTime(new Date());
        eleCallSet1.setId(eleCallSet1.getId());
        eleCallSet1.setSetSwitch(eleCallSet.getSetSwitch());
        eleCallSet1.setTimeInterval(eleCallSet.getTimeInterval());
            int update = eleCallSetService.updateByPrimaryKeySelective(eleCallSet1);
            if (update>0){
                if ("1".equals(eleCallSet.getSetSwitch())){
                    Timer timer1 = SystemMap.getTimermap().get(eleCallSet.getUserId());
                    if (timer1!=null){
                        timer1.cancel();
                        // 创建定时器
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            // 在run方法中的语句就是定时任务执行时运行的语句。
                            public void run() {
                                //统计
                                if (eleCallSet.getTimeInterval()==null){
                                    eleCallSet.setTimeInterval(15);
                                }
                                String format = simpleDateFormat.format(new Date());
                                StringBuffer sb=new StringBuffer(format);
                                sb.append("_");
                                sb.append(eleCallSet.getUserId());
                                //在线集合
                                List<Tag> onlineList=new ArrayList<>();
                                //离线集合
                                List<Tag> notOnlineList=new ArrayList<>();
                                List<Tag>tagList1=tagService.getTagsByUsed(eleCallSet.getUserId());
                                for (Tag tag : tagList1) {
                                    EleCall eleCall=new EleCall();
                                    eleCall.setUserId(tag.getUserId());
                                    eleCall.setAddress(tag.getAddress());
                                    if (tag.getX()!=null){
                                        eleCall.setX(tag.getX());
                                    }
                                    if (tag.getY()!=null){
                                        eleCall.setY(tag.getY());
                                    }
                                    if (tag.getZ()!=null){
                                        eleCall.setZ(tag.getZ());
                                    }
                                    //电子点名设置人员名字,地图key,电话,logo,idcard
                                    Person person = personService.getPersonByAddress(tag.getUserId(), tag.getAddress());
                                    if (person!=null){
                                        eleCall.setPersonName(person.getPersonName());
                                        eleCall.setPersonPhone(person.getPersonPhone());
                                        eleCall.setPersonLog(person.getImg());
                                        eleCall.setPersonIdcard(person.getIdCard());
                                    }
                                    eleCall.setMapKey(tag.getMapKey());
                                    eleCall.setIsonline(tag.getIsonline());
                                    eleCall.setLastTime(tag.getLastonline());
                                    eleCall.setTimeUserid(sb.toString());
                                    int i = eleCallService.insertSelective(eleCall);
                                    if (i>0){
                                        if ("1".equals(tag.getIsonline())){
                                            onlineList.add(tag);
                                        }
                                        if ("0".equals(tag.getIsonline())){
                                            notOnlineList.add(tag);
                                        }
                                    }
                                }
                                StatisticsCall statisticsCall=new StatisticsCall();
                                statisticsCall.setTotal(onlineList.size()+notOnlineList.size());
                                statisticsCall.setOnline(onlineList.size());
                                statisticsCall.setNotOnline(notOnlineList.size());
                                statisticsCall.setTimeUser(sb.toString());
                                statisticsCall.setUserId(eleCallSet.getUserId());
                                statisticsCall.setRecordTime(new Date());
                                statisticsCall.setTimeInterval(eleCallSet.getTimeInterval());
                                statisticsCallService.insertSelective(statisticsCall);
                            }
                            // 表示在3秒之后开始执行，并且每2秒执行一次
                        }, 3000, eleCallSet.getTimeInterval()*1000);
                        SystemMap.getTimermap().put(user.getId(),timer);
                    }else {
                        // 创建定时器
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            // 在run方法中的语句就是定时任务执行时运行的语句。
                            public void run() {
                                //统计
                                if (eleCallSet.getTimeInterval()==null){
                                    eleCallSet.setTimeInterval(15);
                                }
                                String format = simpleDateFormat.format(new Date());
                                StringBuffer sb=new StringBuffer(format);
                                sb.append("_");
                                sb.append(eleCallSet.getUserId());
                                //在线集合
                                List<Tag> onlineList=new ArrayList<>();
                                //离线集合
                                List<Tag> notOnlineList=new ArrayList<>();
                                List<Tag>tagList1=tagService.getTagsByUsed(eleCallSet.getUserId());
                                for (Tag tag : tagList1) {
                                    EleCall eleCall=new EleCall();
                                    eleCall.setUserId(tag.getUserId());
                                    eleCall.setAddress(tag.getAddress());
                                    if (tag.getX()!=null){
                                        eleCall.setX(tag.getX());
                                    }
                                    if (tag.getY()!=null){
                                        eleCall.setY(tag.getY());
                                    }
                                    if (tag.getZ()!=null){
                                        eleCall.setZ(tag.getZ());
                                    }
                                    eleCall.setIsonline(tag.getIsonline());
                                    eleCall.setLastTime(tag.getLastonline());
                                    eleCall.setTimeUserid(sb.toString());
                                    int i = eleCallService.insertSelective(eleCall);
                                    if (i>0){
                                        if ("1".equals(tag.getIsonline())){
                                            onlineList.add(tag);
                                        }
                                        if ("0".equals(tag.getIsonline())){
                                            notOnlineList.add(tag);
                                        }
                                    }
                                }
                                StatisticsCall statisticsCall=new StatisticsCall();
                                statisticsCall.setTotal(onlineList.size()+notOnlineList.size());
                                statisticsCall.setOnline(onlineList.size());
                                statisticsCall.setNotOnline(notOnlineList.size());
                                statisticsCall.setTimeUser(sb.toString());
                                statisticsCall.setUserId(eleCallSet.getUserId());
                                statisticsCall.setRecordTime(new Date());
                                statisticsCall.setTimeInterval(eleCallSet.getTimeInterval());
                                statisticsCallService.insertSelective(statisticsCall);
                            }
                            // 表示在3秒之后开始执行，并且每2秒执行一次
                        }, 3000, eleCallSet.getTimeInterval()*1000);
                        SystemMap.getTimermap().put(user.getId(),timer);
                    }

                }
                if ("0".equals(eleCallSet.getSetSwitch())){
                    Timer timer1 = SystemMap.getTimermap().get(eleCallSet.getUserId());
                    if (timer1!=null){
                        timer1.cancel();
                    }
                    SystemMap.getTimermap().remove(user.getId());
                }
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("时间设置成功");
                List<EleCallSet> list = new ArrayList<>();
                list.add(eleCallSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("设置定时时间失败");
                List<EleCallSet> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
    }

    /*
    * 设置开关,打开关闭
    * 1 打开
    * 0 关闭
    * */
    @RequestMapping(value = "setSwitch",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean setSwitch(@RequestParam(defaultValue = "")String setSwitch,
                              HttpServletRequest request) {
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
        if (setSwitch==null||"".equals(setSwitch)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("开关不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (!"0".equals(setSwitch)&&!"1".equals(setSwitch)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("setSwitch参数有误");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        EleCallSet eleCallSet=eleCallSetService.getEleCallSetByUserid(user.getId());
        if (eleCallSet!=null){
            eleCallSet.setUpdateTime(new Date());
            eleCallSet.setSetSwitch(setSwitch);
            int update = eleCallSetService.updateByPrimaryKeySelective(eleCallSet);
            if (update>0){
                if ("1".equals(setSwitch)){
                    Timer timer1 = SystemMap.getTimermap().get(eleCallSet.getUserId());
                    if (timer1!=null){
                        timer1.cancel();
                        // 创建定时器
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            // 在run方法中的语句就是定时任务执行时运行的语句。
                            public void run() {
                                //统计
                                if (eleCallSet.getTimeInterval()==null){
                                    eleCallSet.setTimeInterval(15);
                                }
                                String format = simpleDateFormat.format(new Date());
                                StringBuffer sb=new StringBuffer(format);
                                sb.append("_");
                                sb.append(eleCallSet.getUserId());
                                //在线集合
                                List<Tag> onlineList=new ArrayList<>();
                                //离线集合
                                List<Tag> notOnlineList=new ArrayList<>();
                                List<Tag>tagList1=tagService.getTagsByUsed(eleCallSet.getUserId());
                                for (Tag tag : tagList1) {
                                    EleCall eleCall=new EleCall();
                                    eleCall.setUserId(tag.getUserId());
                                    eleCall.setAddress(tag.getAddress());
                                    if (tag.getX()!=null){
                                        eleCall.setX(tag.getX());
                                    }
                                    if (tag.getY()!=null){
                                        eleCall.setY(tag.getY());
                                    }
                                    if (tag.getZ()!=null){
                                        eleCall.setZ(tag.getZ());
                                    }
                                    //电子点名设置人员名字,地图key,电话,logo,idcard
                                    Person person = personService.getPersonByAddress(tag.getUserId(), tag.getAddress());
                                    if (person!=null){
                                        eleCall.setPersonName(person.getPersonName());
                                        eleCall.setPersonPhone(person.getPersonPhone());
                                        eleCall.setPersonLog(person.getImg());
                                        eleCall.setPersonIdcard(person.getIdCard());
                                    }
                                    eleCall.setMapKey(tag.getMapKey());
                                    eleCall.setIsonline(tag.getIsonline());
                                    eleCall.setLastTime(tag.getLastonline());
                                    eleCall.setTimeUserid(sb.toString());
                                    int i = eleCallService.insertSelective(eleCall);
                                    if (i>0){
                                        if ("1".equals(tag.getIsonline())){
                                            onlineList.add(tag);
                                        }
                                        if ("0".equals(tag.getIsonline())){
                                            notOnlineList.add(tag);
                                        }
                                    }
                                }
                                StatisticsCall statisticsCall=new StatisticsCall();
                                statisticsCall.setTotal(onlineList.size()+notOnlineList.size());
                                statisticsCall.setOnline(onlineList.size());
                                statisticsCall.setNotOnline(notOnlineList.size());
                                statisticsCall.setTimeUser(sb.toString());
                                statisticsCall.setUserId(eleCallSet.getUserId());
                                statisticsCall.setRecordTime(new Date());
                                statisticsCall.setTimeInterval(eleCallSet.getTimeInterval());
                                statisticsCallService.insertSelective(statisticsCall);
                            }
                            // 表示在3秒之后开始执行，并且每2秒执行一次
                        }, 3000, eleCallSet.getTimeInterval()*1000*60);
                        SystemMap.getTimermap().put(eleCallSet.getUserId(),timer);
                    }else {
                        // 创建定时器
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            // 在run方法中的语句就是定时任务执行时运行的语句。
                            public void run() {
                                //统计
                                if (eleCallSet.getTimeInterval()==null){
                                    eleCallSet.setTimeInterval(15);
                                }
                                String format = simpleDateFormat.format(new Date());
                                StringBuffer sb=new StringBuffer(format);
                                sb.append("_");
                                sb.append(eleCallSet.getUserId());
                                //在线集合
                                List<Tag> onlineList=new ArrayList<>();
                                //离线集合
                                List<Tag> notOnlineList=new ArrayList<>();
                                List<Tag>tagList1=tagService.getTagsByUsed(eleCallSet.getUserId());
                                for (Tag tag : tagList1) {
                                    EleCall eleCall=new EleCall();
                                    eleCall.setUserId(tag.getUserId());
                                    eleCall.setAddress(tag.getAddress());
                                    if (tag.getX()!=null){
                                        eleCall.setX(tag.getX());
                                    }
                                    if (tag.getY()!=null){
                                        eleCall.setY(tag.getY());
                                    }
                                    if (tag.getZ()!=null){
                                        eleCall.setZ(tag.getZ());
                                    }
                                    eleCall.setIsonline(tag.getIsonline());
                                    eleCall.setLastTime(tag.getLastonline());
                                    eleCall.setTimeUserid(sb.toString());
                                    int i = eleCallService.insertSelective(eleCall);
                                    if (i>0){
                                        if ("1".equals(tag.getIsonline())){
                                            onlineList.add(tag);
                                        }
                                        if ("0".equals(tag.getIsonline())){
                                            notOnlineList.add(tag);
                                        }
                                    }
                                }
                                StatisticsCall statisticsCall=new StatisticsCall();
                                statisticsCall.setTotal(onlineList.size()+notOnlineList.size());
                                statisticsCall.setOnline(onlineList.size());
                                statisticsCall.setNotOnline(notOnlineList.size());
                                statisticsCall.setTimeUser(sb.toString());
                                statisticsCall.setUserId(eleCallSet.getUserId());
                                statisticsCall.setRecordTime(new Date());
                                statisticsCall.setTimeInterval(eleCallSet.getTimeInterval());
                                statisticsCallService.insertSelective(statisticsCall);
                            }
                            // 表示在3秒之后开始执行，并且每2秒执行一次
                        }, 3000, eleCallSet.getTimeInterval()*1000*60);
                        SystemMap.getTimermap().put(eleCallSet.getUserId(),timer);
                    }

                }
                if ("0".equals(setSwitch)){
                    Timer timer1 = SystemMap.getTimermap().get(eleCallSet.getUserId());
                    if (timer1!=null){
                        timer1.cancel();
                    }
                    SystemMap.getTimermap().remove(eleCallSet.getUserId());
                }
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("设置定时开关成功");
                List<EleCallSet> list = new ArrayList<>();
                list.add(eleCallSet);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("设置定时开关失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("设置定时开关失败");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    /*
    * 查看所有统计的数据
    * 分页
    * */
    @RequestMapping(value = "getStatisticsPage",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getStatisticsPage(HttpServletRequest request,
                                        @RequestParam(defaultValue = "1") Integer pageIndex,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
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
        PageInfo<StatisticsCall> pageInfo=statisticsCallService.getStatisticsCallByUserIdPage(pageIndex,pageSize,user.getId());

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
     * 查看所有统计的数据
     * 不分页
     * */
    @RequestMapping(value = "getStatistics",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getStatistics(HttpServletRequest request) {
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
        List<StatisticsCall> statisticsCallList=statisticsCallService.getStatisticsCallByUserId(user.getId());
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(statisticsCallList);
        resultBean.setSize(statisticsCallList.size());
        return resultBean;
    }
    /*
     * 查看某次统计的具体数据
     * 分页
     * */
    @RequestMapping(value = "getEleCallByKeyPage",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getEleCallByKeyPage(HttpServletRequest request,
                                      @RequestParam(defaultValue = "")String timeuser,
                                        @RequestParam(defaultValue = "1") Integer pageIndex,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
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
        if ("".equals(timeuser)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("参数错误");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        PageInfo<EleCall> pageInfo=eleCallService.geteleCallByKeyPage(pageIndex,pageSize,timeuser);

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
     * 查看某次统计的具体数据
     * 不分页
     * */
    @RequestMapping(value = "getEleCallByKey",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getEleCallByKey(HttpServletRequest request,
                                      @RequestParam(defaultValue = "")String timeuser) {
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
        if ("".equals(timeuser)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("参数错误");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        List<EleCall> eleCallList=eleCallService.geteleCallByKey(timeuser);
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(eleCallList);
        resultBean.setSize(eleCallList.size());
        return resultBean;
    }
}
