package com.tg.locationsystem.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;

/**
 * @author hyy
 * @ Date2020/3/11
 */
@RestController
@RequestMapping("vedio")
public class VedioController {

    /*
    * 视频下载
    * */
    @RequestMapping(value = "downVedio",method = RequestMethod.GET)
    public void   querygoodsTypeImg(@RequestParam("VedioUrl") String url, HttpServletResponse response,
                                  HttpServletRequest request) throws IOException {



        // System.out.println("图片地址:"+url);
       if (url==null || "".equals(url)){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("暂无视频");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ;
        }
        //要下载的文件名 从前台传来
        //String fileNameNeedDown = request.getParameter("fileName");
      /*  String fileNameNeedDown="out000.mp4";
        //这里的路径是要下载的文件所在路径
        String realPath = request.getServletContext().getRealPath("/")+"printController\\";
        //要下载的文件路径+文件名
        String aFilePath = realPath + fileNameNeedDown;*/

      //文件名
        //多个视频地址
        String[] split = url.split(",");
        for (String s : split) {
            String fileNameNeedDown =url.substring(9);
            File file = null;
            file = new File(url);

            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                String aFileName = null;
                request.setCharacterEncoding("UTF-8");
                String agent = request.getHeader("User-Agent").toUpperCase();
                if ((agent.indexOf("MSIE") > 0)
                        || ((agent.indexOf("RV") != -1) && (agent
                        .indexOf("FIREFOX") == -1)))
                    aFileName = URLEncoder.encode(fileNameNeedDown, "UTF-8");
                else {
                    aFileName = new String(fileNameNeedDown.getBytes("UTF-8"), "ISO8859-1");
                }
                response.setContentType("application/octet-stream");//octet-stream为要下载文件是exe类型或看该文档http://www.w3school.com.cn/media/media_mimeref.asp
                response.setHeader("Content-disposition", "attachment; filename="
                        + aFileName);
                response.setHeader("Content-Length", String.valueOf(file.length()));
                bis = new BufferedInputStream(new FileInputStream(new File(url)));
                bos = new BufferedOutputStream(response.getOutputStream());
                byte[] buff = new byte[2048];
                int bytesRead;
                while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))
                    bos.write(buff, 0, bytesRead);
                System.out.println("success");
                bos.flush();
            } catch (Exception e) {
                System.out.println("失败！");
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
//				file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

       // return new JsonView();


    }


    /*
    * 响应视频流给浏览器
    * */
    @RequestMapping(value = "queryVedio",method = RequestMethod.GET)
    public void   queryVedio(@RequestParam("VedioUrl") String url, HttpServletResponse response,
                             HttpServletRequest request) throws IOException {


        // System.out.println("图片地址:"+url);
        if (url==null || "".equals(url)){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("暂无视频");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ;
        }

        show(response,url,"video");
    }

    private void show(HttpServletResponse response, String url, String type) {
        try{
            FileInputStream fis = new FileInputStream(url); // 以byte流的方式打开文件
            int i=fis.available(); //得到文件大小
            byte data[]=new byte[i];
            fis.read(data);  //读数据
            response.setContentType(type+"/*"); //设置返回的文件类型
            OutputStream toClient=response.getOutputStream(); //得到向客户端输出二进制数据的对象
            toClient.write(data);  //输出数据
            toClient.flush();
            toClient.close();
            fis.close();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("文件不存在");
        }


    }
}
