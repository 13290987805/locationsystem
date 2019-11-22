package com.tg.locationsystem.utils;

import java.awt.*;

/**
 * @author hyy
 * @ Date2019/11/15
 */
public class RuleUtil {
    /**两【线段】是否相交
     * @param l1x1 线段1的x1
     * @param l1y1 线段1的y1
     * @param l1x2 线段1的x2
     * @param l1y2 线段1的y2
     * @param l2x1 线段2的x1
     * @param l2y1 线段2的y1
     * @param l2x2 线段2的x2
     * @param l2y2 线段2的y2
     * @return 是否相交
     */
    public static boolean intersection(double l1x1, double l1y1, double l1x2, double l1y2,
                                       double l2x1, double l2y1, double l2x2, double l2y2) {
        // 快速排斥实验 首先判断两条线段在 x 以及 y 坐标的投影是否有重合。 有一个为真，则代表两线段必不可交。
        if (Math.max(l1x1, l1x2) < Math.min(l2x1, l2x2)
                || Math.max(l1y1, l1y2) < Math.min(l2y1, l2y2)
                || Math.max(l2x1, l2x2) < Math.min(l1x1, l1x2)
                || Math.max(l2y1, l2y2) < Math.min(l1y1, l1y2)) {
            return false;
        }
        // 跨立实验  如果相交则矢量叉积异号或为零，大于零则不相交
        if ((((l1x1 - l2x1) * (l2y2 - l2y1) - (l1y1 - l2y1) * (l2x2 - l2x1))
                * ((l1x2 - l2x1) * (l2y2 - l2y1) - (l1y2 - l2y1) * (l2x2 - l2x1))) > 0
                || (((l2x1 - l1x1) * (l1y2 - l1y1) - (l2y1 - l1y1) * (l1x2 - l1x1))
                * ((l2x2 - l1x1) * (l1y2 - l1y1) - (l2y2 - l1y1) * (l1x2 - l1x1))) > 0) {
            return false;
        }
        return true;

    }

    public static Point getFoot(Point p1, Point p2, Point p3){
        Point foot=new Point();

        float dx=p1.x-p2.x;
        float dy=p1.y-p2.y;

        float u=(p3.x-p1.x)*dx+(p3.y-p1.y)*dy;
        u/=dx*dx+dy*dy;

        foot.x=(int)(p1.x+u*dx);
        foot.y=(int)(p1.y+u*dy);

        float d=Math.abs((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.x-p2.y));
        float d1=Math.abs((p1.x-foot.x)*(p1.x-foot.x)+(p1.y-foot.y)*(p1.x-foot.y));
        float d2=Math.abs((p2.x-foot.x)*(p2.x-foot.x)+(p2.y-foot.y)*(p2.x-foot.y));

        if(d1>d||d2>d){
            if (d1>d2)  return p2;
            else return p1;
        }

        return foot;
    }
}
