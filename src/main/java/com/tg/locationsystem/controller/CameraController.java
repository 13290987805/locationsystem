package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.LocationsystemApplication;
import com.tg.locationsystem.entity.Camera;
import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.service.ICameraService;
import com.tg.locationsystem.utils.IPUtil;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author hyy
 * @ Date2020/2/26
 */
@Controller
@RequestMapping("camera")
public class CameraController {
    @Autowired
    private ICameraService cameraService;


    /*
     * 新增摄像头
     * */
    @RequestMapping(value = "addCamera", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultBean addCamera(@Valid Camera camera, BindingResult result,
                                HttpServletRequest request) throws UnknownHostException {

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

        boolean isRightIP = IPUtil.isRightIP(camera.getCameraIp());
        if (!isRightIP) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("摄像头ip有误,请检查");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Camera existCamera = cameraService.selectByMapKeyAndCameraIp(camera.getMapKey(), camera.getCameraIp());
        if (existCamera != null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("同一地图不能出现Ip相同的设备。");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        camera.setCreateTime(new Date());

        //rtmp://localhost/live/192.168.3.40

        StringBuffer sb = new StringBuffer("rtmp://");
        //获取本机ip地址
        InetAddress addr = InetAddress.getLocalHost();
        sb.append(addr.getHostAddress());
        sb.append("/live/");
        String uuid = UUID.randomUUID().toString();
        sb.append(uuid);
        camera.setCameraStreamMediaAddress(sb.toString());

        int i = cameraService.insertSelective(camera);
        if (i > 0) {
            SystemMap.getCameramap().put(camera.getId(), uuid);

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("摄像头添加成功");
            List<Camera> list = new ArrayList<>();
            list.add(camera);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("摄像头添加失败");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }

    /*
     * 编辑修改摄像头
     *
     * */
    @RequestMapping(value = "updateCamera", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean updateCamera(@Valid Camera camera, BindingResult result,
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
        Camera sqlcamera = cameraService.selectByPrimaryKey(camera.getId());
        if (sqlcamera == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该摄像头不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Camera existCamera = cameraService.selectByMapKeyAndCameraIp(camera.getMapKey(), camera.getCameraIp());
        if (existCamera != null && existCamera.getId() != camera.getId()) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("同一地图不能出现Ip相同的设备。");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        int update = cameraService.updateByPrimaryKeySelective(camera);

        if (update > 0) {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("摄像头修改成功");
            List<Camera> list = new ArrayList<>();
            list.add(camera);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("摄像头修改失败");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
     *
     * 调用摄像头
     * */
    @RequestMapping(value = "startCamera", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultBean startCamera(@RequestParam("") Integer cameraId, HttpServletRequest request) {
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
        if ("".equals(cameraId) || cameraId == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("摄像头id参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Camera camera = cameraService.selectByPrimaryKey(cameraId);
        if (camera == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该摄像头不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //String ip ="192.168.3.40";
        String ip = camera.getCameraIp();
        String id = SystemMap.getCameramap().get(cameraId);
        if (id == null || "".equals(id)) {
            String address = camera.getCameraStreamMediaAddress();
            id = address.substring(22);
            SystemMap.getCameramap().put(camera.getId(), id);
        }
        //System.out.println("id="+id);
        //"rtsp://admin:Z1234567@" + ip + ":554"
        String sb = "rtsp://" + camera.getCameraUsername() + ":" + camera.getCameraPwd() + "@" + ip + ":554";
        //String sb="rtsp://"+"admin:Z1234567@" + ip + ":554";


        Map<String, String> map = new HashMap<>();
        map.put("appName", id);
        //map.put("input", "rtsp://admin:Z1234567@" + ip + ":554");
        map.put("input", sb.toString());
        map.put("output", "rtmp://localhost/live/");
        map.put("codec", "libx264");
        map.put("fmt", "flv");
        map.put("fps", "60");
        map.put("rs", "640x360");
        map.put("twoPart", "0");
        //String ids=manager.start(map);
        LocationsystemApplication.manager.start(map);
        //System.out.println(ids);


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("摄像头调用成功");
        List<Camera> list = new ArrayList<>();
        list.add(camera);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }


    /*
     * 停止摄像头
     *单个
     * */
    @RequestMapping(value = "stopCamera", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultBean stopCamera(@RequestParam("") Integer cameraId, HttpServletRequest request) {
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
        if ("".equals(cameraId) || cameraId == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("摄像头id参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Camera camera = cameraService.selectByPrimaryKey(cameraId);
        if (camera == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该摄像头不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        String uuid = SystemMap.getCameramap().get(cameraId);
        //停止调用摄像头命令
        boolean stop = LocationsystemApplication.manager.stop(uuid);
        if (stop) {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("摄像头停止调用");
            List<Camera> list = new ArrayList<>();
            list.add(camera);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("停止调用失败");
            List<Camera> list = new ArrayList<>();
            list.add(camera);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }


    }

    /*
     * 停止摄像头
     *全部
     * */
    @RequestMapping(value = "stopAllCamera", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultBean stopAllCamera(HttpServletRequest request) {
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

        //停止调用摄像头命令

        int stop = LocationsystemApplication.manager.stopAll();
        if (stop > 0) {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("摄像头停止调用");
            List<Camera> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("停止调用失败");
            List<Camera> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }


    }


    /*
     * 查看某地图下的所有摄像头
     * 分页
     * */
    @RequestMapping(value = "getCameraByMapKeyPage", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultBean getCameraByMapKeyPage(@RequestParam("") String mapKey, HttpServletRequest request,
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
        if ("".equals(mapKey) || mapKey == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("mapKey地图参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        PageInfo<Camera> pageInfo = cameraService.getCaneraByMapKeyPage(mapKey, pageIndex, pageSize);


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list = new ArrayList<>();
        list.add(pageInfo);
        resultBean.setData(list);
        resultBean.setSize(pageInfo.getSize());
        return resultBean;
    }

    /*
     * 查看某地图下的所有摄像头
     * 不分页
     * */
    @RequestMapping(value = "getCameraByMapKey", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultBean getCameraByMapKey(@RequestParam("") String mapKey, HttpServletRequest request) {
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
        if ("".equals(mapKey) || mapKey == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("mapKey地图参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        List<Camera> cameraList = cameraService.getCameraByMapKey(mapKey);


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(cameraList);
        resultBean.setSize(cameraList.size());
        return resultBean;
    }

    /*
     * 删除摄像头信息
     * */
    @RequestMapping(value = "delCamera", method = {RequestMethod.POST})
    @ResponseBody
    public ResultBean deleteCamera(@RequestParam("") Integer cameraId, HttpServletRequest request) {
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
        if ("".equals(cameraId) || cameraId == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("摄像头id不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //该摄像头是否存在
        Camera camera = cameraService.selectByPrimaryKey(cameraId);
        if (camera == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("摄像头不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        int delete = cameraService.deleteByPrimaryKey(cameraId);
        if (delete > 0) {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("摄像头删除成功");
            List<Camera> list = new ArrayList<>();
            list.add(camera);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("摄像头删除失败");
            List<Camera> list = new ArrayList<>();
            list.add(camera);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
}
