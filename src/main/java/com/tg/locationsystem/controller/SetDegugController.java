package com.tg.locationsystem.controller;

import boot.SkylabSDK;
import com.tg.locationsystem.LocationsystemApplication;
import com.tg.locationsystem.entity.Map;
import com.tg.locationsystem.entity.Station;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.service.IMapService;
import com.tg.locationsystem.utils.SystemMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/11/25
 */
@Controller
@RequestMapping("setDebug")
public class SetDegugController {
    @Autowired
    private IMapService mapService;
    /*
    * sdk设置debug
    * */
    @RequestMapping(value = "setDebug",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultBean setDegug(HttpServletRequest request
            ,@RequestParam("") String debug) {
        ResultBean resultBean;
        if (debug==null||"".equals(debug)){
            resultBean = new ResultBean();
            resultBean.setCode(109);
            resultBean.setMsg("debug参数不能为空");
            List<Station> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if ("true".equals(debug)){
            SkylabSDK.debug=true;
        }else {
            SkylabSDK.debug=false;
        }
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list=new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
        }

}
