package com.tg.locationsystem.controller;

import com.tg.locationsystem.service.IHeartRateHistoryService;
import com.tg.locationsystem.utils.SystemMap;
import com.tg.locationsystem.utils.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author hyy
 * @ Date2019/8/2
 */
@Controller
@RequestMapping("test")
public class testController {
    @Autowired
    private IHeartRateHistoryService heartRateHistoryService;
    /*
    * 得到请求者的ip
    * */
    @GetMapping(value = "/{id}")
    @ResponseBody
    public String AddTag(HttpServletRequest request,
                         @PathVariable("id") String id){
        /*String ip = TestUtil.getIP(request);
        return ip;*/
        return TestUtil.getGoP(request);

    }/*
     * 得到系统启动时间
     * */
    @GetMapping(value = "/getTime")
    @ResponseBody
    public Long getTime(HttpServletRequest request){
        Long startTime = SystemMap.getTimeMap().get("startTime");
        if (startTime == null){
            startTime = System.currentTimeMillis();
        }
        return startTime;

    }
    /*
     * 查看图片
     * 图片下载
     * */
    @RequestMapping(value = "queryImg",method = RequestMethod.GET)
    public void queryImg( HttpServletResponse response,
                         HttpServletRequest request) throws IOException {
       String img="D:\\img\\aa.jpg";
        if (img==null||"".equals(img)){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("查询不到相关信息");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (img==null || "".equals(img)){
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
            hFile = new FileInputStream(img);
            int i=hFile.available();
            byte data[]=new byte[i];        //读数据

            hFile.read(data);         //得到向客户端输出二进制数据的对象

            OutputStream toClient=response.getOutputStream();         //输出数据

            toClient.write(data);

            toClient.flush();
            toClient.close();

            hFile.close();
        } catch (FileNotFoundException e) {
            response.setContentType("text/html;charset=UTF-8");
            try {
                response.getWriter().write("图片读取失败");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            response.getWriter().write("图片读取失败");
        }finally {
            //重新设置session存活时间
            //request.getSession().setMaxInactiveInterval(30*60);
        }


    }
}
