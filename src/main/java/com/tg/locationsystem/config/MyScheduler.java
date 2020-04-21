package com.tg.locationsystem.config;

import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.DateUtil;
import com.tg.locationsystem.utils.MD5Tools;
import com.tg.locationsystem.utils.SystemMap;
import com.tg.locationsystem.utils.influxDBUtil.InfluxDBConnection;
import org.apache.shiro.session.mgt.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hyy
 * @ Date2019/7/18
 */
@Component
public class MyScheduler {
    public static MyScheduler scheduler;
    @Autowired
    private ITagHistoryService tagHistoryService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private IPersonTypeService personTypeService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IGoodsTypeService goodsTypeService;
    @Autowired
    private IStationService stationService;
    @Autowired
    private InfluxDBConnection influxDBConnection;


    public static final int UP_TIME=15;
    public static final int STATION_TIME=15;
    private  SimpleDateFormat dateFormat =
            new SimpleDateFormat("YYYYMMdd");
    DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final int DAY_TIME=60*60*24;

    //系统运行时常配置文件
    public  static String path="C:\\data\\time.txt";


    @PostConstruct
    public void init(){
        scheduler=this;
        scheduler.tagHistoryService=this.tagHistoryService;
        scheduler.tagService=this.tagService;
        scheduler.personService=this.personService;
        scheduler.personTypeService=this.personTypeService;
        scheduler.goodsService=this.goodsService;
        scheduler.goodsTypeService=this.goodsTypeService;
    }
    public static int existTable(String format){
        return scheduler.tagHistoryService.existTable(format);
    }
    public static int createNewTable(String format){
        return scheduler.tagHistoryService.createNewTable(format);
    }
    public static Tag getTagByOnlyAddress(String aAddress){
        return scheduler.tagService.getTagByOnlyAddress(aAddress);
    }
    public static int inserttagHistory(String format,TagHistory tagHistory){
        return scheduler.tagHistoryService.inserttagHistory(format,tagHistory);
    }
    public static int updatetag(Tag tag){
        return scheduler.tagService.updateByPrimaryKeySelective(tag);
    }
    public static Person getPersonByImg(String img){
        return scheduler.personService.getPersonByImg(img);
    }
    public static PersonType getPersonTypeByImg(String img){
        return scheduler.personTypeService.getPersonTypeByImg(img);
    }
    public static Goods getGoodsByImg(String img){
        return scheduler.goodsService.getGoodsByImg(img);
    }
    public static GoodsType getGoodsTypeByImg(String img){
        return scheduler.goodsTypeService.getGoodsTypeByImg(img);
    }

