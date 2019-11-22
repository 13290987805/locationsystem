package com.tg.locationsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author hyy
 * @ Date2019/7/19
 */
@Controller
@RequestMapping("image")
public class ImageController {
    /*
    * 根据图片地址读出图片
    * */
    @RequestMapping(value = "queryImg",method = RequestMethod.GET)
    public void querygoodsTypeImg(@RequestParam("imageUrl") String url, HttpServletResponse response,
                                  HttpServletRequest request) throws IOException {
       // System.out.println("图片地址:"+url);
        if (url==null || "".equals(url)){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("暂无图片");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if (url.contains("\\")){
            url=url.replace("\\\\","/");
        }else {
            url=url.replace("\\\\","/");
        }

        FileInputStream hFile = null;      //得到文件大小
        try {
            hFile = new FileInputStream(url);
            int i=hFile.available();
            byte data[]=new byte[i];        //读数据

            hFile.read(data);         //得到向客户端输出二进制数据的对象

            OutputStream toClient=response.getOutputStream();         //输出数据

            toClient.write(data);

            toClient.flush();
            toClient.close();

            hFile.close();
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(e.getMessage());
        } finally {
            //重新设置session存活时间
            //request.getSession().setMaxInactiveInterval(30*60);
        }

    }
}
