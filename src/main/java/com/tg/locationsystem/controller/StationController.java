package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tg.locationsystem.LocationsystemApplication;
import com.tg.locationsystem.entity.Map;
import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.entity.Station;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.pojo.StationJson;
import com.tg.locationsystem.pojo.StationVO;
import com.tg.locationsystem.service.IMapService;
import com.tg.locationsystem.service.IStationService;
import com.tg.locationsystem.utils.SystemMap;
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
import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Controller
@RequestMapping("station")
public class StationController {
    @Autowired
    private IStationService stationService;
    @Autowired
    private IMapService mapService;

/*
* 添加网关设备
* */
    @RequestMapping(value = "AddStation",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddStation(@Valid Station station, BindingResult result,
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
            resultBean.setCode(2);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        //基站已经存在
        Station mystation=stationService.getStationByAddress(station.getAddr(),user.getId());
        if(mystation!=null){
            resultBean = new ResultBean();
            resultBean.setCode(6);
            resultBean.setMsg("该网关已经存在");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        station.setUserId(user.getId());
        if (station.getMasterLagDelay()==null){
            station.setMasterLagDelay("2000");
        }
        if (station.getAntDelayRx()==null){
            station.setAntDelayRx("258.114");
        }
        if (station.getAntDelayTx()==null){
            station.setAntDelayTx("258.114");
        }
        if (station.getDimension()==null){
            station.setDimension("2");
        }
        if (station.getStationStatus()==null){
            station.setStationStatus(1);
        }
        if (station.getRfdistance()==null){
            station.setRfdistance("0");
        }
        //新增基站
        int insert = stationService.insertSelective(station);
        if (insert>0){
            StationVO stationVO=new StationVO();
            stationVO.setId(station.getId());
            if (station.getSid()!=null){
                stationVO.setSid(station.getSid());
            }
            if (station.getX()!=null){
                stationVO.setX(station.getX());
            }
            if (station.getY()!=null){
                stationVO.setY(station.getY());
            }
            if (station.getZ()!=null){
                stationVO.setZ(station.getZ());
            }
            stationVO.setAddr(station.getAddr());
            stationVO.setUserId(station.getUserId());
            stationVO.setMasterLagDelay(station.getMasterLagDelay());
            stationVO.setAntDelayRx(station.getAntDelayRx());
            stationVO.setAntDelayTx(station.getAntDelayTx());
            stationVO.setIsmaster(station.getIsmaster());
            stationVO.setMasteranchorAddress(station.getMasteranchorAddress());
            stationVO.setMasteranchoraddress(station.getMasteranchoraddress());
            stationVO.setStationStatus(station.getStationStatus());
            stationVO.setDimension(station.getDimension());
            stationVO.setMapId(station.getMapId());
            stationVO.setRfdistance(station.getRfdistance());

            Map map = mapService.selectByPrimaryKey(station.getId());
            if (map!=null){
                stationVO.setMapName(map.getMapName());
            }

            resultBean = new ResultBean();
            resultBean.setCode(7);
            resultBean.setMsg("添加网关成功");
            List<StationVO> list = new ArrayList<>();
            list.add(stationVO);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(8);
            resultBean.setMsg("添加网关失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }


    }

   /*
   * 查看网关设备信息
   * */
   @RequestMapping(value = "getStations",method = RequestMethod.GET)
   @ResponseBody
   public ResultBean getStations(HttpServletRequest request,
                                 @RequestParam(defaultValue = "1") Integer pageIndex,
                                 @RequestParam(defaultValue = "1000") Integer pageSize){

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

       PageInfo<Station> pageInfo=stationService.getStationsByUserId(pageIndex,pageSize,user.getId());
       List<StationVO> stationVOList=new ArrayList<>();
       for (Station station : pageInfo.getList()) {
           StationVO stationVO=new StationVO();
           stationVO.setId(station.getId());
           if (station.getSid()!=null){
               stationVO.setSid(station.getSid());
           }
           if (station.getX()!=null){
               stationVO.setX(station.getX());
           }
           if (station.getY()!=null){
               stationVO.setY(station.getY());
           }
           if (station.getZ()!=null){
               stationVO.setZ(station.getZ());
           }
           stationVO.setAddr(station.getAddr());
           stationVO.setUserId(station.getUserId());
           stationVO.setMasterLagDelay(station.getMasterLagDelay());
           stationVO.setAntDelayRx(station.getAntDelayRx());
           stationVO.setAntDelayTx(station.getAntDelayTx());
           stationVO.setIsmaster(station.getIsmaster());
           stationVO.setMasteranchorAddress(station.getMasteranchorAddress());
           stationVO.setMasteranchoraddress(station.getMasteranchoraddress());
           stationVO.setStationStatus(station.getStationStatus());
           stationVO.setDimension(station.getDimension());
           stationVO.setMapId(station.getMapId());
           stationVO.setRfdistance(station.getRfdistance());

           Map map = mapService.selectByPrimaryKey(station.getMapId());
           if (map!=null){
               stationVO.setMapName(map.getMapName());
           }
           stationVOList.add(stationVO);
       }
       PageInfo<StationVO> page= new PageInfo<>(stationVOList);
       page.setPageNum(pageInfo.getPageNum());
       page.setSize(pageInfo.getSize());
       page.setSize(pageInfo.getSize());
       page.setStartRow(pageInfo.getStartRow());
       page.setEndRow(pageInfo.getEndRow());
       page.setTotal(pageInfo.getTotal());
       page.setPages(pageInfo.getPages());
       page.setList(stationVOList);
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
   * 配置基站
   * */
   @RequestMapping(value = "SetStations",method = RequestMethod.GET)
   @ResponseBody
   public ResultBean SetStations(HttpServletRequest request,
                                 @RequestParam("") String StationIds){
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
       if (StationIds==null||"".equals(StationIds)){
           resultBean = new ResultBean();
           resultBean.setCode(107);
           resultBean.setMsg("基站不能为空");
           List<Myuser> list = new ArrayList<>();
           resultBean.setData(list);
           resultBean.setSize(list.size());
           return resultBean;
       }
       String[] split = StationIds.split(",");
       List<Station> stationList=new ArrayList<>();
       Map map=null;
       for (String s : split) {
           Station station = stationService.selectByPrimaryKey(Integer.parseInt(s));
           if (station!=null){
               stationList.add(station);
               map = mapService.selectByPrimaryKey(station.getMapId());
           }
       }
       if (map!=null){
           String key = SystemMap.getCleAndKeyMap().get(map.getMapKey());
           //todo
        //配置发送给cle sdk.configstation
       /*Gson gson=new Gson();
       String string=gson.toJson(stationList);
       //System.out.println("数据库数据:"+string);
       //发送
       skylabSDK.configStation(key,string);*/
       }




       resultBean = new ResultBean();
       resultBean.setCode(1);
       resultBean.setMsg("操作成功");
       resultBean.setData(stationList);
       resultBean.setSize(stationList.size());
       return resultBean;
   }

   /*
   * 根据地图的key查找基站
   * */
   @RequestMapping(value = "getStationsByMapKey",method = RequestMethod.GET)
   @ResponseBody
   public ResultBean getStationsByMapKey(HttpServletRequest request,
                                 @RequestParam("") String mapkey,
                                         @RequestParam(defaultValue = "1") Integer pageIndex,
                                         @RequestParam(defaultValue = "1000") Integer pageSize){
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
       if (mapkey==null||"".equals(mapkey)){
           resultBean = new ResultBean();
           resultBean.setCode(108);
           resultBean.setMsg("地图key不能为空");
           List<Station> list = new ArrayList<>();
           resultBean.setData(list);
           resultBean.setSize(list.size());
           return resultBean;
       }
       Map map = mapService.getMapByUuid(mapkey);
       if (map==null){
           resultBean = new ResultBean();
           resultBean.setCode(109);
           resultBean.setMsg("该地图不存在");
           List<Station> list = new ArrayList<>();
           resultBean.setData(list);
           resultBean.setSize(list.size());
           return resultBean;
       }
       PageInfo<Station> pageInfo=stationService.getOnlineStationsByMapKey(pageIndex,pageSize,map.getId());
       List<StationVO> stationVOList=new ArrayList<>();
       for (Station station : pageInfo.getList()) {
           StationVO stationVO=new StationVO();
           stationVO.setId(station.getId());
           if (station.getSid()!=null){
               stationVO.setSid(station.getSid());
           }
           if (station.getX()!=null){
               stationVO.setX(station.getX());
           }
           if (station.getY()!=null){
               stationVO.setY(station.getY());
           }
           if (station.getZ()!=null){
               stationVO.setZ(station.getZ());
           }
           stationVO.setAddr(station.getAddr());
           stationVO.setUserId(station.getUserId());
           stationVO.setMasterLagDelay(station.getMasterLagDelay());
           stationVO.setAntDelayRx(station.getAntDelayRx());
           stationVO.setAntDelayTx(station.getAntDelayTx());
           stationVO.setIsmaster(station.getIsmaster());
           stationVO.setMasterAddr(station.getMasterAddr());
           stationVO.setMasteranchorAddress(station.getMasteranchorAddress());
           stationVO.setMasteranchoraddress(station.getMasteranchoraddress());
           stationVO.setStationStatus(station.getStationStatus());
           stationVO.setDimension(station.getDimension());
           stationVO.setMapId(station.getMapId());
           stationVO.setRfdistance(station.getRfdistance());

           stationVO.setMapName(map.getMapName());

           stationVOList.add(stationVO);
       }
       PageInfo<StationVO> page= new PageInfo<>(stationVOList);
       page.setPageNum(pageInfo.getPageNum());
       page.setSize(pageInfo.getSize());
       page.setSize(pageInfo.getSize());
       page.setStartRow(pageInfo.getStartRow());
       page.setEndRow(pageInfo.getEndRow());
       page.setTotal(pageInfo.getTotal());
       page.setPages(pageInfo.getPages());
       page.setList(stationVOList);
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
     * 配置基站
     * */
    @RequestMapping(value = "SetStationsByPOST",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean SetStationsByPOST(HttpServletRequest request,
                                  @RequestParam("") String stationJson, @RequestParam("") String MapUUID){
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
        if (MapUUID==null||"".equals(MapUUID)){
            resultBean = new ResultBean();
            resultBean.setCode(108);
            resultBean.setMsg("地图key不能为空");
            List<Station> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (stationJson==null||"".equals(stationJson)){
            resultBean = new ResultBean();
            resultBean.setCode(109);
            resultBean.setMsg("基站不能为空");
            List<Station> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        System.out.println(stationJson);
        Gson gson = new Gson();//创建Gson对象
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(stationJson).getAsJsonArray();//获取JsonArray对象
        ArrayList<StationJson> beans = new ArrayList<>();
        for (JsonElement bean : jsonElements) {
            StationJson bean1 = gson.fromJson(bean, StationJson.class);//解析
            beans.add(bean1);
        }

        for (StationJson bean : beans) {
            Station station=new Station();
            station.setId(bean.getId());
            station.setSid(bean.getSid());
            station.setX(bean.getX());
            station.setY(bean.getY());
            station.setZ(bean.getZ());
            station.setAddr(bean.getAddr());
            station.setUserId(bean.getUserId());
            station.setMasterLagDelay(bean.getMasterLagDelay());
            station.setAntDelayTx(bean.getAntDelayTx());
            station.setAntDelayRx(bean.getAntDelayRx());
            station.setIsmaster(bean.getIsmaster());
            station.setMasterAddr(bean.getMasterAddr());
            station.setMasteranchoraddress(bean.getMasteranchoraddress());
            station.setMasteranchorAddress(bean.getMasteranchorAddress());
            station.setStationStatus(bean.getStationStatus());
            station.setDimension(bean.getDimension());
            station.setMapId(bean.getMapId());
            station.setRfdistance(bean.getRfdistance());
            //更新数据库
            stationService.updateByPrimaryKeySelective(station);
        }
        //将该json发送给cle
        String key = SystemMap.getCleAndKeyMap().get(MapUUID);
        if (key==null||"".equals(key)){
            resultBean = new ResultBean();
            resultBean.setCode(109);
            resultBean.setMsg("定位引擎未连接");
            List<Station> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        List<bean.Station> sdkList=new ArrayList(beans.size());
        for (StationJson stationJson1 : beans) {
            bean.Station station1=new bean.Station();
            station1.setId(stationJson1.getId().toString());
            station1.setX(stationJson1.getX().toString());
            station1.setY(stationJson1.getY().toString());
            station1.setZ(stationJson1.getZ().toString());
            station1.setMaster_lag_delay(stationJson1.getMasterLagDelay());
            station1.setAddr(stationJson1.getAddr());
            station1.setAnt_delay_rx(stationJson1.getAntDelayRx());
            station1.setAnt_delay_tx(stationJson1.getAntDelayTx());
            station1.setIsmaster(stationJson1.getIsmaster());
            station1.setMaster_addr(stationJson1.getMasterAddr());
            System.out.println("主基站地址:"+station1.getMaster_addr());
            station1.setRfdistance(stationJson1.getRfdistance());
            station1.setDimension(Integer.parseInt(stationJson1.getDimension()));
            String masteranchoraddress = stationJson1.getMasteranchorAddress();
            if (masteranchoraddress!=null&&!"".equals(masteranchoraddress)){
                String[] split = masteranchoraddress.split(",");
                station1.setMasteranchorAddress(split);
            }

            sdkList.add(station1);
        }
        List<bean.Station> SendsdkList=new ArrayList(beans.size());
        if (sdkList.size()!=0){
            for (bean.Station station : sdkList) {
                if(station.getIsmaster()==null||"".equals(station.getIsmaster())){
                    continue;
                }
                SendsdkList.add(station);
            }
            if (sdkList.size()>0){
                String string=gson.toJson(SendsdkList);
                System.out.println("发送cle数据:"+string);
                //发送
                LocationsystemApplication.skylabSDK.configStation(key,string);
                //System.out.println("beans:"+beans.size());
            }

        }


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list=new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

    /*
    * 开始/停止定位
    *
    * */
    @RequestMapping(value = "locationOrder",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean locationOrder(HttpServletRequest request,
                                        @RequestParam("") String locationOrder,@RequestParam("") String MapUUID){

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
        if (locationOrder==null||"".equals(locationOrder)){
            resultBean = new ResultBean();
            resultBean.setCode(110);
            resultBean.setMsg("命令不能为空");
            List<Station> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (MapUUID==null||"".equals(MapUUID)){
            resultBean = new ResultBean();
            resultBean.setCode(108);
            resultBean.setMsg("地图key不能为空");
            List<Station> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        String key = SystemMap.getCleAndKeyMap().get(MapUUID);
        if (key==null||"".equals(key)){
            resultBean = new ResultBean();
            resultBean.setCode(109);
            resultBean.setMsg("定位引擎未连接");
            List<Station> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            if ("1".equals(locationOrder)){
                //开始定位
                LocationsystemApplication.skylabSDK.startLocation(key);

                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("开始定位操作成功");
                List list=new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }else {
                //停止定位

                LocationsystemApplication.skylabSDK.stopLocation(key);

                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("停止定位操作成功");
                List list=new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }


    }

    /*
     * 删除基站
     * */
    @RequestMapping(value = "delStation",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean delStation(HttpServletRequest request,
                                 @RequestParam("") Integer stationId){
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
        if (stationId==null||"".equals(stationId)){
            resultBean = new ResultBean();
            resultBean.setCode(107);
            resultBean.setMsg("基站id不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Station station = stationService.selectByPrimaryKey(stationId);
        if (station==null){
            resultBean = new ResultBean();
            resultBean.setCode(107);
            resultBean.setMsg("该基站不存在");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        int del = stationService.deleteByPrimaryKey(stationId);
        if (del>0){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List list=new ArrayList<>();
            list.add(station);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(120);
            resultBean.setMsg("操作失败");
            List list=new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }
}
