package com.tg.locationsystem;

import bean.Station_status;
import bean.Tag_info;
import bean.Tag_status;
import boot.SkylabSDK;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.handler.NettyServerHandler;
import com.tg.locationsystem.pojo.AlertVO;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.StringUtils;
import com.tg.locationsystem.utils.SystemMap;
import interfacer.NetWorkCallback;
import interfacer.UWBCallback;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ResourceLeakDetector;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.*;

@SpringBootApplication
@MapperScan("com.tg.locationsystem.mapper")
@EnableScheduling
//@ServletComponentScan("com.tg.locationsystem")
public class LocationsystemApplication  {
	private static ITagService tagService;//Service层类
	private static IFrenceService frenceService;//Service层类
	private static IHeartRateSetService heartRateSetService;//Service层类
	private static IEleCallSetService eleCallSetService;//Service层类
	private static IEleCallService eleCallService;//Service层类
	private static IStatisticsCallService statisticsCallService;//Service层类
	private static IStationService stationService ;//Service层类
	private static IMapService mapService  ;//Service层类
	private static IFrenceHistoryService frenceHistoryService  ;//Service层类
	private static ITagStatusService tagStatusService  ;//Service层类
	private static IPersonService personService  ;//Service层类
	private static IGoodsService goodsService  ;//Service层类
	private static IMapRuleService mapRuleService  ;//Service层类
	private static IHeartRateHistoryService heartRateHistoryService  ;//Service层类
	private static IAlertSetService alertSetService  ;//Service层类

	static DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
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

	//最低电量
	public static final int MAX_BATTERY=10;

	//边界限定
    //public static final double boundary_MaxX=30.91;
    public static final double boundary_MaxX=25;
    public static final double boundary_MinX=0;
    public static final double boundary_MaxY=18;
    public static final double boundary_MinY=0;
	public static final String SOS="_sos";
	public static final String HEART="_heatr";
	public static final String CUT="_cut";
	public static final String BATTERY="_battery";
	public static final String FRENCE="_frence";

	public static  SkylabSDK skylabSDK=new SkylabSDK(3600,3601);

