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
       /* Map<String,String> map = new HashMap<>();
        map.put("appName", id);
        map.put("input", "rtsp://admin:Z1234567@" + ip + ":554");
        map.put("output", "rtmp://localhost/live/");
        map.put("codec", "libx264");
        map.put("fmt", "flv");
        map.put("fps", "60");
        map.put("rs", "640x360");
        map.put("twoPart", "0");
        String ids=manager.start(map);
        System.out.println(ids);*/

//ffmpeg -i rtsp://admin:Z1234567@192.168.3.39:554-vcodeclibx264 -b:v 400k -s 640x360 -r 25 -acodec libfaac -b:a 64k -f flv -an rtmp://localhost/live/fourfour  -c copy -map 0 -f segment -segment_time 10 -segment_format mp4 C:\\Users\\92962\\Pictures\\temp\\out%03d.mp4



        StringBuffer sb=new StringBuffer("");
        sb.append("ffmpeg -i rtsp://admin:Z1234567@192.168.3.39:554-vcodeclibx264");
        sb.append(" -b:v 400k -s 640x360 -r 25 -acodec libfaac -b:a 64k -f flv -an ");
        sb.append("rtmp://localhost/live/fourfour  -c copy -map 0 -f segment -segment_time 10 -segment_format mp4 ");
        sb.append("C:\\video\\%03d.mp4");
        System.out.println("sb:"+sb.toString());

        String comm="ffmpeg -i rtsp://admin:Z1234567@192.168.3.39:554-vcodeclibx264 -b:v 400k -s 640x360 -r 25 -acodec libfaac -b:a 64k -f flv -an rtmp://localhost/live/fourfour  -c copy -map 0 -f segment -segment_time 10 -segment_format mp4 C:\\video\\out%03d.mp4.mp4";
        String conn="ffmpeg -i rtsp://admin:Z1234567@192.168.3.40:554-vcodeclibx264 -b:v 400k -s 640x360 -r 25 -acodec libfaac -b:a 64k -f flv -an rtmp://localhost/live/fourfour  -c copy -map 0 -f segment -segment_time 10 -segment_format mp4 C:\\video\\%03d.mp4";



        String start = manager.start(id, comm);
        System.out.println("start:"+start);
    }
}
