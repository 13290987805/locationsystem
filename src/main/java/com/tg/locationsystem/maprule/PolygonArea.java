package com.tg.locationsystem.maprule;


import java.util.List;

public class PolygonArea{
	private List<Line> walls;
	private List<Line> gates;
	private boolean enterable;
	public PolygonArea() {}
	
	public PolygonArea(List<Line> walls, List<Line> gates) {
		super();
		this.walls = walls;
		this.gates = gates;
	}
	public List<Line> getWalls() {
		return walls;
	}
	public void setWalls(List<Line> walls) {
		this.walls = walls;
	}
	public List<Line> getGates() {
		return gates;
	}
	public void setGates(List<Line> gates) {
		this.gates = gates;
	}
	public boolean isEnterable() {
		return enterable;
	}

	public void setEnterable(boolean enterable) {
		this.enterable = enterable;
	}

	/*@Override
	public String toString() {
		return "PolygonArea [walls=" + walls + ", gates=" + gates + ", enterable=" + enterable + "]";
	}*/
	
}
