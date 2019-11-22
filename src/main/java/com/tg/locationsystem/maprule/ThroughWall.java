package com.tg.locationsystem.maprule;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.locationsystem.entity.Tag;

public class ThroughWall {
	static Map<String, PolygonArea> curAreaMap = new ConcurrentHashMap<String, PolygonArea>();
	static Map<String, Tag> preTagMap = new ConcurrentHashMap<String, Tag>();
	static Map<String, Integer> flagCountMap = new ConcurrentHashMap<String, Integer>();
	//判断两线是否相交,用于判断穿墙
	/**
	 * @param l1 线段1
	 * @param l2 线段2
	 * @return 是则相交，否则不交
	 */
	public static boolean intersection(Line l1,Line l2) {
		// 快速排斥实验 首先判断两条线段在 x 以及 y 坐标的投影是否有重合。 有一个为真，则代表两线段必不可交。
		if (Math.max(l1.getP1x(), l1.getP2x()) < Math.min(l2.getP1x(), l2.getP2x()) ||
				Math.max(l1.getP1y(), l1.getP2y()) < Math.min(l2.getP1y(), l2.getP2y()) ||
				Math.max(l2.getP1x(), l2.getP2x()) < Math.min(l1.getP1x(), l2.getP1x()) ||
				Math.max(l2.getP1y(), l2.getP2y()) < Math.min(l1.getP1y(), l1.getP2y())) {
			return false;
		}
		// 跨立实验  如果相交则矢量叉积异号或为零，大于零则不相交
		//线段1的P1和线段2的P1组成向量 叉乘 线段1向量
		double acab = ((l1.getP1x()-l2.getP1x())*(l1.getP1y()-l1.getP2y()))-((l1.getP1y()-l2.getP1y())*(l1.getP1x()-l1.getP2x()));
		//线段1的P1和线段2的P2组成向量 叉乘 线段1向量
		double adab = ((l1.getP1x()-l2.getP2x())*(l1.getP1y()-l1.getP2y()))-((l1.getP1y()-l2.getP2y())*(l1.getP1x()-l1.getP2x()));
		//线段2的P1和线段1的P1组成向量 叉乘 线段2向量
		double cacd = ((l2.getP1x()-l1.getP1x())*(l2.getP1y()-l2.getP2y()))-((l2.getP1y()-l1.getP1y())*(l2.getP1x()-l2.getP2x()));
		//线段2的P1和线段1的P2组成向量 叉乘 线段2向量
		double cbcd = ((l2.getP1x()-l1.getP2x())*(l2.getP1y()-l2.getP2y()))-((l2.getP1y()-l1.getP2y())*(l2.getP1x()-l2.getP2x()));

		if (acab * adab > 0 || cacd * cbcd > 0) {
			return false;
		}
		return true;
	}

	//点到线段的距离
	/**
	 * @param x 点x坐标
	 * @param y 点y坐标
	 * @param line 线段
	 * @return 点到线段的距离
	 */
	public static double distancePtSeg(double x,double y,Line line) {
		double x1 = line.getP1x();
		double y1 = line.getP1y();
		double x2 = line.getP2x();
		double y2 = line.getP2y();
		double cross = (x2 - x1) * (x - x1) + (y2 - y1) * (y - y1);
		if (cross <= 0)
			return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
		double d2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
		if (cross >= d2)
			return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
		double r = cross / d2;
		double px = x1 + (x2 - x1) * r;
		double py = y1 + (y2 - y1) * r;
		return Math.sqrt((x - px) * (x - px) + (py - y1) * (py - y1));

	}

	//判断墙是否属于门
	/**
	 * 判断门是否属于墙
	 * @param door 门
	 * @param wall 墙
	 * @return 门属于墙，否则不属于
	 */
	public static boolean doorInWall(Line door,Line wall) {
		double x = (door.getP1x()+door.getP2x())/2;
		double y = (door.getP1y()+door.getP2y())/2;
		return distancePtSeg(x, y, wall) < 0.05;
	}


