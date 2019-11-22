package com.tg.locationsystem.utils;

import com.tg.locationsystem.entity.Frence;
import io.netty.channel.Channel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hyy
 * @ Date2019/7/23
 */
public class SystemMap {

    public SystemMap() {
    }
    //缓存标签address x,y,z,time,地图key
    private static Map<String, String> map = new ConcurrentHashMap();

    //缓存标签address userid
    private static Map<String, Integer> usermap=new ConcurrentHashMap();

    //缓存userid 围栏
    private static Map<Integer, List<Frence>> frencemap=new ConcurrentHashMap();

    //缓存次数
    private static Map<String, Integer> countmap=new ConcurrentHashMap();

    //缓存userid channel
    private static Map<String,Channel> channelmap = new ConcurrentHashMap();

    //缓存userid 定时器
    private static Map<Integer,Timer> timermap = new ConcurrentHashMap();

    //缓存标签address 序列号
    private static Map<String,String> serimap = new ConcurrentHashMap();

    //缓存警报
    private static Map<String,String> alertmap = new ConcurrentHashMap();

    //缓存地图uuid,服务器与cle通信链路
    private static Map<String,String> cleAndKeyMap = new ConcurrentHashMap();

    //缓存标签跟人员
    private static Map<String,String> tagAndPersonMap = new ConcurrentHashMap();

    //缓存地图key 地图规则
    private static Map<String,String> MapRuleMap = new ConcurrentHashMap();

    public static Map<String, Integer> getCountmap() {
        return countmap;
    }

    public static void setCountmap(Map<String, Integer> countmap) {
        SystemMap.countmap = countmap;
    }

    public static Map<String, Integer> getUsermap() {
        return usermap;
    }

    public static void setUsermap(Map<String, Integer> usermap) {
        SystemMap.usermap = usermap;
    }

    public static Map<Integer, List<Frence>> getFrencemap() {
        return frencemap;
    }

    public static void setFrencemap(Map<Integer, List<Frence>> frencemap) {
        SystemMap.frencemap = frencemap;
    }

    public static Map<Integer, Timer> getTimermap() {
        return timermap;
    }

    public static void setTimermap(Map<Integer, Timer> timermap) {
        SystemMap.timermap = timermap;
    }

    public static Map<String, String> getMap() {
        return map;
    }

    public static Map<String, Channel> getChannelmap() {
        return channelmap;
    }

    public static void setChannelmap(Map<String, Channel> channelmap) {
        SystemMap.channelmap = channelmap;
    }

    public static Map<String, String> getSerimap() {
        return serimap;
    }

    public static void setSerimap(Map<String, String> serimap) {
        SystemMap.serimap = serimap;
    }

    public static Map<String, String> getAlertmap() {
        return alertmap;
    }

    public static void setAlertmap(Map<String, String> alertmap) {
        SystemMap.alertmap = alertmap;
    }

    public static void setMap(Map<String, String> map) {
        SystemMap.map = map;
    }

    public static Map<String, String> getCleAndKeyMap() {
        return cleAndKeyMap;
    }

    public static Map<String, String> getTagAndPersonMap() {
        return tagAndPersonMap;
    }

    public static void setTagAndPersonMap(Map<String, String> tagAndPersonMap) {
        SystemMap.tagAndPersonMap = tagAndPersonMap;
    }

    public static void setCleAndKeyMap(Map<String, String> cleAndKeyMap) {
        SystemMap.cleAndKeyMap = cleAndKeyMap;
    }
    //根据value值获取到对应的一个key值
    public static String getKey(Map<String,String> map, String value){
        String key = null;
        //Map,HashMap并没有实现Iteratable接口.不能用于增强for循环.
        for(String getKey: map.keySet()){
            if(map.get(getKey).equals(value)){
                key = getKey;
            }
        }
        return key;
        //这个key肯定是最后一个满足该条件的key.
    }

    public static Map<String, String> getMapRuleMap() {
        return MapRuleMap;
    }

    public static void setMapRuleMap(Map<String, String> mapRuleMap) {
        MapRuleMap = mapRuleMap;
    }
}
