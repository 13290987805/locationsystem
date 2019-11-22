package com.tg.locationsystem.controller;

import com.tg.locationsystem.config.KalmanFilter;
import com.tg.locationsystem.pojo.HeartRateHistoryVO;
import com.tg.locationsystem.service.IHeartRateHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
    * 测试心率
    * */
    @RequestMapping(value = "test")
    @ResponseBody
    public String AddTag(Model model){
       return "hello";

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
