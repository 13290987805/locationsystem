package com.tg.locationsystem.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author hyy
 * @ Date2020/2/27
 */
public class IPUtil {
    private static final int timeOut = 3000; //超时应该在3钞以上

    public static boolean isPing(String ip)
    {
        boolean status = false;
        if(ip != null)
        {
            try
            {
                status = InetAddress.getByName(ip).isReachable(timeOut);
            }
            catch(UnknownHostException e)
            {

            }
            catch(IOException e)
            {

            }
        }
        return status;
    }
    public static boolean isRightIP(String ip) {
        int index,a1,b1;
        boolean bool=true;
        try {
            for(int i=1;i<=3;i++) {
                index = ip.indexOf('.');
                a1=Integer.parseInt(ip.substring(0, index));
                if(a1<0||a1>255) {
                    bool=false;
                    break;
                }
                if(i!=3)
                    ip=ip.substring(index+1);
                else {
                    ip=ip.substring(index+1);
                    b1=Integer.parseInt(ip.substring(0));
                    if(b1<0||b1>255)
                        bool=false;
                }
            }
            return bool;
        }catch (Exception e){
            return false;
        }

    }

}
