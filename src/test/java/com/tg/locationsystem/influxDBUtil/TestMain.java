package com.tg.locationsystem.influxDBUtil;

import com.tg.locationsystem.entity.TagHistory;
import com.tg.locationsystem.service.ITagHistoryService;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestMain {
    @Autowired
    private ITagHistoryService tagHistoryService;
    private InfluxDBConnection influxDBConnection;
    private String username = "admin";//用户名
    private String password = "admin";//密码
    private String openurl = "http://127.0.0.1:8086";//连接地址
    private String database = "historyData";//数据库
    private String measurement = "tagHistory";
    @Before
    public void connectDB(){
        influxDBConnection = new InfluxDBConnection(username,password,openurl,database,"");
        //influxDBConnection.createRetentionPolicy("save2Day","2",1,true);
        System.out.println(influxDBConnection.ping());
    }

    @Test
    public void testInsert(){
        System.out.println(System.currentTimeMillis());
        List<TagHistory> tagHistories = tagHistoryService.getHistoryTest("tag_history_20191120");
        System.out.println(tagHistories.size());
        System.out.println(System.currentTimeMillis());
        Map<String, String> tags = new HashMap<String, String>();
        Map<String, Object> fields = new HashMap<String, Object>();

        for(TagHistory tagHistory : tagHistories){
            tags.put("mapKey",tagHistory.getMapKey());
            tags.put("personIdcard",tagHistory.getPersonIdcard());

            fields.put("id",tagHistory.getId());
            fields.put("x",tagHistory.getX());
            fields.put("y",tagHistory.getY());
            fields.put("z",tagHistory.getZ());

            influxDBConnection.insert(measurement,tags,fields,System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void testQuery(){
        System.out.println(System.currentTimeMillis());
        List<TagHistory> tagHistories = tagHistoryService.getHistoryTest("tag_history_20191120");
        System.out.println(tagHistories.size());
        System.out.println(System.currentTimeMillis());
        String command = "select * from tagHistory where mapKey = '656543db-20cf-4fbc-9081-2588f124cacf' ORDER BY time DESC";
        System.out.println(System.currentTimeMillis());
        QueryResult results = influxDBConnection.query(command);
        System.out.println(results.getResults().get(0).getSeries().stream().map(QueryResult.Series::getValues).collect(Collectors.toList()).get(0).size());
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void testInsert2(){
        System.out.println(System.currentTimeMillis());
        List<TagHistory> tagHistories = tagHistoryService.getHistoryTest("tag_history_20191121");
        System.out.println(tagHistories.size());
        System.out.println(System.currentTimeMillis());
        int i = 0;
        BatchPoints batchPoints = BatchPoints
                .database(database)
                .tag("async", "true")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        //遍历sqlserver获取数据
        for(TagHistory tagHistory : tagHistories){
            //创建单条数据对象——表名
            Point point = Point.measurement(measurement)
                    //tag属性——只能存储String类型
                    .tag("mapKey",tagHistory.getMapKey())
                    .tag("personIdcard",tagHistory.getPersonIdcard())
                    .time(tagHistory.getTime().getTime(),TimeUnit.MILLISECONDS)
                    //field存储数据
                    .addField("id",tagHistory.getId())
                    .addField("x",tagHistory.getX())
                    .addField("y",tagHistory.getY())
                    .addField("z",tagHistory.getZ())
                    .build();
            //将单条数据存储到集合中
            batchPoints.point(point);
            i++;
            //每读取十万条数据提交到influxdb存储一次
            if(i / 10000 == 1){
                i = 0;
                influxDBConnection.batchInsert(batchPoints);
                batchPoints = BatchPoints
                        .database(database)
                        .tag("async", "true")
                        .consistency(InfluxDB.ConsistencyLevel.ALL)
                        .build();
            }
        }
        influxDBConnection.batchInsert(batchPoints);
        System.out.println(System.currentTimeMillis());
        String command = "select * from tagHistory where mapKey = '656543db-20cf-4fbc-9081-2588f124cacf' ORDER BY time DESC";
        System.out.println(System.currentTimeMillis());
        QueryResult results = influxDBConnection.query(command);
        System.out.println(results.getResults().get(0).getSeries().stream().map(QueryResult.Series::getValues).collect(Collectors.toList()).get(0).size());
        System.out.println(System.currentTimeMillis());
    }
}
