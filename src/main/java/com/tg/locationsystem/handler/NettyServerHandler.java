package com.tg.locationsystem.handler;

import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.pojo.AlertVO;
import com.tg.locationsystem.pojo.DateJsonValueProcessor;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.StringUtils;
import com.tg.locationsystem.utils.SystemMap;
import com.tg.locationsystem.utils.filewriteutil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hyy
 * @ Date2019/7/22
 */
@Component
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private ITagService tagService;
    @Autowired
    private ITagStatusService tagStatusService;
    @Autowired
    private IFrenceHistoryService frenceHistoryService;
    @Autowired
    private IHeartRateHistoryService heartRateHistoryService;
    @Autowired
    private IHeartRateSetService heartRateSetService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private IGoodsService goodsService;

    public static NettyServerHandler nettyServerHandler;
  DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    DateFormat sdf2 =
            new SimpleDateFormat("yyyy-MM-dd");

   //最远距离
    public static final double MIN_DISTANCE=0.5;
    //最近距离
    public static final double MAX_DISTANCE=3;
    //最大心率
    public static final int MAX_HEART_RATE=100;
    //最小心率
    public static final int MIN_HEART_RATE=30;
    //最大计数
    public static final int MAX_COUNT=50;
    //报警时间
    public static final int ALERT_TIME=3;
    public static final String sos="_sos";
    public static final String heart="_heatr";
    public static final String cut="_cut";
    //报警标识
    String alertData="";
    //存储多余数据
    public  static String  suparData="";
    //public static  int i=0;
    //Set<String> set=new TreeSet<>();

    @PostConstruct
    public void init(){
        nettyServerHandler=this;
        nettyServerHandler.tagService=this.tagService;
        nettyServerHandler.tagStatusService=this.tagStatusService;
        nettyServerHandler.frenceHistoryService=this.frenceHistoryService;
        nettyServerHandler.heartRateHistoryService=this.heartRateHistoryService;
        nettyServerHandler.heartRateSetService=this.heartRateSetService;
        nettyServerHandler.personService=this.personService;
        nettyServerHandler.goodsService=this.goodsService;

    }

    public static Tag getTagByOnlyAddress(String aAddress){
        return nettyServerHandler.tagService.getTagByOnlyAddress(aAddress);
    }
    public static Tag getUseTagByAddress(String aAddress){
        return nettyServerHandler.tagService.getUseTagByAddress(aAddress);
    }
    public static int updateTag(Tag tag){
        return nettyServerHandler.tagService.updateByPrimaryKeySelective(tag);
    }
    public static int inserttagStatus(TagStatus tagStatus){
        return nettyServerHandler.tagStatusService.insertSelective(tagStatus);
    }
    public static int insertFrenceHistory(FrenceHistory frenceHistory){
        return nettyServerHandler.frenceHistoryService.insertSelective(frenceHistory);
    }

    public static int insertheartRateHistory(HeartRateHistory heartRateHistory){
        return nettyServerHandler.heartRateHistoryService.insertSelective(heartRateHistory);
    }
    public static HeartRateSet getHeartRateSet(Integer userid){
        return nettyServerHandler.heartRateSetService.getHeartRateSet(userid);
    }
    public static Person getPersonByAddress(Integer id,String tagAdd){
        return nettyServerHandler.personService.getPersonByAddress(id,tagAdd);
    }
    public static Goods getGoodsByAddress(Integer id,String tagAdd){
        return nettyServerHandler.goodsService.getGoodsByAddress(id,tagAdd);
    }
    private List<TagStatus> getTagStatusByAddress(String tagAdd) {
        return nettyServerHandler.tagStatusService.getTagStatusByAddress(tagAdd);
    }

    //收到数据时调用
     @Override
    public  void channelRead(ChannelHandlerContext ctx, Object  msg) throws Exception {
        try {
            // System.out.println("22ctx: "+ctx);
            //System.out.println(GatewayService.getChannels());
            ByteBuf in = (ByteBuf) msg;
            int readableBytes = in.readableBytes();
            byte[] bytes = new byte[readableBytes];
            in.readBytes(bytes);
            //i=i+bytes.length;
            //System.out.println(i);
            //将Byte[]字节数组转为十六进制。
            String string = StringUtils.ByteArrToHex(bytes);
            String string1= StringUtils.ByteArrToHex(bytes);
           //System.out.println("收到数据:"+string);

            string=string.replace(" ","");
            string=suparData+string;

            suparData="";
            String data1="";

           /* String property = System.getProperty("user.dir");
            String[] pathsplit = property.split(":");
            String dirpath=pathsplit[0]+":"+"\\log\\data";
            boolean dir = filewriteutil.createDir(dirpath);*/



            //将原始数据存到文件中
            //filewriteutil.filewrite(simpleDateFormat.format(new Date())+":"+string,dirpath+"\\"+sdf2.format(new Date())+"_originalData.txt");

         //Set<String> sendSet=new HashSet<>();
        // Map<String,Integer> sendMap=new HashMap<>();
         //List<TagStatus> tagStatusList=new ArrayList<>();
            //String format = sdf.format(new Date());
//循环查找string
            while (string.length()>0){
                //判断数据格式
               /* if (!string.startsWith("65")||string.length()<146){
                    //System.out.println("数据有误,开头或长度有误:"+string);
                    return;
                }*/
                //位置信息
                if (string.startsWith("65")){
                    if (string.length()<146){
                        suparData=string;
                        //System.out.println(string);
                        // System.out.println("数据长度有误:"+string);
                        //错误数据存到文件中
                        //filewriteutil.filewrite(simpleDateFormat.format(new Date())+":"+string,dirpath+"\\"+sdf2.format(new Date())+"_error.txt");
                        return;
                    }
                    data1=string.substring(0,146);
                    //标签address
                    String tagAdd=data1.substring(50,66);

                    //原始标签mac存到文件
                    //filewriteutil.filewrite(simpleDateFormat.format(new Date())+":"+tagAdd,dirpath+"\\"+sdf2.format(new Date())+"_originalTag.txt");

                    //该标签在缓存无记录
                    Map<String, Integer> usermap = SystemMap.getUsermap();
                    if (!usermap.containsKey(tagAdd)){
                        //System.out.println("该标签在数据库无记录或未被使用...");
                        //filewriteutil.filewrite(simpleDateFormat.format(new Date())+":"+tagAdd,dirpath+"\\"+sdf2.format(new Date())+"_errorlTag.txt");
                        //没有记录的标签存到文件
                        return;
                    }


                    //  System.out.println(string);
                  /* Tag tag = getUseTagByAddress(tagAdd);
                   //该标签在数据库没有有记录,或未被绑定
                   if (tag==null){
                       //System.out.println("该标签在数据库无记录或未被使用...");
                       return;
                   }*/

                    //x坐标3DE99F64EBFE3040

                    String Xstring=data1.substring(2,18);
                    byte[] Xbytes = StringUtils.HexToByteArr(Xstring);
                    double vx = StringUtils.bytes2Double(Xbytes)+5.3;
                    double x = StringUtils.round(vx);

                    //y坐标
                    String Ystring=data1.substring(18,34);
                    byte[] Ybytes = StringUtils.HexToByteArr(Ystring);
                    double vy = 15.5-StringUtils.bytes2Double(Ybytes);
                    double y = StringUtils.round(vy);

                    //z坐标
                    String Zmsg=data1.substring(34,50);
                    //System.out.println(Zmsg);
                    byte[] Zbytes = StringUtils.HexToByteArr(Zmsg);
                    double vz = StringUtils.bytes2Double(Zbytes);
                    double z = StringUtils.round(vz);

                    //filewriteutil.filewrite(simpleDateFormat.format(new Date())+":"+tagAdd+":"+x+":"+y,dirpath+"\\"+sdf2.format(new Date())+"_aa.txt");

                    // System.out.println("当前xy:"+x+"--->"+y);
                    // System.out.println("缓存xy:"+SystemMap.getMap().get(tagAdd));

                    double[] p={x*23.5,y*23.5};
                    //该标签所属用户
                    Integer userid = SystemMap.getUsermap().get(tagAdd);
                    //该用户所属集合
                    List<Frence> frenceList = SystemMap.getFrencemap().get(userid);
                    // System.out.println("该用户所属集合:"+frenceList.size()+"---->"+frenceList);
                    if (frenceList!=null) {
                        if (frenceList.size() > 0) {
                            for (Frence frence : frenceList) {
                                String data = frence.getData();
                                //1.1,1.1 2.2,2.2 3.3,3.3;
                                List<double[]> poly = StringUtils.setData(data);
                                String s = StringUtils.rayCasting(p, poly);
                                //System.out.println("点与围栏的关系:"+s);
                                //System.out.println("数据库围栏的触发条件:"+frence.getType());
                                if (s.equals(frence.getType())) {
                                    FrenceHistory frenceHistory = new FrenceHistory();
                                    //frenceHistory.setTagAddress(tagAdd);
                                    frenceHistory.setX(x);
                                    frenceHistory.setY(y);
                                    frenceHistory.setStatus("0");
                                    frenceHistory.setTime(new Date());
                                    frenceHistory.setUserId(userid);
                                    frenceHistory.setFrenceId(frence.getId());

                                    //websocket通知前端有警告
                            /*   String tagStatusjson = net.sf.json.JSONObject.fromObject(frenceHistory).toString();
                               Channel channel = SystemMap.getChannelmap().get(userid);
                               TextWebSocketFrame tws = new TextWebSocketFrame(tagStatusjson);
                               channel.writeAndFlush(tws);*/
                                    //插入一条围栏记录
                                    insertFrenceHistory(frenceHistory);
                                }
                            }
                        }
                    }
                /*   Map<String, String> map = SystemMap.getMap();
                   String date = sdf.format(new Date());
                   StringBuffer sb=new StringBuffer();
                   sb.append(x);
                   sb.append(",");
                   sb.append(y);
                   sb.append(",");
                   sb.append(z);
                   sb.append(",");
                   sb.append(date);

                   map.put(tagAdd,sb.toString());*/
                    //该标签在数据库有有记录,且被绑定---刷新缓存的位置
                    Map<String, String> map = SystemMap.getMap();
                    if (map.get(tagAdd)==null||"".equals(map.get(tagAdd))){
                        String date = sdf.format(new Date());
                        StringBuffer sb=new StringBuffer();
                        sb.append(x);
                        sb.append(",");
                        sb.append(y);
                        sb.append(",");
                        sb.append(z);
                        sb.append(",");
                        sb.append(date);

                        map.put(tagAdd,sb.toString());
                    }else {
                        String data = map.get(tagAdd);
                        String[] split = data.split(",");
                        double x1 = (x - Double.parseDouble(split[0])) * (x - Double.parseDouble(split[0]));
                        double y1 = (y - Double.parseDouble(split[1])) * (y -Double.parseDouble(split[1]));
                        double z1 = (z - Double.parseDouble(split[2])) * (z - Double.parseDouble(split[2]));

                        double sqrt = Math.sqrt(x1 + y1 + z1);
                        if (sqrt<=MIN_DISTANCE||sqrt>=MAX_DISTANCE){
                            //  System.out.println("数据太小或太大,不做处理,只更新时间");
                            Map<String, Integer> countmap = SystemMap.getCountmap();
                            Integer count = countmap.get(tagAdd);
                            if (count<MAX_COUNT){
                                //用原本缓存的位置位置
                                String date = sdf.format(new Date());
                                StringBuffer sb=new StringBuffer();
                                sb.append(split[0]);
                                sb.append(",");
                                sb.append(split[1]);
                                sb.append(",");
                                sb.append(split[2]);
                                sb.append(",");
                                sb.append(date);

                                map.put(tagAdd,sb.toString());
                                count++;
                                countmap.put(tagAdd,count);
                            }else {
                                //次数超过50,代替缓存位置
                                // System.out.println("更新位置...");

                                StringBuffer sb=new StringBuffer();
                                sb.append(x);
                                sb.append(",");
                                sb.append(y);
                                sb.append(",");
                                sb.append(z);
                                sb.append(",");
                                String date = sdf.format(new Date());
                                sb.append(date);

                                map.put(tagAdd,sb.toString());
                                //刷新次数
                                countmap.put(tagAdd,0);

                            }

                        }else {
                            //刷新位置
                            String date = sdf.format(new Date());
                            StringBuffer sb=new StringBuffer();
                            sb.append(x);
                            sb.append(",");
                            sb.append(y);
                            sb.append(",");
                            sb.append(z);
                            sb.append(",");
                            sb.append(date);
                            map.put(tagAdd,sb.toString());
                        }
                    }
                    //截取string
                    string=string.substring(146);

                    //标签状态
                }else if (string.startsWith("66")){

                    //i++;
                    // System.out.println(i);
                    if (string.length()<50){
                        //  System.out.println("数据长度有误:"+string);
                        suparData=string;
                        return;
                    }
                    String data=string.substring(0,50);
                    //System.out.println("触发66消息"+data);

                    //标签address
                    String tagAdd=data.substring(2,18);
                    Map<String, Integer> usermap = SystemMap.getUsermap();
                    if (!usermap.containsKey(tagAdd)){
                        // System.out.println("该标签在数据库无记录或未被使用...");
                        return;
                    }
                  /* if (!"A34D81CF0001CADE".equals(tagAdd)){
                       return;
                   }*/
                    // exist=string.substring(0,50);
                 /*  if (set.contains(data)){
                       return;
                   }else {
                       set.add(data);
                   }*/
                    /*//根据标签address得到标签*/
                 /*  Tag tag = getTagByOnlyAddress(tagAdd);
                  if (tag==null){
                       System.out.println("该标签在数据库无记录..");
                       return;
                   }*/

                    //sos警报
                    String sos_alert = data.substring(18, 20);
                    if ("01".equals(sos_alert)) {
                        //System.out.println("SOS警报");
                        TagStatus tagStatus = new TagStatus();
                        String peridcard = SystemMap.getKey(SystemMap.getTagAndPersonMap(), tagAdd);
                        tagStatus.setPersonIdcard(peridcard);
                        tagStatus.setAddTime(new Date());
                        tagStatus.setAlertType("1");
                        tagStatus.setData("1");
                        tagStatus.setIsdeal("0");

                        if (usermap.get(tagAdd) != null) {
                            tagStatus.setUserId(usermap.get(tagAdd));
                        }
                        //插入数据库
                        inserttagStatus(tagStatus);
                    /*   List<TagStatus> tagStatusList=getTagStatusByAddress(tagAdd);
                       if (tagStatusList.size()==0){
                           //插入数据库
                           inserttagStatus(tagStatus);
                       }else {
                           TagStatus tagStatus1 = tagStatusList.get(tagStatusList.size() - 1);
                           if (new Date().getTime()/1000-tagStatus1.getAddTime().getTime()/1000>ALERT_TIME){
                               //插入数据库
                               inserttagStatus(tagStatus);
                           }
                       }*/


                        //websocket通知前端有警告
                        Integer userid = SystemMap.getUsermap().get(tagAdd);
                        AlertVO alertVO = new AlertVO();
                        alertVO.setId(tagStatus.getId());
                        alertVO.setTagAddress(tagAdd);
                        alertVO.setData(String.valueOf("1"));
                        alertVO.setAlertType("1");
                        alertVO.setIsdeal("0");


                        Tag tag = getTagByOnlyAddress(tagAdd);
                        if (tag.getX() != null) {
                            alertVO.setX(tag.getX());
                        }
                        if (tag.getY() != null) {
                            alertVO.setY(tag.getY());
                        }
                        Person person = getPersonByAddress(userid, tagAdd);
                        if (person != null) {
                            alertVO.setType("person");
                            alertVO.setIdCard(person.getIdCard());
                            alertVO.setName(person.getPersonName());
                        }
                        Goods goods = getGoodsByAddress(userid, tagAdd);
                        if (goods != null) {
                            alertVO.setType("goods");
                            alertVO.setIdCard(goods.getGoodsIdcard());
                            alertVO.setName(goods.getGoodsName());
                        }

                        alertVO.setAddTime(sdf.format(new Date()));
                        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO);
					/*JsonConfig jsonConfig = new JsonConfig();
					jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
					net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO, jsonConfig);*/

                        //报警标识
                        alertData=tagAdd+sos;
                        Map<String, String> alertmap = SystemMap.getAlertmap();
                        String time = alertmap.get(alertData);
                        //最新时间
                        String format = sdf.format(new Date());
                        if (time==null||"".equals(time)){
                            alertmap.put(alertData,format);
                            // System.out.println("sos1");
                            send(userid, jsonObject.toString());
                        }else {
                            if (System.currentTimeMillis()/1000-sdf.parse(time).getTime()/1000>ALERT_TIME){
                                alertmap.put(alertData,format);
                                // System.out.println("sos2");
                                send(userid, jsonObject.toString());
                            }
                        }
                        //System.out.println("sos");
                        //sendMap.put(jsonObject.toString(),userid);


                    }
                    //电池电量百分比
                    String bat_percent = data.substring(20,22);
                    if (usermap.get(tagAdd)!=null){
                        Tag tag = getTagByOnlyAddress(tagAdd);
                        tag.setElectric(String.valueOf(StringUtils.HexToByte(bat_percent)));
                        //跟新数据库
                        updateTag(tag);
                    }
                    if (!data.substring(22,26).contains("FF")) {

                        //心率数
                        String heart_rateA = data.substring(22, 24);
                        String heart_rateB = data.substring(24, 26);
                        int heart_rate = 0xff & StringUtils.HexToByte(heart_rateA) + (0xff & StringUtils.HexToByte(heart_rateB)) * 256;
                        if (heart_rate != 255&&heart_rate!=0) {
                            //插入心率历史记录表
                            HeartRateHistory heartRateHistory = new HeartRateHistory();
                            heartRateHistory.setAddTime(new Date());
                            //heartRateHistory.setTagAddress(tagAdd);
                            heartRateHistory.setTagData(heart_rate);
                            //userid
                            heartRateHistory.setUserId(usermap.get(tagAdd));
                            //System.out.println(tagAdd+"心率上报="+heart_rate);
                            //System.out.println(heartRateHistory.toString());
                            insertheartRateHistory(heartRateHistory);

                            HeartRateSet heartRateSet = getHeartRateSet(usermap.get(tagAdd));
                            if (heartRateSet != null) {
                                if (heartRateSet.getMinData() == null) {
                                    heartRateSet.setMinData(MIN_HEART_RATE);
                                }
                                if (heartRateSet.getMaxData() == null) {
                                    heartRateSet.setMaxData(MAX_HEART_RATE);
                                }
                            } else {
                                heartRateSet = new HeartRateSet();
                                heartRateSet.setMinData(MIN_HEART_RATE);
                                heartRateSet.setMaxData(MAX_HEART_RATE);
                            }
                            if (heart_rate > heartRateSet.getMaxData() || heart_rate < heartRateSet.getMinData()) {
                                TagStatus tagStatus = new TagStatus();
                                String peridcard = SystemMap.getKey(SystemMap.getTagAndPersonMap(), tagAdd);
                                tagStatus.setPersonIdcard(peridcard);
                                tagStatus.setAddTime(new Date());
                                tagStatus.setAlertType("2");
                                tagStatus.setData(String.valueOf(heart_rate));
                                tagStatus.setIsdeal("0");

                                if (usermap.get(tagAdd) != null) {
                                    tagStatus.setUserId(usermap.get(tagAdd));
                                }
                                List<TagStatus> tagStatusList = getTagStatusByAddress(tagAdd);
                                if (tagStatusList.size() == 0) {
                                    //插入数据库
                                    inserttagStatus(tagStatus);
                                } else {
                                    TagStatus tagStatus1 = tagStatusList.get(tagStatusList.size() - 1);
                                    if (new Date().getTime() / 1000 - tagStatus1.getAddTime().getTime() / 1000 > ALERT_TIME) {
                                        //插入数据库
                                        inserttagStatus(tagStatus);
                                    }
                                }

                                //websocket通知前端有警告
                                Integer userid = SystemMap.getUsermap().get(tagAdd);
                                AlertVO alertVO = new AlertVO();
                                //alertVO.setId(tagStatus.getId());
                                alertVO.setTagAddress(tagAdd);
                                alertVO.setData(String.valueOf(heart_rate));
                                alertVO.setAlertType("2");

                                alertVO.setIsdeal("0");
                                Tag tag = getTagByOnlyAddress(tagAdd);
                                if (tag.getX() != null) {
                                    alertVO.setX(tag.getX());
                                }
                                if (tag.getY() != null) {
                                    alertVO.setY(tag.getY());
                                }
                                Person person = getPersonByAddress(userid, tagAdd);
                                if (person != null) {
                                    alertVO.setType("person");
                                    alertVO.setIdCard(person.getIdCard());
                                    alertVO.setName(person.getPersonName());
                                }
                                Goods goods = getGoodsByAddress(userid, tagAdd);
                                if (goods != null) {
                                    alertVO.setType("goods");
                                    alertVO.setIdCard(goods.getGoodsIdcard());
                                    alertVO.setName(goods.getGoodsName());
                                }
                                alertVO.setAddTime(sdf.format(new Date()));
                                net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO);
					/*JsonConfig jsonConfig = new JsonConfig();
					jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
					net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO, jsonConfig);*/

                                //报警标识
                                alertData=tagAdd+heart;
                                Map<String, String> alertmap = SystemMap.getAlertmap();
                                String time = alertmap.get(alertData);
                                //最新时间
                                String format = sdf.format(new Date());
                                if (time==null||"".equals(time)){
                                    alertmap.put(alertData,format);
                                    send(userid, jsonObject.toString());
                                }else {
                                    if (System.currentTimeMillis()/1000-sdf.parse(time).getTime()/1000>ALERT_TIME){
                                        alertmap.put(alertData,format);
                                        send(userid, jsonObject.toString());
                                    }
                                }
                            }
                        }
                    }
                    //步数
                    //  String step_num=string.substring(26,34);
                    //剪断警报
                    String cut_alert=data.substring(34,36);
                    if ("01".equals(cut_alert)) {
                        // System.out.println("剪断警报"+string);
                        TagStatus tagStatus = new TagStatus();
                        String peridcard = SystemMap.getKey(SystemMap.getTagAndPersonMap(), tagAdd);
                        tagStatus.setPersonIdcard(peridcard);
                        tagStatus.setAddTime(new Date());
                        tagStatus.setAlertType("3");
                        tagStatus.setData("1");
                        tagStatus.setIsdeal("0");

                        if (usermap.get(tagAdd) != null) {
                            tagStatus.setUserId(usermap.get(tagAdd));
                        }
                        List<TagStatus> tagStatusList = getTagStatusByAddress(tagAdd);
                        if (tagStatusList.size() == 0) {
                            //插入数据库
                            inserttagStatus(tagStatus);
                        } else {
                            TagStatus tagStatus1 = tagStatusList.get(tagStatusList.size() - 1);
                            if (new Date().getTime() / 1000 - tagStatus1.getAddTime().getTime() / 1000 > ALERT_TIME) {
                                //插入数据库
                                inserttagStatus(tagStatus);
                            }
                        }

                        //websocket通知前端有警告
                        Integer userid = SystemMap.getUsermap().get(tagAdd);
                        AlertVO alertVO = new AlertVO();
                        //alertVO.setId(tagStatus.getId());
                        alertVO.setTagAddress(tagAdd);
                        alertVO.setData(String.valueOf("1"));
                        alertVO.setAlertType("3");

                        alertVO.setIsdeal("0");
                        Tag tag = getTagByOnlyAddress(tagAdd);
                        if (tag.getX() != null) {
                            alertVO.setX(tag.getX());
                        }
                        if (tag.getY() != null) {
                            alertVO.setY(tag.getY());
                        }
                        Person person = getPersonByAddress(userid, tagAdd);
                        if (person != null) {
                            alertVO.setType("person");
                            alertVO.setIdCard(person.getIdCard());
                            alertVO.setName(person.getPersonName());
                        }
                        Goods goods = getGoodsByAddress(userid, tagAdd);
                        if (goods != null) {
                            alertVO.setType("goods");
                            alertVO.setIdCard(goods.getGoodsIdcard());
                            alertVO.setName(goods.getGoodsName());
                        }
                        alertVO.setAddTime(sdf.format(new Date()));
                        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO);
					/*JsonConfig jsonConfig = new JsonConfig();
					jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
					net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO, jsonConfig);*/

                        //报警标识
                        alertData=tagAdd+heart;
                        Map<String, String> alertmap = SystemMap.getAlertmap();
                        String time = alertmap.get(alertData);
                        //最新时间
                        String format = sdf.format(new Date());
                        if (time==null||"".equals(time)){
                            alertmap.put(alertData,format);
                            send(userid, jsonObject.toString());
                        }else {
                            if (System.currentTimeMillis()/1000-sdf.parse(time).getTime()/1000>ALERT_TIME){
                                alertmap.put(alertData,format);
                                send(userid, jsonObject.toString());
                            }
                        }

                    }
                    string=string.substring(50);

                }else if (string.startsWith("63")){
                    if (string.length()<50){
                        //  System.out.println("数据长度有误:"+string);
                        suparData=string;
                        return;
                    }
                    string=string.substring(50);
                }else {
                    string=string.substring(2);
                    //return;
                  /* System.out.println("准备结束.."+string);
                   int s=0;
                   byte[] ss=StringUtils.HexToByteArr(string);
                   for(byte b:ss){
                       if(b==(byte)0x65||b==(byte)0x66||b==(byte)0x63){
                           string=StringUtils.ByteArrToHex(bytes,s,ss.length-s);
                           System.out.println("准备结束.."+string);
                           break;
                       }
                       else if(s==ss.length-1){
                           return;
                       }
                       s++;
                   }*/
                    // System.out.println("原始数据.."+string1);

                }
            }


           // exist="";
            //set.clear();
        /*    if (sendMap.size()>0){
                for (String key : sendMap.keySet()) {
                    System.out.println("警报key:"+key);
                    send(sendMap.get(key),key);
                }
                sendMap.clear();
            }*/
            } catch (Exception e) {
            System.out.println("异常"+e.getMessage());
            e.printStackTrace();
           /* if(e instanceof InvocationTargetException &&e.getMessage()==null){
               String msg1= ((InvocationTargetException) e).getTargetException().getMessage();
               //System.out.println("异常警报="+msg1);
            }*/
            //e.printStackTrace();
        } finally {
            // 抛弃收到的数据
            ReferenceCountUtil.release(msg);
        }
    }

    //处理接收到的数据,数据有可能是多个包
    private String[] dealstring(String string) {
      return  string.split("固定结束符");
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
    /*
     * 建立连接时，返回消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
        System.out.println("连接的客户端ID:" + ctx.channel().id());
        ctx.writeAndFlush("client"+ InetAddress.getLocalHost().getHostName() + "success connected！ \n");
        System.out.println("connection");
        //StaticVar.ctxList.add(ctx);
        //StaticVar.chc = ctx;

        super.channelActive(ctx);
    }
    /*
     * 连接断开(主动)
     *
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("断开的客户端地址:" + ctx.channel().remoteAddress());
        System.out.println("断开:" + ctx.channel().id());
        ctx.writeAndFlush("client"+ InetAddress.getLocalHost().getHostName() + "success connected！ \n");
        System.out.println("NOconnection");
        ctx.fireChannelInactive();
    }

    private void send(int id,String data){
                    //System.out.println("触发推送信息="+data);
        for (String key : SystemMap.getChannelmap().keySet()) {
            if(key!=null&&!key.equals("")&&key.contains(",")&key.split(",")[1].equals(id+"")){
                Channel channel = SystemMap.getChannelmap().get(key);
                TextWebSocketFrame tws = new TextWebSocketFrame(data);
                channel.writeAndFlush(tws);
            }
        }

    }

        }