	@Autowired
	public void setDatastore(ITagService tagService, IFrenceService frenceService,
							 IHeartRateSetService  heartRateSetService,IEleCallSetService eleCallSetService,
							 IEleCallService eleCallService,IStatisticsCallService statisticsCallService,
							 IStationService stationService,IMapService mapService, IFrenceHistoryService frenceHistoryService,
							 ITagStatusService tagStatusService,IPersonService personService,IGoodsService goodsService,
							 IHeartRateHistoryService heartRateHistoryService,IMapRuleService mapRuleService,
							 IAlertSetService alertSetService ) {
		//TagbindingApplication
		LocationsystemApplication.tagService = tagService;
		LocationsystemApplication.frenceService = frenceService;
		LocationsystemApplication.heartRateSetService = heartRateSetService;
		LocationsystemApplication.eleCallSetService = eleCallSetService;
		LocationsystemApplication.eleCallService = eleCallService;
		LocationsystemApplication.statisticsCallService = statisticsCallService;
		LocationsystemApplication.stationService = stationService;
		LocationsystemApplication.mapService = mapService;
		LocationsystemApplication.frenceHistoryService = frenceHistoryService;
		LocationsystemApplication.tagStatusService = tagStatusService;
		LocationsystemApplication.personService = personService;
		LocationsystemApplication.goodsService = goodsService;
		LocationsystemApplication.heartRateHistoryService = heartRateHistoryService;
		LocationsystemApplication.mapRuleService = mapRuleService;
		LocationsystemApplication.alertSetService = alertSetService;
	}
	public  static Tag tag1=new Tag();
	public static void main(String[] args) throws Exception {
		SpringApplication.run(LocationsystemApplication.class, args);
		new LocationsystemApplication().cach();
		/*new LocationsystemApplication().run();
		new NettyServer().initNetty();*/
        SkylabSDK.debug=false;
		skylabSDK.setUwbDataCallback(new UWBCallback() {
			@Override
			public void onLocation(String key, bean.Tag tag) {
                //System.out.println("标签:"+tag.getAddres());
				//System.out.println("cle链路:"+key);
				//地图的key
				String mapKey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
				//System.out.println("地图uuid:"+mapKey);
				if (mapKey==null||"".equals(mapKey)){
					return;
				}
				//该标签在缓存无记录
				Map<String, Integer> usermap = SystemMap.getUsermap();
				if (!usermap.containsKey(tag.getAddres())){
					return;
				}
				//规则
			/*	String rule = SystemMap.getMapRuleMap().get(mapKey);
				if (!rule.isEmpty()){
					tag1.setAddress(tag.getAddres());
					tag1.setX(tag.getX());
					tag1.setY(tag.getY());
					Tag tagByRule = ThroughWall.getTagByRule(tag1, rule);
					tag.setX(tagByRule.getX());
					tag.setY(tagByRule.getY());
					tag.setAddres(tagByRule.getAddress());
				}*/


				double[] p={tag.getX(),tag.getY()};
				//该标签所属用户
				Integer userid = SystemMap.getUsermap().get(tag.getAddres());
				//该用户所属集合
				List<Frence> frenceList = SystemMap.getFrencemap().get(userid);
				if (frenceList!=null) {
					if (frenceList.size() > 0) {
						for (Frence frence : frenceList) {
							if (!mapKey.equals(frence.getMapKey())){
								continue;
							}
							String data = frence.getData();
							//1.1,1.1 2.2,2.2 3.3,3.3;
							List<double[]> poly = StringUtils.setData(data);
							String s = StringUtils.rayCasting(p, poly);
							//System.out.println("点与围栏的关系:"+s);
							//System.out.println("数据库围栏的触发条件:"+frence.getType());
							if (s.equals(frence.getType())) {
								String frenceTime = SystemMap.getFrenceTimemap().get(tag.getAddres());
								//第一次进来,不存在
								if (frenceTime==null||"".equals(frenceTime)){
									String time = sdf1.format(new Date());
									SystemMap.getFrenceTimemap().put(tag.getAddres(),time+","+time);
								}else {
									DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

									String[] frenceTimesplit = frenceTime.split(",");
									try {
										long nowTime = sdf1.parse(frenceTimesplit[1]).getTime() / 1000;
										long now = System.currentTimeMillis()/1000;
										//缓存的当前时间-系统当前时间
										//若超过3s,刷新缓存当前时间
										if ((nowTime-now)>3){
											String time = sdf1.format(new Date());
											SystemMap.getFrenceTimemap().put(tag.getAddres(),time+","+time);
										}else {
											String time = sdf1.format(new Date());
											frenceTimesplit[1]=time;
											SystemMap.getFrenceTimemap().put(tag.getAddres(),frenceTimesplit[0]+","+frenceTimesplit[1]);
											long firstTime = sdf1.parse(frenceTimesplit[0]).getTime() / 1000;
											//真的进入了围栏,触发了警报
											if ((firstTime-nowTime)>5){
												//报警标识
												String alertData=tag.getAddres()+FRENCE;
												Map<String, String> alertmap = SystemMap.getAlertmap();
												String alerttime = alertmap.get(alertData);
												//最新时间
												String format = sdf1.format(new Date());
												if (alerttime==null||"".equals(alerttime)){
													alertmap.put(alertData,format);
													FrenceHistory frenceHistory = new FrenceHistory();
													//frenceHistory.setTagAddress(tag.getAddres());
													String peridcard = SystemMap.getTagAndPersonMap().get(tag.getAddres());
													frenceHistory.setPersonIdcard(peridcard);
													frenceHistory.setX(tag.getX());
													frenceHistory.setY(tag.getY());
													frenceHistory.setStatus("0");
													frenceHistory.setTime(new Date());
													frenceHistory.setUserId(userid);
													frenceHistory.setFrenceId(frence.getId());
													frenceHistory.setMapKey(mapKey);
													//websocket通知前端有警告
                            /*   String tagStatusjson = net.sf.json.JSONObject.fromObject(frenceHistory).toString();
                               Channel channel = SystemMap.getChannelmap().get(userid);
                               TextWebSocketFrame tws = new TextWebSocketFrame(tagStatusjson);
                               channel.writeAndFlush(tws);*/
													//插入一条围栏记录
													frenceHistoryService.insertSelective(frenceHistory);
												}else {
													if (System.currentTimeMillis()/1000-sdf1.parse(alerttime).getTime()/1000>10) {
														alertmap.put(alertData,format);
														FrenceHistory frenceHistory = new FrenceHistory();
														//frenceHistory.setTagAddress(tag.getAddres());
														String peridcard = SystemMap.getTagAndPersonMap().get(tag.getAddres());
														frenceHistory.setPersonIdcard(peridcard);
														frenceHistory.setX(tag.getX());
														frenceHistory.setY(tag.getY());
														frenceHistory.setStatus("0");
														frenceHistory.setTime(new Date());
														frenceHistory.setUserId(userid);
														frenceHistory.setFrenceId(frence.getId());
														frenceHistory.setMapKey(mapKey);
														//websocket通知前端有警告
                            /*   String tagStatusjson = net.sf.json.JSONObject.fromObject(frenceHistory).toString();
                               Channel channel = SystemMap.getChannelmap().get(userid);
                               TextWebSocketFrame tws = new TextWebSocketFrame(tagStatusjson);
                               channel.writeAndFlush(tws);*/
														//插入一条围栏记录
														frenceHistoryService.insertSelective(frenceHistory);

													}
												}
											}
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}

								}

							}
						}
					}
				}
				//该标签在数据库有有记录,且被绑定---刷新缓存的位置
				Map<String, String> map = SystemMap.getMap();
				//DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (map.get(tag.getAddres())==null||"".equals(map.get(tag.getAddres()))){
					if (userid==1){
					    if (tag.getX()<boundary_MinX){
					        tag.setX(boundary_MinX);
                        }
                        if (tag.getX()>boundary_MaxX){
                            tag.setX(boundary_MaxX);
                        }
                        if (tag.getY()<boundary_MinY){
					        tag.setY(boundary_MinY);
                        }
                        if (tag.getY()>boundary_MaxY){
                            tag.setY(boundary_MaxY);
                        }
                    }

				    String date = sdf1.format(new Date());
					StringBuffer sb=new StringBuffer();
					sb.append(tag.getX());
					sb.append(",");
					sb.append(tag.getY());
					sb.append(",");
					sb.append(tag.getZ());
					sb.append(",");
					sb.append(date);
					//地图的key
                    String mapkey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
					//System.out.println("地图uuid:"+mapkey);
                    sb.append(",");
                    sb.append(mapkey);

					map.put(tag.getAddres(),sb.toString());
				}else {
                    if (userid==1){
                        if (tag.getX()<boundary_MinX){
                            tag.setX(boundary_MinX);
                        }
                        if (tag.getX()>boundary_MaxX){
                            tag.setX(boundary_MaxX);
                        }
                        if (tag.getY()<boundary_MinY){
                            tag.setY(boundary_MinY);
                        }
                        if (tag.getY()>boundary_MaxY){
                            tag.setY(boundary_MaxY);
                        }
                    }

					//刷新位置
						String date = sdf1.format(new Date());
					StringBuffer sb=new StringBuffer();
					sb.append(tag.getX());
					sb.append(",");
					sb.append(tag.getY());
					sb.append(",");
					sb.append(tag.getZ());
					sb.append(",");
					sb.append(date);
					//地图的key
					String mapkey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
					//System.out.println("地图uuid:"+mapkey);
					sb.append(",");
					sb.append(mapkey);
					map.put(tag.getAddres(),sb.toString());

					/*String data = map.get(tag.getAddres());
					String[] split = data.split(",");
					double x1 = (tag.getX() - Double.parseDouble(split[0])) * (tag.getX() - Double.parseDouble(split[0]));
					double y1 = (tag.getY() - Double.parseDouble(split[1])) * (tag.getY() -Double.parseDouble(split[1]));
					double z1 = (tag.getZ() - Double.parseDouble(split[2])) * (tag.getZ() - Double.parseDouble(split[2]));

					double sqrt = Math.sqrt(x1 + y1 + z1);

				if (sqrt<=MIN_DISTANCE||sqrt>=MAX_DISTANCE){
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
                        //地图的key
                        String mapkey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
                        //System.out.println("地图uuid:"+mapkey);
                        sb.append(",");
                        sb.append(mapkey);


					*//*	if (count<MAX_COUNT){
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
                            //地图的key
                            String mapkey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
							//System.out.println("地图uuid:"+mapkey);
                            sb.append(",");
                            sb.append(mapkey);

							map.put(tag.getAddres(),sb.toString());
							count++;
							countmap.put(tag.getAddres(),count);
						}else {
							//次数超过50,代替缓存位置
							// System.out.println("更新位置...");

							StringBuffer sb=new StringBuffer();
							sb.append(tag.getX());
							sb.append(",");
							sb.append(tag.getY());
							sb.append(",");
							sb.append(tag.getZ());
							sb.append(",");
							String date = sdf.format(new Date());
							sb.append(date);
                            //地图的key
                            String mapkey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
							//System.out.println("地图uuid:"+mapkey);
                            sb.append(",");
                            sb.append(mapkey);

							map.put(tag.getAddres(),sb.toString());
							//刷新次数
							countmap.put(tag.getAddres(),0);

						}*//*
					}else {
						//刷新位置
						String date = sdf.format(new Date());
						StringBuffer sb=new StringBuffer();
						sb.append(tag.getX());
						sb.append(",");
						sb.append(tag.getY());
						sb.append(",");
						sb.append(tag.getZ());
						sb.append(",");
						sb.append(date);
                        //地图的key
                        String mapkey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
						//System.out.println("地图uuid:"+mapkey);
                        sb.append(",");
                        sb.append(mapkey);
						map.put(tag.getAddres(),sb.toString());
					}*/


				}
				}


			@Override
			public void onTagStatud(String key, Tag_status tag_status) {

			}

			@Override
			public void onTagInfo(String key, Tag_info tag_info) {
				//   System.out.println(tag_info.toString());
				//标签address
				String tagAdd=tag_info.getAddress();
				Map<String, Integer> usermap = SystemMap.getUsermap();
				if (!usermap.containsKey(tagAdd)){
					// System.out.println("该标签在数据库无记录或未被使用...");
					return;
				}
				//得到地图的key
                String mapkey = SystemMap.getKey(SystemMap.getCleAndKeyMap(), key);
                //地图的key
                String mapKey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
                //System.out.println("地图uuid:"+mapKey);
                if (mapKey==null||"".equals(mapKey)){
                    return;
                }
				String peridcard ="";

                //sos报警
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//如果该标签的用户开启了sos开关

					if (tag_info.getSos()==1){
						//websocket通知前端有警告
						Integer userid = SystemMap.getUsermap().get(tagAdd);
						AlertVO alertVO = new AlertVO();
						//alertVO.setId(tagStatus.getId());
						alertVO.setTagAddress(tagAdd);
						alertVO.setData(String.valueOf("1"));
						alertVO.setAlertType("1");
						alertVO.setIsdeal("0");
						alertVO.setMapKey(mapKey);

						Tag tag = tagService.getTagByOnlyAddress(tagAdd);
						if (tag.getX() != null) {
							alertVO.setX(tag.getX());
						}
						if (tag.getY() != null) {
							alertVO.setY(tag.getY());
						}
						Person person = personService.getPersonByAddress(userid, tagAdd);
						if (person != null) {
							alertVO.setType("person");
							alertVO.setIdCard(person.getIdCard());
							alertVO.setName(person.getPersonName());
						}
						Goods goods = goodsService.getGoodsByAddress(userid, tagAdd);
						if (goods != null) {
							alertVO.setType("goods");
							alertVO.setIdCard(goods.getGoodsIdcard());
							alertVO.setName(goods.getGoodsName());
						}
						alertVO.setAddTime(sdf.format(new Date()));
						Gson gson=new Gson();

						//net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO);
					/*JsonConfig jsonConfig = new JsonConfig();
					jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
					net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO, jsonConfig);*/

						//报警标识
						String alertData=tagAdd+SOS;
						Map<String, String> alertmap = SystemMap.getAlertmap();
						String time = alertmap.get(alertData);
						//最新时间
						String format = sdf.format(new Date());
						if (time==null||"".equals(time)){
							alertmap.put(alertData,format);
							TagStatus tagStatus = new TagStatus();
							peridcard = SystemMap.getTagAndPersonMap().get(tagAdd);
							tagStatus.setPersonIdcard(peridcard);
							tagStatus.setAddTime(new Date());
							tagStatus.setAlertType("1");
							tagStatus.setData("1");
							tagStatus.setIsdeal("0");
							tagStatus.setMapKey(mapkey);
							tagStatus.setX(tag.getX());
							tagStatus.setY(tag.getY());
							tagStatus.setZ(tag.getZ());

							if (usermap.get(tagAdd) != null) {
								tagStatus.setUserId(usermap.get(tagAdd));
							}
							//插入数据库
							tagStatusService.insertSelective(tagStatus);

							alertVO.setId(tagStatus.getId());
							String jsonObject = gson.toJson(alertVO);

							if (SystemMap.getSosList().contains(SystemMap.getUsermap().get(tagAdd))){
								//推送
								send(userid, jsonObject.toString());
							}



						}else {
							try {
								if (System.currentTimeMillis()/1000-sdf.parse(time).getTime()/1000>ALERT_TIME){
									alertmap.put(alertData,format);
									TagStatus tagStatus = new TagStatus();
									peridcard = SystemMap.getTagAndPersonMap().get(tagAdd);
									tagStatus.setPersonIdcard(peridcard);
									tagStatus.setAddTime(new Date());
									tagStatus.setAlertType("1");
									tagStatus.setData("1");
									tagStatus.setIsdeal("0");
									tagStatus.setMapKey(mapkey);
									tagStatus.setX(tag.getX());
									tagStatus.setY(tag.getY());
									tagStatus.setZ(tag.getZ());

									if (usermap.get(tagAdd) != null) {
										tagStatus.setUserId(usermap.get(tagAdd));
									}
									//插入数据库
									tagStatusService.insertSelective(tagStatus);
									alertVO.setId(tagStatus.getId());
									String jsonObject = gson.toJson(alertVO);

									if (SystemMap.getSosList().contains(SystemMap.getUsermap().get(tagAdd))){
										//推送
										send(userid, jsonObject.toString());
									}


								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}



				//电池电量百分比
				int battery = tag_info.getBattery();
				if (usermap.get(tagAdd)!=null){

					Tag tag = tagService.getTagByOnlyAddress(tagAdd);
					tag.setElectric(String.valueOf(battery));
					//更新新数据库
					tagService.updateByPrimaryKeySelective(tag);
					//是否触发电量报警
					if ((tag_info.getBattery()<MAX_BATTERY)){
						//websocket通知前端有警告
						Integer userid = SystemMap.getUsermap().get(tagAdd);
						AlertVO alertVO = new AlertVO();
						//alertVO.setId(tagStatus.getId());
						alertVO.setTagAddress(tagAdd);
						alertVO.setData(String.valueOf(tag_info.getBattery()));
						alertVO.setAlertType("4");
						alertVO.setIsdeal("0");
						alertVO.setMapKey(mapKey);

						if (tag.getX() != null) {
							alertVO.setX(tag.getX());
						}
						if (tag.getY() != null) {
							alertVO.setY(tag.getY());
						}
						Person person = personService.getPersonByAddress(userid, tagAdd);
						if (person != null) {
							alertVO.setType("person");
							alertVO.setIdCard(person.getIdCard());
							alertVO.setName(person.getPersonName());
						}
						Goods goods = goodsService.getGoodsByAddress(userid, tagAdd);
						if (goods != null) {
							alertVO.setType("goods");
							alertVO.setIdCard(goods.getGoodsIdcard());
							alertVO.setName(goods.getGoodsName());
						}
						alertVO.setAddTime(sdf.format(new Date()));
						Gson gson=new Gson();


						//报警标识
						String alertData=tagAdd+BATTERY;
						Map<String, String> alertmap = SystemMap.getAlertmap();
						String time = alertmap.get(alertData);

						//最新时间
						String format = sdf.format(new Date());
						if (time==null||"".equals(time)){
							alertmap.put(alertData,format);
							TagStatus tagStatus = new TagStatus();
							peridcard = SystemMap.getTagAndPersonMap().get(tagAdd);
							tagStatus.setPersonIdcard(peridcard);
							tagStatus.setAddTime(new Date());
							tagStatus.setAlertType("4");
							tagStatus.setData(String.valueOf(tag_info.getBattery()));
							tagStatus.setIsdeal("0");
							tagStatus.setMapKey(mapkey);
							tagStatus.setX(tag.getX());
							tagStatus.setY(tag.getY());
							tagStatus.setZ(tag.getZ());

							if (usermap.get(tagAdd) != null) {
								tagStatus.setUserId(usermap.get(tagAdd));
							}
							//插入数据库
							tagStatusService.insertSelective(tagStatus);

							alertVO.setId(tagStatus.getId());
							String jsonObject = gson.toJson(alertVO);

							if (SystemMap.getBatteryList().contains(SystemMap.getUsermap().get(tagAdd))){
								//推送
								send(userid, jsonObject.toString());
							}



						}else {
							try {
								if (System.currentTimeMillis()/1000-sdf.parse(time).getTime()/1000>ALERT_TIME){
									alertmap.put(alertData,format);
									TagStatus tagStatus = new TagStatus();
									peridcard = SystemMap.getTagAndPersonMap().get(tagAdd);
									tagStatus.setPersonIdcard(peridcard);
									tagStatus.setAddTime(new Date());
									tagStatus.setAlertType("4");
									tagStatus.setData(String.valueOf(tag_info.getBattery()));
									tagStatus.setIsdeal("0");
									tagStatus.setMapKey(mapkey);
									tagStatus.setX(tag.getX());
									tagStatus.setY(tag.getY());
									tagStatus.setZ(tag.getZ());

									if (usermap.get(tagAdd) != null) {
										tagStatus.setUserId(usermap.get(tagAdd));
									}
									//插入数据库
									tagStatusService.insertSelective(tagStatus);

									alertVO.setId(tagStatus.getId());
									String jsonObject = gson.toJson(alertVO);

									if (SystemMap.getBatteryList().contains(SystemMap.getUsermap().get(tagAdd))){
										//推送
										send(userid, jsonObject.toString());
									}
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}

				}
				if (tag_info.getHeart_rate()>0){
					//插入心率历史记录表
					HeartRateHistory heartRateHistory = new HeartRateHistory();
					heartRateHistory.setAddTime(new Date());
					peridcard = SystemMap.getTagAndPersonMap().get(tagAdd);
					heartRateHistory.setPersonIdcard(peridcard);
					heartRateHistory.setTagData(tag_info.getHeart_rate());

					//userid
					heartRateHistory.setUserId(usermap.get(tagAdd));
					heartRateHistoryService.insertSelective(heartRateHistory);
				}


				HeartRateSet heartRateSet = heartRateSetService.getHeartRateSet(usermap.get(tagAdd));
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

					if (tag_info.getHeart_rate()>0){
						if (tag_info.getHeart_rate() > heartRateSet.getMaxData() || tag_info.getHeart_rate() < heartRateSet.getMinData()) {
							//websocket通知前端有警告
							Integer userid = SystemMap.getUsermap().get(tagAdd);
							AlertVO alertVO = new AlertVO();
							//alertVO.setId(tagStatus.getId());
							alertVO.setTagAddress(tagAdd);
							alertVO.setData(String.valueOf(tag_info.getHeart_rate()));
							alertVO.setAlertType("2");
							alertVO.setIsdeal("0");
							alertVO.setMapKey(mapKey);
							Tag tag = tagService.getTagByOnlyAddress(tagAdd);
							if (tag.getX() != null) {
								alertVO.setX(tag.getX());
							}
							if (tag.getY() != null) {
								alertVO.setY(tag.getY());
							}
							Person person = personService.getPersonByAddress(userid, tagAdd);
							if (person==null){
								return;
							}
							if (person != null) {
								alertVO.setType("person");
								alertVO.setIdCard(person.getIdCard());
								alertVO.setName(person.getPersonName());
							}
							Goods goods = goodsService.getGoodsByAddress(userid, tagAdd);
							if (goods != null) {
								alertVO.setType("goods");
								alertVO.setIdCard(goods.getGoodsIdcard());
								alertVO.setName(goods.getGoodsName());
							}
							alertVO.setAddTime(sdf.format(new Date()));
							Gson gson=new Gson();


							//net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO);
					/*JsonConfig jsonConfig = new JsonConfig();
					jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
					net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO, jsonConfig);*/

							//报警标识
							String alertData=tagAdd+HEART;
							Map<String, String> alertmap = SystemMap.getAlertmap();
							String time = alertmap.get(alertData);
							//最新时间
							String format = sdf.format(new Date());
							if (time==null||"".equals(time)){
								alertmap.put(alertData,format);
								TagStatus tagStatus = new TagStatus();
								peridcard = SystemMap.getTagAndPersonMap().get(tagAdd);
								tagStatus.setPersonIdcard(peridcard);
								tagStatus.setAddTime(new Date());
								tagStatus.setAlertType("2");
								tagStatus.setData(String.valueOf(tag_info.getHeart_rate()));
								tagStatus.setIsdeal("0");
								tagStatus.setMapKey(mapkey);
								tagStatus.setX(tag.getX());
								tagStatus.setY(tag.getY());
								tagStatus.setZ(tag.getZ());

								if (usermap.get(tagAdd) != null) {
									tagStatus.setUserId(usermap.get(tagAdd));
								}
								//插入数据库
								tagStatusService.insertSelective(tagStatus);

								alertVO.setId(tagStatus.getId());
								String jsonObject = gson.toJson(alertVO);
								//如果该标签的用户开启了心率报警开关
								if (SystemMap.getHeartList().contains(SystemMap.getUsermap().get(tagAdd))){
									send(userid, jsonObject.toString());
								}



							}else {
								try {
									if (System.currentTimeMillis()/1000-sdf.parse(time).getTime()/1000>ALERT_TIME){
										alertmap.put(alertData,format);
										TagStatus tagStatus = new TagStatus();
										peridcard = SystemMap.getTagAndPersonMap().get(tagAdd);
										tagStatus.setPersonIdcard(peridcard);
										tagStatus.setAddTime(new Date());
										tagStatus.setAlertType("2");
										tagStatus.setData(String.valueOf(tag_info.getHeart_rate()));
										tagStatus.setIsdeal("0");
										tagStatus.setMapKey(mapkey);
										tagStatus.setX(tag.getX());
										tagStatus.setY(tag.getY());
										tagStatus.setZ(tag.getZ());

										if (usermap.get(tagAdd) != null) {
											tagStatus.setUserId(usermap.get(tagAdd));
										}
										//插入数据库
										tagStatusService.insertSelective(tagStatus);
										alertVO.setId(tagStatus.getId());
										String jsonObject = gson.toJson(alertVO);

										//如果该标签的用户开启了心率报警开关
										if (SystemMap.getHeartList().contains(SystemMap.getUsermap().get(tagAdd))){
											send(userid, jsonObject.toString());
										}


									}
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						}
					}



				//步数
				//int step = tag_info.getStep();

					//剪断警报
					int cut = tag_info.getCut();
					if (cut==1){
						//websocket通知前端有警告
						Integer userid = SystemMap.getUsermap().get(tagAdd);


						AlertVO alertVO = new AlertVO();
						//alertVO.setId(tagStatus.getId());
						alertVO.setTagAddress(tagAdd);
						alertVO.setData(String.valueOf("1"));
						alertVO.setAlertType("3");

						alertVO.setIsdeal("0");
						alertVO.setMapKey(mapKey);
						Tag tag = tagService.getTagByOnlyAddress(tagAdd);
						if (tag.getX() != null) {
							alertVO.setX(tag.getX());
						}
						if (tag.getY() != null) {
							alertVO.setY(tag.getY());
						}
						Person person = personService.getPersonByAddress(userid, tagAdd);
						if (person==null){
							return;
						}
						if (person != null) {
							alertVO.setType("person");
							alertVO.setIdCard(person.getIdCard());
							alertVO.setName(person.getPersonName());
						}
						Goods goods = goodsService.getGoodsByAddress(userid, tagAdd);
						if (goods != null) {
							alertVO.setType("goods");
							alertVO.setIdCard(goods.getGoodsIdcard());
							alertVO.setName(goods.getGoodsName());
						}
						alertVO.setAddTime(sdf.format(new Date()));
						Gson gson=new Gson();

						//net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO);
					/*JsonConfig jsonConfig = new JsonConfig();
					jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
					net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO, jsonConfig);*/

						//报警标识
						String alertData=tagAdd+CUT;
						Map<String, String> alertmap = SystemMap.getAlertmap();
						String time = alertmap.get(alertData);
						//最新时间
						String format = sdf.format(new Date());
						if (time==null||"".equals(time)){
							alertmap.put(alertData,format);

							TagStatus tagStatus = new TagStatus();
							peridcard = SystemMap.getTagAndPersonMap().get(tagAdd);
							tagStatus.setPersonIdcard(peridcard);
							tagStatus.setAddTime(new Date());
							tagStatus.setAlertType("3");
							tagStatus.setData("1");
							tagStatus.setIsdeal("0");
							tagStatus.setMapKey(mapkey);
							tagStatus.setX(tag.getX());
							tagStatus.setY(tag.getY());
							tagStatus.setZ(tag.getZ());

							if (usermap.get(tagAdd) != null) {
								tagStatus.setUserId(usermap.get(tagAdd));
							}
							//插入数据库
							tagStatusService.insertSelective(tagStatus);
							alertVO.setId(tagStatus.getId());
							String jsonObject = gson.toJson(alertVO);

							//如果该标签的用户开启了剪断报警开关
							if (SystemMap.getCutList().contains(SystemMap.getUsermap().get(tagAdd))){
								send(userid, jsonObject.toString());
							}


						}else {
							try {
								if (System.currentTimeMillis()/1000-sdf.parse(time).getTime()/1000>ALERT_TIME){
									alertmap.put(alertData,format);
									TagStatus tagStatus = new TagStatus();
									peridcard = SystemMap.getTagAndPersonMap().get(tagAdd);
									tagStatus.setPersonIdcard(peridcard);
									tagStatus.setAddTime(new Date());
									tagStatus.setAlertType("3");
									tagStatus.setData("1");
									tagStatus.setIsdeal("0");
									tagStatus.setMapKey(mapkey);
									tagStatus.setX(tag.getX());
									tagStatus.setY(tag.getY());
									tagStatus.setZ(tag.getZ());

									if (usermap.get(tagAdd) != null) {
										tagStatus.setUserId(usermap.get(tagAdd));
									}
									//插入数据库
									tagStatusService.insertSelective(tagStatus);
									alertVO.setId(tagStatus.getId());
									String jsonObject = gson.toJson(alertVO);

									//如果该标签的用户开启了剪断报警开关
									if (SystemMap.getCutList().contains(SystemMap.getUsermap().get(tagAdd))){
										send(userid, jsonObject.toString());
									}


								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}




			}

			@Override
			public void onStationStatus(String key, Station_status[] stations) {
				//System.out.println("基站状态="+stations.toString());
                for (Station_status status : stations) {
					//System.out.println(status.toString());
                    Station station = stationService.selectByPrimaryKey(status.getId());
					//System.out.println(station.getAddr());
                    if (station!=null){
                        station.setStationStatus(status.getStatus());
                        //更新状态
                        stationService.updateByPrimaryKeySelective(station);
                    }
                }

			}

			@Override
			public void onConnect(String key,String userInfo) {
				System.out.println("OnConnect"+key+"--"+userInfo);
				//将传过来的地图key与数据库作对比
				com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(userInfo);
				if (map==null){
					return;
				}
				//将 地图key 服务器与cle通信链路  存到缓存中
				SystemMap.getCleAndKeyMap().put(userInfo,key);
				//地图的key
				String mapkey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
				System.out.println("保存地图:"+mapkey);

				String cf="<?xml version=\"1.0\" encoding=\"utf-8\"?><req type=\"rf cfg\">   <rf chan=\"2\" prf=\"64\" rate=\"6810\" code=\"9\" plen=\"128\" pac=\"8\" nsfd=\"0\"/>" +
						"</req>\n";

				//CLELinker.sendCmd(key,cf);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(3000);
							skylabSDK.getStations(key);
						}catch(Exception e){
							System.out.println(e.getMessage());
						}
					}
				}).start();
			}

			@Override
			public void onStation(String key, String stationsJson){
				System.out.println("stationsJson:"+stationsJson);

				Gson gson = new Gson();//创建Gson对象
				JsonParser jsonParser = new JsonParser();
				JsonArray jsonElements = jsonParser.parse(stationsJson).getAsJsonArray();//获取JsonArray对象
				ArrayList<bean.Station> beans = new ArrayList<>();
				for (JsonElement bean : jsonElements) {
					bean.Station bean1 = gson.fromJson(bean, bean.Station.class);//解析
					beans.add(bean1);
				}

				String mapkey = SystemMap.getKey(SystemMap.getCleAndKeyMap(),key);
				//System.out.println("uuid:"+uuid);
				com.tg.locationsystem.entity.Map map=mapService.getMapByUuid(mapkey);
				if (map==null){
					System.out.println("地图为空");
					return;
				}
				List<Station> stationList=stationService.getStationByMapId(map.getId());
				//System.out.println("数据库数据:"+stationList.size());
				List<bean.Station> sdkList=new ArrayList(stationList.size());
				for (Station station : stationList) {
					if(station.getIsmaster()==null||"".equals(station.getIsmaster())){
						continue;
					}
					bean.Station station1=new bean.Station();
					station1.setId(station.getId().toString());
					station1.setX(station.getX().toString());
					station1.setY(station.getY().toString());
					station1.setZ(station.getZ().toString());
					station1.setMaster_lag_delay(station.getMasterLagDelay());
					station1.setAddr(station.getAddr());
					station1.setAnt_delay_rx(station.getAntDelayRx());
					station1.setAnt_delay_tx(station.getAntDelayTx());
					station1.setIsmaster(station.getIsmaster());
					station1.setMaster_addr(station.getMasterAddr());
					station1.setRfdistance(station.getRfdistance());
					station1.setDimension(Integer.parseInt(station.getDimension()));
					String masteranchoraddress = station.getMasteranchorAddress();
					if (masteranchoraddress!=null&&!"".equals(masteranchoraddress)){
						String[] split = masteranchoraddress.split(",");
						station1.setMasteranchorAddress(split);
					}

					sdkList.add(station1);

				}
				//通过Gson将list集合转换为jsonArray数组
				if (sdkList.size()!=0){
					String string=gson.toJson(sdkList);
					System.out.println("数据库数据:"+string);
					//发送
					skylabSDK.configStation(key,string);
					//System.out.println("beans:"+beans.size());
					new Thread(new Runnable() {
						public void run() {
							try {
								Thread.sleep(5000L);
								skylabSDK.startLocation(key);
							} catch (Exception var2) {
								System.out.println(var2.getMessage());
							}

						}
					}).start();


				}

				//对比收到的基站与数据库基站,若收到基站数量大于数据库基站数量,插入
				for (bean.Station bean : beans) {
					boolean isexit=false;
					for (Station station : stationList) {
						if (bean.getAddr().equals(station.getAddr())){
							isexit=true;
							break;
						}else {
							isexit=false;
						}
					}
					//System.out.println("bean结果:"+isexit);
					if (!isexit){
						Station station=new Station();
						station.setX(Float.parseFloat(bean.getX()));
						station.setY(Float.parseFloat(bean.getY()));
						station.setZ(Float.parseFloat(bean.getZ()));
						station.setAddr(bean.getAddr());
						station.setUserId(map.getUserId());
						station.setMasterLagDelay(bean.getMaster_lag_delay());
						station.setAntDelayRx(bean.getAnt_delay_rx());
						station.setAntDelayTx(bean.getAnt_delay_tx());
						station.setIsmaster(bean.getIsmaster());
						station.setMasterAddr(bean.getMaster_addr());
						//station1.setMaster_addr(stationJson1.getMasterAddr());
						//station.setMasteranchorAddress(bean.getMasteranchor_address());
						station.setMasteranchoraddress(null);
						station.setDimension(String.valueOf(bean.getDimension()));
                        station.setRfdistance(bean.getRfdistance());
                        if (map.getId()!=null){
                            station.setMapId(map.getId());
                        }
                        //将该基站插入数据库
                        stationService.insertSelective(station);
					}
				}
			}
		});
		skylabSDK.setNetWorkCallback(new NetWorkCallback() {
			@Override
			public void success(String serialize) {
				System.out.println("成功回调的值="+serialize);
			}

			@Override
			public void fail(String serialize) {
				System.out.println("失败回调的值="+serialize);
			}
		});

		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
		skylabSDK.start();

	}

	private static void send(Integer userid, String string) {
		//System.out.println("userid:"+userid);
		for (String key : SystemMap.getChannelmap().keySet()) {
			//System.out.println("key:"+key);
			if(key!=null&&!key.equals("")&&key.contains(",")&key.split(",")[1].equals(userid+"")){
				Channel channel = SystemMap.getChannelmap().get(key);
				TextWebSocketFrame tws = new TextWebSocketFrame(string);
				channel.writeAndFlush(tws);
			}
		}
	}

	private  void cach() {
		List<Tag> tagList=tagService.getTagList();
		System.out.println("得到所有标签:"+tagList.size());
		//把标签放到缓存中
		Map<String, Integer> usermap = SystemMap.getUsermap();
		Map<String, Integer> countmap = SystemMap.getCountmap();
		Map<String, String> map = SystemMap.getMap();
		Map<String, String> tagAndPersonMap = SystemMap.getTagAndPersonMap();
		for (Tag tag : tagList) {
			StringBuffer sb=new StringBuffer();

			sb.append(tag.getX()==null?"0":tag.getX());

			sb.append(",");

			sb.append(tag.getY()==null?"0":tag.getY());

			sb.append(",");

			sb.append(tag.getZ()==null?"0":tag.getZ());

			sb.append(",");
			sb.append(sdf1.format(tag.getLastonline()));
			sb.append(",");
			sb.append(tag.getMapKey());

			map.put(tag.getAddress(),sb.toString());
			usermap.put(tag.getAddress(),tag.getUserId());
			countmap.put(tag.getAddress(),0);
			Person person = personService.getPersonByOnlyAddress(tag.getAddress());
			if (person!=null){
				tagAndPersonMap.put(tag.getAddress(),person.getIdCard());
			}
			Goods goods=goodsService.getGoodsByOnlyAddress(tag.getAddress());
			if (goods!=null){
				tagAndPersonMap.put(tag.getAddress(),goods.getGoodsIdcard());
		}
		}
		//把围栏放到缓存中
		List<Frence> frenceList=frenceService.getFrenceList();
		System.out.println("得到所有围栏:"+frenceList.size());
		Map<Integer, List<Frence>> frencemap = SystemMap.getFrencemap();
		List<Frence> userFrenceList=null;
		for (Frence frence : frenceList) {
			if (!frencemap.containsKey(frence)){
				userFrenceList=new ArrayList<>();
				userFrenceList.add(frence);
				frencemap.put(frence.getUserId(),userFrenceList);
			}else {
				userFrenceList.add(frence);
				frencemap.put(frence.getUserId(),userFrenceList);
			}

		}
		//把地图规则放到缓存
		List<MapRule> mapRuleList=mapRuleService.getAllRule();
		for (MapRule mapRule : mapRuleList) {
			SystemMap.getMapRuleMap().put(mapRule.getMapKey(),mapRule.getMapRule());
		}
		//把报警开关放到缓存
		List<AlertSet> alertSetList=alertSetService.getAllSetList();
		List<Integer> sosList = SystemMap.getSosList();
		List<Integer> heartList = SystemMap.getHeartList();
		List<Integer> cutList = SystemMap.getCutList();
		List<Integer> batteryList = SystemMap.getBatteryList();
		for (AlertSet alertSet : alertSetList) {
			if ("1".equals(alertSet.getSosAlert())){
				sosList.add(alertSet.getUserId());
			}
			if ("1".equals(alertSet.getHeartAlert())){
				heartList.add(alertSet.getUserId());
			}
			if ("1".equals(alertSet.getCutAlert())){
				cutList.add(alertSet.getUserId());
			}
			if ("1".equals(alertSet.getBatteryAlert())){
				batteryList.add(alertSet.getUserId());
			}
		}

		List<EleCallSet> eleCallSetList = eleCallSetService.getEleCallSetList();
		Map<Integer, Timer> timermap = SystemMap.getTimermap();
		for (EleCallSet eleCallSet : eleCallSetList) {
			// 创建定时器
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				// 在run方法中的语句就是定时任务执行时运行的语句。
				public void run() {
					//统计
					if (eleCallSet.getTimeInterval()==null){
						eleCallSet.setTimeInterval(15);
					}
					String format = simpleDateFormat.format(new Date());
					StringBuffer sb=new StringBuffer(format);
					sb.append("_");
					sb.append(eleCallSet.getUserId());
					//在线集合
					List<Tag> onlineList=new ArrayList<>();
					//离线集合
					List<Tag> notOnlineList=new ArrayList<>();
					List<Tag>tagList1=tagService.getTagsByUsed(eleCallSet.getUserId());
					for (Tag tag : tagList1) {
						EleCall eleCall=new EleCall();
						eleCall.setUserId(tag.getUserId());
						eleCall.setAddress(tag.getAddress());
						if (tag.getX()!=null){
							eleCall.setX(tag.getX());
						}
						if (tag.getY()!=null){
							eleCall.setY(tag.getY());
						}
						if (tag.getZ()!=null){
							eleCall.setZ(tag.getZ());
						}
						//电子点名设置人员名字,地图key,电话,logo,idcard
						Person person = personService.getPersonByAddress(tag.getUserId(), tag.getAddress());
						if (person!=null){
							eleCall.setPersonName(person.getPersonName());
							eleCall.setPersonPhone(person.getPersonPhone());
							eleCall.setPersonLog(person.getImg());
							eleCall.setPersonIdcard(person.getIdCard());
						}
						eleCall.setMapKey(tag.getMapKey());
						eleCall.setIsonline(tag.getIsonline());
						eleCall.setLastTime(tag.getLastonline());
						eleCall.setTimeUserid(sb.toString());
						int i = eleCallService.insertSelective(eleCall);
						if (i>0){
							if ("1".equals(tag.getIsonline())){
								onlineList.add(tag);
							}
							if ("0".equals(tag.getIsonline())){
								notOnlineList.add(tag);
							}
						}
					}
					StatisticsCall statisticsCall=new StatisticsCall();
					statisticsCall.setTotal(onlineList.size()+notOnlineList.size());
					statisticsCall.setOnline(onlineList.size());
					statisticsCall.setNotOnline(notOnlineList.size());
					statisticsCall.setTimeUser(sb.toString());
					statisticsCall.setUserId(eleCallSet.getUserId());
					statisticsCall.setRecordTime(new Date());
					statisticsCall.setTimeInterval(eleCallSet.getTimeInterval());
					statisticsCallService.insertSelective(statisticsCall);
				}
				// 表示在3秒之后开始执行，并且每2秒执行一次
			}, 3000, eleCallSet.getTimeInterval()*1000*60);
			timermap.put(eleCallSet.getUserId(),timer);
		}

	}


	private static final int port = 3600;

	private void run() throws Exception{
		//NioEventLoopGroup是用来处理IO操作的多线程事件循环器
		EventLoopGroup bossGroup  = new NioEventLoopGroup();  // 用来接收进来的连接
		EventLoopGroup workerGroup  = new NioEventLoopGroup();// 用来处理已经被接收的连接
		try{
			ServerBootstrap server =new ServerBootstrap();//是一个启动NIO服务的辅助启动类
			server.group(bossGroup,workerGroup )
					.channel(NioServerSocketChannel.class)  // 这里告诉Channel如何接收新的连接
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// 自定义处理类
							ch.pipeline().addLast(new NettyServerHandler());
							//ch.pipeline().addLast(new ChildChannelHandler());
						}
					});
			server.option(ChannelOption.SO_BACKLOG,128);
			server.childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = server.bind(port).sync();// 绑定端口，开始接收进来的连接
			//ChannelFuture w = server.bind(3604).sync();// 绑定端口，开始接收进来的连接
			System.out.println("服务端启动成功...");
			// 监听服务器关闭监听
			f.channel().closeFuture().sync();
		}finally {
			bossGroup.shutdownGracefully(); ////关闭EventLoopGroup，释放掉所有资源包括创建的线程
			workerGroup.shutdownGracefully();
		}
		// 服务器绑定端口监听


	}

	@Bean
	public ConfigurableServletWebServerFactory webServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector ->
				connector.setProperty("relaxedQueryChars", "|{}[]\\"));
		return factory;
	}
	}


