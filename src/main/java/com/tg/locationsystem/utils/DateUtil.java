package com.tg.locationsystem.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author hyy
 * @ Date2019/7/3
 */
public class DateUtil {
    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     *
     * <p>Description: 本地时间转化为UTC时间</p>
     * @param localTime
     * @return
     * @author wgs
     * @date  2018年10月19日 下午2:23:43
     *
     */
    public static Date localToUTC(Date localTime) {
        long localTimeInMillis=localTime.getTime();
        /** long时间转换成Calendar */
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(localTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date utcDate=new Date(calendar.getTimeInMillis());
        return utcDate;
    }

    /**
     *
     * <p>Description:UTC时间转化为本地时间 </p>
     * @param utcTime
     * @return
     * @author wgs
     * @date  2018年10月19日 下午2:23:24
     *
     */
    public static Date utcToLocal(Date utcTime){
        long utcTimeInMillis = utcTime.getTime();
        /** long时间转换成Calendar */
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(utcTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(java.util.Calendar.MILLISECOND, +(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date localDate=new Date(calendar.getTimeInMillis());
        return localDate;
    }
}
