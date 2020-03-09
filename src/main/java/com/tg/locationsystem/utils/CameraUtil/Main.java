package com.tg.locationsystem.utils.CameraUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hyy
 * @ Date2020/2/27
 */
public class Main {
    public static void main(String[] args) {
        String ip ="192.168.3.40";
        String id = "fourfour";
        CommandManager manager=new CommandManagerImpl(10);
        Map<String,String> map = new HashMap<>();
        map.put("appName", id);
        map.put("input", "rtsp://admin:Z1234567@" + ip + ":554");
        map.put("output", "rtmp://localhost/live/");
        map.put("codec", "libx264");
        map.put("fmt", "flv");
        map.put("fps", "60");
        map.put("rs", "640x360");
        map.put("twoPart", "0");
        String ids=manager.start(map);
        System.out.println(ids);
    }
}