    //每秒执行一次 从缓存中把标签更新到数据库
    @Scheduled(cron ="0/1 * * * * ?" )
    public void testTasks4() throws ParseException {
        Map<String, String> map = SystemMap.getMap();
        Date tagDate;
        //遍历map中的值
        for (String key : map.keySet()) {
            String value = map.get(key);
            //System.out.println("Key = " + key + ", Value = " + value);
            if (value!=null&&!"".equals(value)){
                String[] split = value.split(",");
                Tag tag = tagService.getTagByOnlyAddress(key);
                if (tag==null){
                    continue;
                }
                if (split[0].toString().equals("")||split[0]==null||Double.isNaN(Double.parseDouble(split[0]))){
                    split[0]=String.valueOf(tag.getX());
                }
                if (split[1].toString().equals("")||split[1]==null||Double.isNaN(Double.parseDouble(split[1]))){
                    split[1]=String.valueOf(tag.getY());
                }
                if (split[2].toString().equals("")||split[2]==null||Double.isNaN(Double.parseDouble(split[2]))){
                    split[2]=String.valueOf(tag.getZ());
                }
                if (split[3].toString().equals("")||split[3]==null){
                    split[3]=String.valueOf(sdf2.format(tag.getLastonline()));
                }
                //更新标签表
                //System.out.println("ssssssss" + split[3]);
                tagDate = sdf2.parse(split[3]);
                tag.setX(Double.parseDouble(split[0]));
                tag.setY(Double.parseDouble(split[1]));
                tag.setZ(Double.parseDouble(split[2]));
                tag.setLastonline(tagDate);
                if (split[4]!=null&&!"".equals(split[4])){
                    tag.setMapKey(split[4]);
                }
                long now = System.currentTimeMillis()/1000;
                long time = tagDate.getTime()/1000;
                //System.out.println("当前时间-系统时间:"+(now-time));
                if (now-time<UP_TIME){
                    tag.setIsonline("1");
                }else {
                    tag.setLastoffline(tagDate);
                    tag.setIsonline("0");
                }

                try {
                    updatetag(tag);
                }catch (Exception s){
                    System.out.println("更新标签:"+tag.toString());
                    s.printStackTrace();
                }


                if ("1".equals(tag.getIsonline())) {
                    //System.out.println("into insert and x = " + Double.parseDouble(split[0]));
                    try {
                        //将标签的数据存到历史记录表
                        TagHistory tagHistory = new TagHistory();
                        String peridcard = SystemMap.getTagAndPersonMap().get(key);
                        tagHistory.setPersonIdcard(peridcard);
                        tagHistory.setX(Double.parseDouble(split[0]));
                        tagHistory.setY(Double.parseDouble(split[1]));
                        tagHistory.setZ(Double.parseDouble(split[2]));
                        tagHistory.setTime(tagDate);
                        if (split[4]!=null&&!"".equals(split[4])){
                            tagHistory.setMapKey(split[4]);
                        }

                        //选择要插入的表格,不存在就创建
                        String format2 = dateFormat.format(new Date());
                        format2 = "tag_history_" + format2;
                        int existTable = tagHistoryService.existTable(format2);
                        if (existTable == 0) {
                            System.out.println(sdf2.format(new Date()) + format2 + "表不存在...");
                            int create = tagHistoryService.createNewTable(format2);
                            if (create == 0) {
                                System.out.println("新建了一张表:" + format2);
                            }
                        }
                        //往记录表插入数据
                        inserttagHistory(format2, tagHistory);




                        //往记录表插入数据
                        //inserttagHistory(format2, tagHistory);
                        String measurement = "tagHistory" + key;
                        Map<String, String> tags = new HashMap<String, String>();
                        Map<String, Object> fields = new HashMap<String, Object>();
                        tags.put("mapKey",split[4]);
                        tags.put("personIdcard",peridcard);

                        fields.put("x",Double.parseDouble(split[0]));
                        fields.put("y",Double.parseDouble(split[1]));
                        fields.put("z",Double.parseDouble(split[2]));
                        //System.err.println(System.currentTimeMillis());
                        influxDBConnection.insert(measurement,tags,fields, DateUtil.utcToLocal(tagDate).getTime(), TimeUnit.MILLISECONDS);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

        }
    }








    //每秒执行一次 从缓存中把标签更新到数据库
    //@Scheduled(cron ="0/1 * * * * ?" )
    public void testTasks2() throws ParseException, IOException {
        //System.out.println(sdf2.format(new Date()));
        Map<String, String> map = SystemMap.getMap();
        String format = dateFormat.format(new Date());
       // System.out.println("所有map长度:"+map.size());
        //System.out.println(format+":"+map.size());
        //遍历map中的值

        for (String key : map.keySet()) {
            String value = map.get(key);
            //System.out.println("Key = " + key + ", Value = " + value);
            if (value!=null&&!"".equals(value)){
                String[] split = value.split(",");
                Tag tag = tagService.getTagByOnlyAddress(key);
                if (tag==null){
                    continue;
                }
               if (split[0].toString().equals("")||split[0]==null||Double.isNaN(Double.parseDouble(split[0]))){
                   split[0]=String.valueOf(tag.getX());
               }
                if (split[1].toString().equals("")||split[1]==null||Double.isNaN(Double.parseDouble(split[1]))){
                    split[1]=String.valueOf(tag.getY());
                }
                if (split[2].toString().equals("")||split[2]==null||Double.isNaN(Double.parseDouble(split[2]))){
                    split[2]=String.valueOf(tag.getZ());
                }
                if (split[3].toString().equals("")||split[3]==null){
                    split[3]=String.valueOf(sdf2.format(tag.getLastonline()));
                }
                    //更新标签表

                    tag.setX(Double.parseDouble(split[0]));
                    tag.setY(Double.parseDouble(split[1]));
                    tag.setZ(Double.parseDouble(split[2]));
                    tag.setLastonline(sdf2.parse(split[3]));
                if (split[4]!=null&&!"".equals(split[4])){
                    tag.setMapKey(split[4]);
                }
                    long now = System.currentTimeMillis()/1000;
                    long time = sdf2.parse(split[3]).getTime()/1000;
                    //System.out.println("当前时间-系统时间:"+(now-time));
                    if (now-time<UP_TIME){
                        tag.setIsonline("1");
                    }else {
                        tag.setLastoffline(sdf2.parse(split[3]));
                        tag.setIsonline("0");
                    }

                    try {
                        updatetag(tag);
                    }catch (Exception s){
                        System.out.println("更新标签:"+tag.toString());
                        s.printStackTrace();
                    }


                    if ("1".equals(tag.getIsonline())) {
                        try {
                            //将标签的数据存到历史记录表
                            TagHistory tagHistory = new TagHistory();
                            String peridcard = SystemMap.getTagAndPersonMap().get(key);
                            tagHistory.setPersonIdcard(peridcard);
                            tagHistory.setX(Double.parseDouble(split[0]));
                            tagHistory.setY(Double.parseDouble(split[1]));
                            tagHistory.setZ(Double.parseDouble(split[2]));
                            tagHistory.setTime(sdf2.parse(split[3]));
                            if (split[4]!=null&&!"".equals(split[4])){
                                tagHistory.setMapKey(split[4]);
                            }


                            //选择要插入的表格,不存在就创建
                            String format2 = dateFormat.format(new Date());
                            format2 = "tag_history_" + format2;
                            int existTable = tagHistoryService.existTable(format2);
                            if (existTable == 0) {
                                System.out.println(sdf2.format(new Date()) + format2 + "表不存在...");
                                int create = tagHistoryService.createNewTable(format2);
                                if (create == 0) {
                                     System.out.println("新建了一张表:" + format2);
                                }
                            }
                            //往记录表插入数据
                            inserttagHistory(format2, tagHistory);
                        } catch (ParseException e) {
                            System.out.println("时间转换错误..");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

            }

        }
       // System.out.println("处理长度:"+count);
       /* if (count>0) {
            String property = System.getProperty("user.dir");
            String[] pathsplit = property.split(":");
            String path=pathsplit[0]+":"+"\\log\\data";
            boolean dir = filewriteutil.createDir(path);

            //将每秒执行的标签次数存到文件中
            filewriteutil.filewrite(sdf2.format(new Date()) + ":" + count, path + "\\count.txt");
        }*/
    }

     /*
     * 0 0 12 * * ? 每天12点
      * 定时删除时间照片
      * */

/*    @Scheduled(cron ="0 0 12 * * ?" )
    public void testTasks3() {
        String personPhoto="C:\\whzy\\locationsystem\\src\\main\\resources\\static\\person";
        File personfile=new File(personPhoto);
        String[] personlist = personfile.list();

        for (String s : personlist) {
            String personpath=personPhoto+"\\"+s;
            boolean personExist=true;
            System.out.println(personpath);
            Person person = getPersonByImg(personpath);
            if (person==null){
                //personExist=false;
                PersonType personType = getPersonTypeByImg(personpath);
                if (personType==null){
                    personExist=false;
                }else {
                    personExist=true;
                }
            }else {
                personExist=true;
            }

            //该url在数据库不存在,删除该图片
            if (!personExist){
                File deletefile=new File(personpath);
                deletefile.delete();
            }
            personpath=personPhoto;
        }
        String goodsPhoto="C:\\whzy\\locationsystem\\src\\main\\resources\\static\\goods";
        File goodsfile=new File(goodsPhoto);
        String[] goodslist = goodsfile.list();
        for (String s : goodslist) {
            String goodspath=goodsPhoto+"\\"+s;
            boolean personExist=true;
            System.out.println(goodspath);
            Goods goods = getGoodsByImg(goodspath);
            if (goods==null){
                //personExist=false;
                GoodsType goodsType = getGoodsTypeByImg(goodspath);
                if (goodsType==null){
                    personExist=false;
                }else {
                    personExist=true;
                }
            }else {
                personExist=true;
            }

            //该url在数据库不存在,删除该图片
            if (!personExist){
                File deletefile=new File(goodspath);
                deletefile.delete();
            }
            goodspath=goodsPhoto;
        }

    }*/

     //todo 定时更新基站状态
    //每秒执行一次 从缓存中把标签更新到数据库
     @Scheduled(cron ="0/10 * * * * ?" )
     public void StationTasks(){
         List<Station> stationList=stationService.getAllStationList();
         for (Station station : stationList) {
            if (station.getLastonline()==null){
                station.setStationStatus(0);
                //更新基站状态
                stationService.updateByPrimaryKeySelective(station);
                continue;
            }
             long Station_time = station.getLastonline().getTime();
             long now_time = System.currentTimeMillis();
             if ((now_time-Station_time)<STATION_TIME){
                 station.setStationStatus(1);
                 //更新基站状态
                 stationService.updateByPrimaryKeySelective(station);
             }else {
                 station.setStationStatus(0);
                 //更新基站状态
                 stationService.updateByPrimaryKeySelective(station);
             }
         }
     }

    @Scheduled(cron ="0/3 * * * * ?" )
    //0 0 0/1 * * ?
    public void SystemTasks() throws IOException {
       /* File file=new File(path);
        if (!file.exists()){
            //退出
            System.exit(0);
        }*/

        BufferedReader br = null;
        try {
            br = new BufferedReader
                    (new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //文件异常,退出
            System.out.println("密钥文件异常...");
            List<String> secretList = SystemMap.getSecretList();
            if (secretList==null||secretList.size()==0){
                SystemMap.getSecretList().add("user_secret");
            }

        }

        String read;
        List<String> list=new ArrayList<>();
        //4. 循环读取, 只要条件满足就一直读, 并将读取到的内容赋值给变量.
        while((read=br.readLine())!=null) {
            //System.out.println(read);

            list.add(read);
        }
        MD5Tools md5=new MD5Tools();


        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < list.size(); i++) {
            try {
               /* String s = list.get(i);
                String time = MD5Tools.ees3DecodeECB(s);*/
                //System.out.println(time);
                //String day = MD5Tools.ees3DecodeECB(list.get(1));
               // String StartTime = MD5Tools.ees3DecodeECB(list.get(0));
                String msg = MD5Tools.ees3DecodeECB(list.get(0));
               // System.out.println("msg:"+msg);
                String[] split = msg.split("#");
                String day =split[1];
                String StartTime =split[0];

                //long now = System.currentTimeMillis()/1000;
                String format = sdf.format(new Date());
                long now =sdf.parse(format).getTime()/1000;
                long Starttime = sdf.parse(StartTime).getTime()/1000;

               // System.out.println((now-Starttime));
                //System.out.println(Integer.parseInt(day)*DAY_TIME);
                if ((now-Starttime)>(Integer.parseInt(day)*DAY_TIME)||((now-Starttime)<0)){
                    //超时
                    System.out.println("密钥过时...");
                    //退出
                    List<String> secretList = SystemMap.getSecretList();
                    if (secretList==null||secretList.size()==0){
                        SystemMap.getSecretList().add("user_secret");
                    }


                }else {
                    SystemMap.getSecretList().clear();
                }


            } catch (Exception e) {

                e.printStackTrace();
                //解密异常,退出
                System.out.println("密钥解析异常...");
                List<String> secretList = SystemMap.getSecretList();
                if (secretList==null||secretList.size()==0){
                    SystemMap.getSecretList().add("user_secret");
                }
            }
        }
    }

}