	//判断点是否在线上
	/**
	 * @param tag 当前坐标
	 * @param line 墙
	 * @return 是否下一次点是在墙上
	 */
	public static boolean onsegment(Tag tag,Line line) {
		boolean flag1 = (tag.getX()-line.getP1x())*(line.getP2y()-line.getP1y())==(tag.getY()-line.getP1y())*(line.getP2x()-line.getP1x());
		return flag1&&Math.min(line.getP1x(),line.getP2x())<=tag.getX()&&tag.getX()<=Math.max(line.getP1x(),line.getP2x())&&Math.min(line.getP1y(),line.getP2y())<=tag.getY()&&tag.getY()<=Math.max(line.getP1y(),line.getP2y());
	}
	//判断点是否在某区域内
	/**
	 * @param tag 点
	 * @param area 折线闭合区域
	 * @return 是则在，否则不在
	 */
	public static boolean inArea(Tag tag,PolygonArea area) {
		List<Line> lines = area.getWalls();
		int intersectCount = 0;
		Line radial = new Line(tag.getX(),tag.getY(),9999,9999);
		//判断点的射线与多边形各边相交次数，为奇则交，偶则不交
		for (Line line : lines) {
			//点在线上，则视为在区域内
			if (onsegment(tag, line)) {
				return true;
			}
			if (intersection(radial, line)) {
				intersectCount++;
			}
		}
		if (intersectCount%2 == 0) {
			return false;
		}
		return true;
	}
	//查找点在哪个区域
	/**
	 * @param tag 点
	 * @param areas 所有区域列表
	 * @return 点所在区域对象
	 */
	public static PolygonArea inWhere(Tag tag,List<PolygonArea> areas) {
		for (PolygonArea area : areas) {
			if (inArea(tag, area)) {
				return area;
			}
		}
		return null;
	}
	//判断点是否在门附近
	/**
	 * @param tag 点
	 * @param door 门（也是线段）
	 * @return 是则在，否则不在
	 */
	public static boolean nearDoor(Tag tag,Line door) {
		double x = tag.getX();
		double y = tag.getY();
		return distancePtSeg(x, y, door) < 0.5;

	}
	//穿墙调整
	/**
	 * @param preTag 缓存中的点的
	 * @param nowTag 当前的点
	 * @param area 缓存中的当前区域
	 * @return 修整后的点，然后存进缓存
	 */
	public static Tag preventThroughWall(Tag preTag,Tag nowTag,PolygonArea area,List<PolygonArea> allArea) {
		//判断是不是该标签的第一个点
		if (area == null || preTag == null) {
			preTagMap.put(nowTag.getAddress(), nowTag);
			PolygonArea curArea = inWhere(nowTag, allArea);
			if (curArea != null) {
				curAreaMap.put(nowTag.getAddress(), curArea);
			}
			flagCountMap.put(nowTag.getAddress(), 0);
			return nowTag;
		}
		//没穿墙就不调整
		if (inArea(nowTag, area)) {
			//把新点存进缓存
			preTagMap.put(nowTag.getAddress(), nowTag);
			//把flag设置为0
			flagCountMap.put(nowTag.getAddress(), 0);
			return nowTag;
		}else {
			PolygonArea area2 = inWhere(nowTag, allArea);
			System.err.println(area + "穿墙：" + area2);
			//判断区域是否可进入
			if (area2 == null || !area2.isEnterable()) {
				System.err.println("不可进");
				return preTag;
			}
			int flagCount = flagCountMap.get(nowTag.getAddress());
			flagCount++;
			if (flagCount > 30 && Math.sqrt((nowTag.getX()-preTag.getX())*(nowTag.getX()-preTag.getX()) + (nowTag.getY()-preTag.getY())*(nowTag.getY()-preTag.getY()))>1.2) {
				flagCount = 0;
				//把新点存进缓存
				preTagMap.put(nowTag.getAddress(), nowTag);
				curAreaMap.put(nowTag.getAddress(), area2);
				System.err.println("次数过多");
				return nowTag;
			}
			flagCountMap.put(nowTag.getAddress(), flagCount);

			//判断穿了哪堵墙，然后判断墙上是否存在门，在判断点是否在门附近
			//若在门附近在调整点落在门的中点，并把所在区域调整成新区域;否则调整点落在实际点在墙上投影的十分之九处
			List<Line> walls = area.getWalls();
			Line forecastLine = new Line(preTag.getX(), preTag.getY(), nowTag.getX(), nowTag.getY());
			for (Line line : walls) {
				if (intersection(forecastLine, line)) {
					//穿墙后判断附近是否有门，若有门则把下一个点定在门的中点，并把所在区域调整成新区域
					List<Line> doors = area.getGates();
					for (Line door : doors) {
						if (doorInWall(door, line) && nearDoor(preTag, door)) {
							double x = (door.getP1x()+door.getP2x())/2;
							double y = (door.getP1y()+door.getP2y())/2;
							Tag reTag = new Tag(x, y);
							reTag.setAddress(nowTag.getAddress());
							//修改当前区域为nowTag所在的区域
							curAreaMap.put(nowTag.getAddress(), area2);
							//把新点存进缓存
							preTagMap.put(nowTag.getAddress(), reTag);
							//把flag设置为0
							flagCountMap.put(nowTag.getAddress(), 0);
							System.err.println("过门");
							return reTag;
						}
					}
					double m = nowTag.getX();
					double n = nowTag.getY();
					double x1 = line.getP1x();
					double x2 = line.getP2x();
					double y1 = line.getP1y();
					double y2 = line.getP2y();
					double x3 = (m*(x2-x1)*(x2-x1)+n*(y2-y1)*(x2-x1)+(x1*y2-x2*y1)*(y2-y1))/((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
					double y3 = (m*(x2-x1)*(y2-y1)+n*(y2-y1)*(y2-y1)+(x2*y1-x1*y2)*(x2-x1))/((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
					double x = preTag.getX()+9*(x3-preTag.getX())/10;
					double y = preTag.getY()+9*(y3-preTag.getY())/10;

					Tag reTag = new Tag(x, y);
					reTag.setAddress(nowTag.getAddress());
					//判断修正后的点是否在区域内，若不在则返回前一个点
					if (!inArea(reTag, area)) {
						return preTag;
					}
					//把新点存进缓存
					preTagMap.put(nowTag.getAddress(), reTag);
					System.err.println("不过门");
					return reTag;
				}
			}
		}
		return nowTag;
	}
	//入口方法
	public static Tag getTagByRule(Tag thisTag, String mapStr) {
		Gson gson = new Gson();
		//解析string成List<PolygonArea>
		List<PolygonArea> allArea = gson.fromJson(mapStr, new TypeToken<List<PolygonArea>>(){}.getType());
		Tag preTag = preTagMap.get(thisTag.getAddress());
		PolygonArea curArea = curAreaMap.get(thisTag.getAddress());
		return preventThroughWall(preTag, thisTag, curArea, allArea);

	}
}
