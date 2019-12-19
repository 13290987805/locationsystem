package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.LocationsystemApplication;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.maprule.SVGUtil;
import com.tg.locationsystem.pojo.AddMapVO;
import com.tg.locationsystem.pojo.CleconfigVO;
import com.tg.locationsystem.pojo.MapVO;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.service.ICleConfigService;
import com.tg.locationsystem.service.IMapRuleService;
import com.tg.locationsystem.service.IMapService;
import com.tg.locationsystem.utils.PngToSvg;
import com.tg.locationsystem.utils.StringUtils;
import com.tg.locationsystem.utils.SystemMap;
import com.tg.locationsystem.utils.UploadFileUtil;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author hyy
 * @ Date2019/9/10
 */
@Controller
@RequestMapping("map")
public class MapController {
    @Autowired
    private IMapService mapService;
    @Autowired
    private IMapRuleService mapRuleService;
    @Autowired
    private ICleConfigService cleConfigService;

    /*
    * 导入地图
    * */
    @RequestMapping(value = "AddMap",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultBean AddMap(@Valid AddMapVO addMapVO, BindingResult result,
                             HttpServletRequest request, @RequestParam(value="map",required=false)MultipartFile file){
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
            resultBean.setCode(2);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        //该地图已经存在
        Map mymap=mapService.getMapByName(addMapVO.getMapName(),user.getId());
        if (mymap!=null){
            resultBean = new ResultBean();
            resultBean.setCode(102);
            resultBean.setMsg("该地图已经存在");
            List<Person> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (file==null){
            resultBean = new ResultBean();
            resultBean.setCode(103);
            resultBean.setMsg("地图不能为空");
            List<Person> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //判断输入是否正确
        if (!StringUtils.isNumeric(addMapVO.getPixelX())||!StringUtils.isNumeric(addMapVO.getPixelY())
                ||!StringUtils.isNumeric(addMapVO.getRealityX())||!StringUtils.isNumeric(addMapVO.getRealityY())){
            resultBean = new ResultBean();
            resultBean.setCode(103);
            resultBean.setMsg("地图参数有误,请输入整数");
            List<Person> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        //判断比例--像素/实际
        double pixelX = Double.parseDouble(addMapVO.getPixelX());
        double realityX = Double.parseDouble(addMapVO.getRealityX());
        double pro_x = pixelX / realityX;
        double pixelY =Double.parseDouble(addMapVO.getPixelY());
        double realityY = Double.parseDouble(addMapVO.getRealityY());
        double pro_y = pixelY / realityY;
        if (pro_x<1||pro_y<1){
            resultBean = new ResultBean();
            resultBean.setCode(103);
            resultBean.setMsg("地图参数有误,像素长宽应大于等于实际长宽");
            List<Person> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (Math.abs(pro_x-pro_y)>1){
            resultBean = new ResultBean();
            resultBean.setCode(103);
            resultBean.setMsg("地图参数有误,长宽倍数相差过大");
            List<Person> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }


        Map map=new Map();
        map.setMapName(addMapVO.getMapName());
        map.setRemark(addMapVO.getRemark());
        map.setPixelX(addMapVO.getPixelX());
        map.setPixelY(addMapVO.getPixelY());
        map.setRealityX(addMapVO.getRealityX());
        map.setRealityY(addMapVO.getRealityY());
        map.setProportion(String.valueOf((pro_x+pro_y)/2));

        //设置地图所属管理者
        map.setUserId(user.getId());
        //设置地图唯一的key
        String uuid = UUID.randomUUID().toString();
        map.setMapKey(uuid);

        //设置地图路径
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
            String newFileName = uuid+originalFilename;
            //封装上传文件位置的全路径
            File targetFile = new File(filePath,newFileName);
            //把本地文件上传到封装上传文件位置的全路径
            boolean png2svg=false;
            try {
                file.transferTo(targetFile);
                //判断，如果是图片，则需要将图片转换为svg格式
                if (!targetFile.getPath().endsWith("svg")){
                    //System.out.println("图片:"+targetFile.getPath());
                    String[] split = targetFile.getPath().split("\\.");
                    System.out.println(split.length);
                    StringBuffer sb=new StringBuffer();
                    for (int i = 0; i < split.length-1; i++) {
                       // System.out.println("分割:"+split[i]);
                        sb.append(split[i]);
                        sb.append(".");
                    }
                    sb.append("svg");
                    try {
                        png2svg = PngToSvg.png2svg(targetFile.getPath(), sb.toString());
                        if (png2svg){
                            map.setMapData(sb.toString());
                        }else {
                            resultBean = new ResultBean();
                            resultBean.setCode(106);
                            resultBean.setMsg("内部错误,地图导入失败");
                            List<Myuser> list = new ArrayList<>();
                            resultBean.setData(list);
                            resultBean.setSize(list.size());
                            return resultBean;
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("文件路径错误");
                        resultBean = new ResultBean();
                        resultBean.setCode(106);
                        resultBean.setMsg("地图导入失败");
                        List<Myuser> list = new ArrayList<>();
                        resultBean.setData(list);
                        resultBean.setSize(list.size());
                        return resultBean;
                    } catch (IOException e) {
                        System.err.println("图片转换错误");
                        resultBean = new ResultBean();
                        resultBean.setCode(106);
                        resultBean.setMsg("地图导入失败");
                        List<Myuser> list = new ArrayList<>();
                        resultBean.setData(list);
                        resultBean.setSize(list.size());
                        return resultBean;
                    }
                }else {
                    map.setMapData(targetFile.getPath());
                }

            } catch (IOException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(104);
                resultBean.setMsg("上传地图失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        MapRule mapRule=new MapRule();
        mapRule.setMapKey(uuid);
        //地图规则
        String rule = SVGUtil.readSVG(map.getMapData());
        mapRule.setMapRule(rule);

        //将规则放入缓存
        SystemMap.getMapRuleMap().put(uuid,rule);
        int insertMapRule = mapRuleService.insertSelective(mapRule);

        int insertMap = mapService.insertSelective(map);

        //默认cle配置
        CleConfig cleConfig=new CleConfig();
        cleConfig.setMapKey(uuid);
        cleConfig.setChannel("2");
        cleConfig.setAskTime("10");
        cleConfig.setSendTime("10");
        int insertconfig = cleConfigService.insertSelective(cleConfig);

        System.out.println("上传地图规则:"+insertMapRule);
        System.out.println("上传cle配置:"+insertconfig);

        if (insertMap>0){
            resultBean = new ResultBean();
            resultBean.setCode(105);
            resultBean.setMsg("地图导入成功");
            List<Map> list = new ArrayList<>();
            list.add(map);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(106);
            resultBean.setMsg("地图导入失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
    * 查看地图
    * */
    @RequestMapping(value = "getMaps",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getMaps(HttpServletRequest request,@RequestParam(defaultValue = "1") Integer pageIndex,
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
        PageInfo<Map> pageInfo=mapService.getMapsByUserId(pageIndex,pageSize,user.getId());
        List<MapVO> mapVOList=new ArrayList<>();
        for (Map map : pageInfo.getList()) {
            MapVO mapVO=new MapVO();
            mapVO.setId(map.getId());
            mapVO.setMapData(map.getMapData());
            mapVO.setMapKey(map.getMapKey());
            mapVO.setRemark(map.getRemark());
            mapVO.setUserId(map.getUserId());
            mapVO.setMapName(map.getMapName());
            StringBuffer sb=new StringBuffer();
            File file = new File(map.getMapData());// svg文件
            BufferedReader br = null;// 构造一个BufferedReader类来读取文件
            try {
                br = new BufferedReader(new FileReader(file));
                String s = null;
                while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                    sb.append(s);
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mapVO.setMapdata(sb.toString());
            //cle配置
            CleConfig cleConfig=cleConfigService.getConfigByMapKey(map.getMapKey());
            mapVO.setChannel(cleConfig.getChannel());
            mapVO.setAskTime(cleConfig.getAskTime());
            mapVO.setSendTime(cleConfig.getSendTime());
            mapVOList.add(mapVO);
        }
        PageInfo<MapVO> page= new PageInfo<>(mapVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(mapVOList);
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
    * 根据地图id读取地图svg文件
    * */
    @RequestMapping(value = "queryMap",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean queryMap(@RequestParam("") Integer MapId, HttpServletResponse response,
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
        if (MapId==null||"".equals(MapId)){
            resultBean = new ResultBean();
            resultBean.setCode(110);
            resultBean.setMsg("参数有误,地图id不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Map map = mapService.selectByPrimaryKey(MapId);
        if (map==null){
            resultBean = new ResultBean();
            resultBean.setCode(107);
            resultBean.setMsg("该地图为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        StringBuffer sb=new StringBuffer();
        File file = new File(map.getMapData());// svg文件
        BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
        String s = null;
        while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
            sb.append(s);
        }
        br.close();

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List<String> list=new ArrayList<>();
        list.add(sb.toString());
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    /*
     * 根据地图key读取地图svg文件
     * */
    @RequestMapping(value = "queryMapByKey",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean queryMapByKey(@RequestParam("") String MapKey, HttpServletResponse response,
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
        if (MapKey==null||"".equals(MapKey)){
            resultBean = new ResultBean();
            resultBean.setCode(111);
            resultBean.setMsg("参数有误,地图key不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Map map = mapService.getMapByUuid(MapKey);
        if (map==null){
            resultBean = new ResultBean();
            resultBean.setCode(107);
            resultBean.setMsg("该地图为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        StringBuffer sb=new StringBuffer();
        File file = new File(map.getMapData());// svg文件
        BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
        String s = null;
        while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
            sb.append(s);
        }
        br.close();

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List<String> list=new ArrayList<>();
        list.add(sb.toString());
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    /*
     * 删除地图
     * */
    @RequestMapping(value = "deleteMap",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultBean deleteMap(@RequestParam("") String MapKey,HttpServletRequest request
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
        if (MapKey==null||"".equals(MapKey)){
            resultBean = new ResultBean();
            resultBean.setCode(111);
            resultBean.setMsg("参数有误,地图key不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Map map = mapService.getMapByUuid(MapKey);
        if (map==null){
            resultBean = new ResultBean();
            resultBean.setCode(107);
            resultBean.setMsg("该地图为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        int delete=mapService.deleteMapByKey(MapKey);
        int deletemaprule= mapRuleService.deleteByMapKey(MapKey);
        int deleteconfig=cleConfigService.deletecofigByMapKey(MapKey);
        if (delete>0){
            //清除缓存
            String clekey = SystemMap.getKey(SystemMap.getCleAndKeyMap(), MapKey);
            if (clekey!=null){
                SystemMap.getCleAndKeyMap().remove(clekey);
                //清除规则
                SystemMap.getMapRuleMap().remove(MapKey);

            }



            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List<Map> list=new ArrayList<>();
            list.add(map);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        resultBean = new ResultBean();
        resultBean.setCode(117);
        resultBean.setMsg("删除地图失败");
        List<Map> list=new ArrayList<>();
        list.add(map);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

    /*
    * cle下发指令
    * */
    @RequestMapping(value = "cleConfig",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultBean cleConfig(@Valid CleconfigVO cleconfigVO, BindingResult result,
                                HttpServletRequest request){
        ResultBean resultBean;
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
            resultBean.setCode(2);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        CleConfig configByMapKey = cleConfigService.getConfigByMapKey(cleconfigVO.getMapKey());
        if (configByMapKey==null){
            resultBean = new ResultBean();
            resultBean.setCode(107);
            resultBean.setMsg("该地图为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        String key = SystemMap.getCleAndKeyMap().get(cleconfigVO.getMapKey());
        if (key==null||"".equals(key)){
            resultBean = new ResultBean();
            resultBean.setCode(109);
            resultBean.setMsg("定位引擎未连接");
            List<Station> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //更新数据库数据
        configByMapKey.setChannel(cleconfigVO.getChannel());
        configByMapKey.setSendTime(cleconfigVO.getSendTime());
        configByMapKey.setAskTime(cleconfigVO.getAskTime());
        cleConfigService.updateByPrimaryKeySelective(configByMapKey);

        //todo 配置信息发送给cle
        bean.CleConfig config=new bean.CleConfig();
        config.setChannel(cleconfigVO.getChannel());
        config.setAskTime(cleconfigVO.getAskTime());
        config.setSendTime(cleconfigVO.getSendTime());
        LocationsystemApplication.skylabSDK.configTag(key,config);

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List<Map> list=new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}
