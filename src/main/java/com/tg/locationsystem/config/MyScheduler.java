package com.tg.locationsystem.config;

import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.SystemMap;
import com.tg.locationsystem.utils.filewriteutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
    public static final int UP_TIME=15;
    private  SimpleDateFormat dateFormat =
            new SimpleDateFormat("YYYYMMdd");
    DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
}
