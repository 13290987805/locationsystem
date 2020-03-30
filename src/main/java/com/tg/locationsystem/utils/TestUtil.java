package com.tg.locationsystem.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * @author hyy
 * @ Date2019/11/27
 */
public class TestUtil {
    public static boolean test(String path) throws IOException {

        File file=new File(path);
        FileInputStream fis=new FileInputStream(file);
        // System.out.println(fis.available());
        return true;
        //Request.ServerVariables("Request_Method")="POST"
    }

    //判断请求是get还是post
    public static String getGoP(HttpServletRequest request){
        String method = request.getMethod();
        return  method;

    }
    public static String getIP(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    /**
     * 通过身份证号码获取出生日期、性别、年龄
     * @param certificateNo
     * @return 返回的出生日期格式：1990-01-01 性别格式：W-女，M-男
     *
     */
    public static Map<String, String> getBirAgeSex(String certificateNo) {
        String birthday = "";
        String age = "";
        String sexCode = "";

        int year = Calendar.getInstance().get(Calendar.YEAR);
        char[] number = certificateNo.toCharArray();
        boolean flag = true;
        if (number.length == 15) {
            for (int x = 0; x < number.length; x++) {
                if (!flag)
                    return new HashMap<String, String>();
                flag = Character.isDigit(number[x]);
            }
        } else if (number.length == 18) {
            for (int x = 0; x < number.length - 1; x++) {
                if (!flag)
                    return new HashMap<String, String>();
                flag = Character.isDigit(number[x]);
            }
        }
        if (flag && certificateNo.length() == 15) {
            birthday = "19" + certificateNo.substring(6, 8) + "-" + certificateNo.substring(8, 10) + "-"
                    + certificateNo.substring(10, 12);
            sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 3, certificateNo.length()))
                    % 2 == 0 ? "W" : "M";
            age = (year - Integer.parseInt("19" + certificateNo.substring(6, 8))) + "";
        } else if (flag && certificateNo.length() == 18) {
            birthday = certificateNo.substring(6, 10) + "-" + certificateNo.substring(10, 12) + "-"
                    + certificateNo.substring(12, 14);
            sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 4, certificateNo.length() - 1))
                    % 2 == 0 ? "W" : "M";
            age = (year - Integer.parseInt(certificateNo.substring(6, 10))) + "";
        }
        Map<String, String> map = new HashMap <String,String>();
        map.put("birthday", birthday);
        map.put("age", age);
        map.put("sexCode", sexCode);
        return map;
    }
    //  ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
    public static String processFLV(String inputPath) {
/*
      if (!checkfile(inputPath)){
          _log.warn(inputPath+" is not file");
          return false;
         }
*/
        List<String> commend = new java.util.ArrayList<String>();

//        commend.add("e:\\videoconver\\ffmpeg\\ffmpeg ");//可以设置环境变量从而省去这行
        commend.add("ffmpeg");
        commend.add("-i");
        commend.add(inputPath);

        try {

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.redirectErrorStream(true);
            Process p = builder.start();

            //1. start
            BufferedReader buf = null; // 保存ffmpeg的输出结果流
            String line = null;
            //read the standard output

            buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuffer sb = new StringBuffer();
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
                continue;
            }
            int ret = p.waitFor();//这里线程阻塞，将等待外部转换进程运行成功运行结束后，才往下执行
            //1. end
            return sb.toString();
        } catch (Exception e) {
//            System.out.println(e);
            return null;
        }
     }

    public static  InetAddress getLocalHostLANAddress() throws Exception {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            return jdkSuppliedAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    }

