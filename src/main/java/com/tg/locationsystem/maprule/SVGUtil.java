package com.tg.locationsystem.maprule;

import com.google.gson.Gson;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * SVG工具类
 * @author liwenchang
 *
 */
public class SVGUtil {

    /**
     * 输入SVG文件地址返回解析的区域集合
     * @param svgURL
     * @return 区域集合
     */
    public static String readSVG(String svgURL){
        List<PolygonArea> areas = new ArrayList<>();

        SAXReader reader = new SAXReader(false);
        //取消DTD验证 加快解析速度
       /* try {
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (SAXException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }*/
        // 忽略DTD，降低延迟
       // reader.setEntityResolver(new IgnoreDTDEntityResolver());
        reader.setEntityResolver(new IgnoreDTDEntityResolver());

        File file = new File(svgURL);
        //读文件
        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        //获取根节点
        Element root = document.getRootElement();
        //获取子节点
        List<Element> childElements = root.elements();
        for (Element child : childElements) {
            // 遍历不可见图层
            if ("g".equals(child.getQName().getName()) && "none".equals(child.attributeValue("display"))) {
//				System.out.println(child.asXML());
                List<Element> gElements = child.elements();
                for (Element gEle : gElements) {
                    if ("g".equals(gEle.getQName().getName())) {
                        List<Element> gChildElements = gEle.elements();

                        PolygonArea area = new PolygonArea();
                        List<Line> gates = new ArrayList<>();
                        List<Line> walls = new ArrayList<>();
                        for (Element gChEle : gChildElements) {
                            //获取多边形线段
                            if ("polygon".equals(gChEle.getQName().getName())) {
                                String points = gChEle.attributeValue("points");
                                walls = SVGUtil.getPolygonLine(points);
                            }
                            //获取只含直线的曲线线段
                            if ("polyline".equals(gChEle.getQName().getName())) {
                                String points = gChEle.attributeValue("points");
                                walls = SVGUtil.getPolylineLine(points);
                            }
                            //获取门线段
                            if ("line".equals(gChEle.getQName().getName())) {
                                double x1 = Double.parseDouble(gChEle.attributeValue("x1"));
                                double y1 = Double.parseDouble(gChEle.attributeValue("y1"));
                                double x2 = Double.parseDouble(gChEle.attributeValue("x2"));
                                double y2 = Double.parseDouble(gChEle.attributeValue("y2"));
                                gates.add(new Line(x1,y1,x2,y2));
                            }
                            //获取墙线段
                            if ("g".equals(gChEle.getQName().getName())) {
                                List<Element> es = gChEle.elements();
                                for (Element e : es) {
                                    if ("rect".equals(e.getQName().getName())) {
                                        double x = Double.parseDouble(e.attributeValue("x"));
                                        double y = Double.parseDouble(e.attributeValue("y"));
                                        double width = Double.parseDouble(e.attributeValue("width"));
                                        double height = Double.parseDouble(e.attributeValue("height"));
                                        walls = SVGUtil.getRectLine(x, y, width, height);
                                    }
                                    if ("polyline".equals(e.getQName().getName())) {
                                        String points = e.attributeValue("points");
                                        walls = SVGUtil.getPolylineLine(points);
                                    }
                                    if ("polygon".equals(e.getQName().getName())) {
                                        String points = e.attributeValue("points");
                                        walls = SVGUtil.getPolygonLine(points);
                                    }

                                }
                            }

                        }

                        area.setWalls(walls);
                        area.setGates(gates);
                        //区域墙不为空 门为空 设为不可进入区域
                        if( !area.getGates().isEmpty() ) {
                            area.setEnterable(true);
                        }
                        areas.add(area);

                    }
                }
            }
        }
        //包装成json字符串
        Gson gson = new Gson();
        return gson.toJson(areas);
    }
    /**
     * 获取矩形的四条边的坐标
     * @param x 顶点x坐标
     * @param y	顶点y坐标
     * @param width	矩形宽度
     * @param height 矩形高度
     * @return 包含四条线的集合
     */
    public static List<Line> getRectLine(double x,double y,double width,double height) {

        List<Line> list = new ArrayList<>();
        list.add(new Line(x,y,x+width,y));
        list.add(new Line(x+width,y,x+width,y+height));
        list.add(new Line(x+width,y+height,x,y+height));
        list.add(new Line(x,y+height,x,y));

        return list;
    }

    /**
     * 获取多边形的边
     * @param points 点的集合
     * @return 包含多边形所有边的集合
     */
    public static List<Line> getPolygonLine(String points){
        points = points.trim();
        String[] sArray = points.split("[\\s]+");
        List<Line> list = new ArrayList<>();
        for(int i = 0;i<sArray.length;i++) {
            double x1 = Double.parseDouble(sArray[i].split(",")[0]);
            double y1 = Double.parseDouble(sArray[i].split(",")[1]);
            double x2=0,y2=0;
            if(i+1 != sArray.length) {
                x2 = Double.parseDouble(sArray[i+1].split(",")[0]);
                y2 = Double.parseDouble(sArray[i+1].split(",")[1]);
            }else {
                x2 = Double.parseDouble(sArray[0].split(",")[0]);
                y2 = Double.parseDouble(sArray[0].split(",")[1]);
            }

            list.add(new Line(x1,y1,x2,y2));
        }
        return list;
    }

    /**
     * 获取只含直线的曲线的边
     * @param points 点的集合
     * @return 包含曲线所有边的集合
     */
    public static List<Line> getPolylineLine(String points){
        points = points.trim();
        String[] sArray = points.split("[\\s]+");
        List<Line> list = new ArrayList<>();
        for(int i = 0;i<sArray.length;i++) {
            double x1 = Double.parseDouble(sArray[i].split(",")[0]);
            double y1 = Double.parseDouble(sArray[i].split(",")[1]);
            double x2=0,y2=0;
            if(i+1 != sArray.length) {
                x2 = Double.parseDouble(sArray[i+1].split(",")[0]);
                y2 = Double.parseDouble(sArray[i+1].split(",")[1]);
                list.add(new Line(x1,y1,x2,y2));
            }
        }
        return list;
    }


}