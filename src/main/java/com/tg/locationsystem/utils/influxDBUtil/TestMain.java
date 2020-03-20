package com.tg.locationsystem.utils.influxDBUtil;

import com.tg.locationsystem.config.KalmanFilter2;
import com.tg.locationsystem.entity.TagHistoryVO;
import com.tg.locationsystem.pojo.PathVO1;
import com.tg.locationsystem.utils.DateUtil;
import com.tg.locationsystem.utils.StringUtils;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.BeanWrapperImpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestMain {
    static DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static void main(String[] args) throws ParseException {
        InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://localhost:8086", "historyData", "oneDay");
//        System.out.println(influxDBConnection.ping());
//        Map<String, String> tags = new HashMap<String, String>();
//        tags.put("mapKey", "156");
//        tags.put("personIdcard", "967");
//        Map<String, Object> fields = new HashMap<String, Object>();
//
//        // 数值型，InfluxDB的字段类型，由第一天插入的值得类型决定
//        fields.put("x", 3.14159);
//        fields.put("y", 1.412f);
//        fields.put("z", 1.412f);
//        // 时间使用毫秒为单位
//        influxDBConnection.insert("ttt", tags, fields, System.currentTimeMillis(), TimeUnit.MILLISECONDS);
//        TagHistoryService tagHistoryService = new TagHistoryService();
//        System.out.println(System.currentTimeMillis());
//        List<TagHistory> tagHistories = tagHistoryService.getHistoryTest("tag_history20191120");
//        System.out.println(tagHistories.size());
//        System.out.println(System.currentTimeMillis());



        String mapKey = "656543db-20cf-4fbc-9081-2588f124cacf";
        String personIdcard = "362330199701245015";
        String address = "32DB91CF0001CADE";
        Date startTime = sdf2.parse("2020-03-19T10:20:36Z");
        Date endTime = sdf2.parse("2020-03-20T20:20:36Z");
        String sqlCommand = "select * from \"tagHistory"+address+"\" where time>"+ DateUtil.utcToLocal(startTime).getTime()*1000000+" and time < "+DateUtil.utcToLocal(endTime).getTime()*1000000+" and mapKey='"+mapKey+"' and personIdcard='"+personIdcard+"'";
        System.err.println(sqlCommand);
        QueryResult results = influxDBConnection.query(sqlCommand);
        //results.getResults()是同时查询多条SQL语句的返回值，此处我们只有一条SQL，所以只取第一个结果集即可。
        if(results.getResults() == null){
            System.out.println("no data");
            return;
        }
        System.out.println(System.currentTimeMillis());
        List<List<TagHistoryVO>> historyList = new ArrayList<>();
        for (QueryResult.Result result : results.getResults()) {
            List<QueryResult.Series> series= result.getSeries();
            for (QueryResult.Series serie : series) {
//				Map<String, String> tags = serie.getTags();
                List<List<Object>>  values = serie.getValues();
                List<String> columns = serie.getColumns();
                historyList.addAll(getQueryData(columns, values));
            }
        }
        List<PathVO1> pathVOList = new ArrayList<>();
        for (int j = 0; j < historyList.size(); j++) {
            PathVO1 pathVO = new PathVO1();
            String stsrt = sdf2.format(historyList.get(j).get(0).getTime());
            String end = sdf2.format(historyList.get(j).get(historyList.get(j).size()-1).getTime());
            // System.out.println("date41="+j+"="+sdf.format(new Date()));
            pathVO.setPath(StringUtils.getPath1(historyList.get(j)));
            //System.out.println("date42="+j+"="+sdf.format(new Date()));
            pathVO.setStartTime(stsrt);
            pathVO.setEndTime(end);
            pathVO.setTagHistoryList(historyList.get(j));
            pathVOList.add(pathVO);
        }
        System.out.println(pathVOList.size());
        System.out.println(System.currentTimeMillis());
    }

    /***整理列名、行数据***/
    private static List<List<TagHistoryVO>> getQueryData(List<String> columns, List<List<Object>> values) throws ParseException {

        List<List<TagHistoryVO>> historyList = new ArrayList<>();
        List<TagHistoryVO> offLine = new ArrayList<>();
        TagHistoryVO startPosition = new TagHistoryVO();
        List<TagHistoryVO> lists = new ArrayList<TagHistoryVO>();
        for (List<Object> list : values) {
            TagHistoryVO info = new TagHistoryVO();
            BeanWrapperImpl bean = new BeanWrapperImpl(info);
            for(int i=0; i< list.size(); i++){
                String propertyName = columns.get(i);//字段名
                Object value = list.get(i);//相应字段值
                if (propertyName.equals("time")) {
                    value = sdf2.parse(value.toString());
                }
                bean.setPropertyValue(propertyName, value);
            }
            lists.add(info);
            if (lists.size()==1){
                startPosition = info;
                offLine.add(startPosition);
                historyList.add(offLine);
            }
            if (((info.getTime().getTime()-startPosition.getTime().getTime())/1000) > 30){
                offLine = new ArrayList<>();
                startPosition = KalmanFilter2.getInstance().printM2(startPosition,info);
                offLine.add(startPosition);
                historyList.add(offLine);
            }else {
                startPosition = KalmanFilter2.getInstance().printM2(startPosition,info);
                offLine.add(startPosition);
            }
            startPosition = info;
        }
        System.out.println(lists.size());
        return historyList;
    }

    /***转义字段***/
    private static String setColumns(String column){
        String[] cols = column.split("_");
        StringBuffer sb = new StringBuffer();
        for(int i=0; i< cols.length; i++){
            String col = cols[i].toLowerCase();
            if(i != 0){
                String start = col.substring(0, 1).toUpperCase();
                String end = col.substring(1).toLowerCase();
                col = start + end;
            }
            sb.append(col);
        }
        return sb.toString();
    }

}
