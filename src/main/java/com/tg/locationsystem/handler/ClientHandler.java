package com.tg.locationsystem.handler;

import com.tg.locationsystem.entity.Tag;
import com.tg.locationsystem.entity.TagTest;
import com.tg.locationsystem.service.ITagTestService;
import com.tg.locationsystem.utils.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hyy
 * @ Date2020/3/19
 */
//@Component
public class ClientHandler extends SimpleChannelInboundHandler<Object> {
    @Autowired
    private ITagTestService tagTestService;
    public static ClientHandler clientHandler;
    @PostConstruct
    public void init() {
        clientHandler = this;
        clientHandler.tagTestService=this.tagTestService;
    }
    public static int insert(TagTest tagTest){
        return clientHandler.tagTestService.insertSelective(tagTest);
    }
    private static Integer sum=1000;
    private static Integer count=0;

    DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    //文件名
    String fileName = "C:\\data\\标签数据表"+simpleDateFormat.format(new Date())+".txt";
    String data="";



    public static void main(String[] args) throws Exception {
        String host="192.168.1.186";
        int port=3335;
        new NettyClient(host,port).start();
    }

    //处理服务端返回的数据
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        try {
            // System.out.println("22ctx: "+ctx);
            //解析msg得到apmac,TODO
            //System.out.printl
            ByteBuf in = (ByteBuf)msg;
            int readableBytes = in.readableBytes();
            byte[] bytes =new byte[readableBytes];
            in.readBytes(bytes);
            //将Byte[]字节数组转为十六进制。
            String string = StringUtils.ByteArrToHex(bytes);
            string=string.replace(" ","");
            //System.out.println(string);
            while (string.length()>0) {
                //位置信息
                if (string.startsWith("65")) {
                    if (string.length() < 146) {
                        // System.out.println("数据长度有误:"+string);
                        //错误数据存到文件中
                        //filewriteutil.filewrite(simpleDateFormat.format(new Date())+":"+string,dirpath+"\\"+sdf2.format(new Date())+"_error.txt");
                        return;
                    }
                    //标签address
                    String tagAdd = string.substring(50, 66);
                    //System.out.println(tagAdd);

                    /*if (!"A3DB91CF0001CADE".equals(tagAdd)){
                        //A3DB91CF0001CADE
                        return;
                    }*/

                    //System.out.println("收到abby标签数据:"+tagAdd);
                    String Xstring = string.substring(2, 18);
                    byte[] Xbytes = StringUtils.HexToByteArr(Xstring);
                    double vx = StringUtils.bytes2Double(Xbytes);
                    double x = StringUtils.round(vx);

                    //y坐标
                    String Ystring = string.substring(18, 34);
                    byte[] Ybytes = StringUtils.HexToByteArr(Ystring);
                    double vy = StringUtils.bytes2Double(Ybytes);
                    double y = StringUtils.round(vy);

                    //z坐标
                    String Zmsg = string.substring(34, 50);
                    //System.out.println(Zmsg);
                    byte[] Zbytes = StringUtils.HexToByteArr(Zmsg);
                    double vz = StringUtils.bytes2Double(Zbytes);
                    double z = StringUtils.round(vz);

                    TagTest tagTest=new TagTest();
                    tagTest.setUpdateTime(new Date());
                    tagTest.setX((int) x);
                    tagTest.setY((int) y);
                    tagTest.setAddress(tagAdd);
                    int insert = insert(tagTest);
                    System.out.println("结果:"+insert);


                 /*   File file=new File(fileName);

                    if(!file.exists())
                    {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (count<sum){
                        // data=tagAdd+";"+x+";"+y+";"+simpleDateFormat.format(new Date());
                        data=tagAdd+";"+x+";"+y;
                        filewriteutil.filewrite(data,fileName);
                        count++;
                    }else {
                        //超过一千条,退出
                        System.exit(0);
                    }*/


                   /* if (filewriteutil.isStartWithNumber(tagAdd)){
                        data=tagAdd+"\t\t\t"+x+"\t\t\t"+y+"\t\t\t"+simpleDateFormat.format(new Date());
                    }else {
                        String secondData=tagAdd.substring(1);
                        if (filewriteutil.isStartWithNumber(secondData)){
                            data=tagAdd+"\t\t\t"+x+"\t\t\t"+y+"\t\t\t"+simpleDateFormat.format(new Date());
                        }else {
                            String tgreeData=secondData.substring(1);
                            if (filewriteutil.isStartWithNumber(tgreeData)){
                                data=tagAdd+"\t\t\t"+x+"\t\t\t"+y+"\t\t\t"+simpleDateFormat.format(new Date());
                            }else {
                                data=tagAdd+"\t\t"+x+"\t\t"+y+"\t\t"+simpleDateFormat.format(new Date());
                            }

                        }
                    }*/



                    //截取string
                    string = string.substring(146);
                }else if (string.startsWith("66")){
                    if (string.length()<50){
                        //  System.out.println("数据长度有误:"+string);
                        return;
                    }
                    string=string.substring(50);
                }else {
                    return;
                }
            }

        }finally {
            // 抛弃收到的数据
            /* ReferenceCountUtil.release(msg);*/
        }
    }


}
