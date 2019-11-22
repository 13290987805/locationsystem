package com.tg.locationsystem.maprule;

public class Line {
	private double p1x;
	private double p1y;
	private double p2x;
	private double p2y;
	
	public Line() {}
	public Line(double p1x,double p1y,double p2x,double p2y) {
		this.p1x = p1x;
		this.p1y = p1y;
		this.p2x = p2x;
		this.p2y = p2y;
	}
	public double getP1x() {
		return p1x;
	}
	public void setP1x(double p1x) {
		this.p1x = p1x;
	}
	public double getP1y() {
		return p1y;
	}
	public void setP1y(double p1y) {
		this.p1y = p1y;
	}
	public double getP2x() {
		return p2x;
	}
	public void setP2x(double p2x) {
		this.p2x = p2x;
	}
	public double getP2y() {
		return p2y;
	}
	public void setP2y(double p2y) {
		this.p2y = p2y;
	}
	@Override
	public String toString() {
		return "Line [p1x=" + p1x + ", p1y=" + p1y + ", p2x=" + p2x + ", p2y=" + p2y + "]";
	}
	
	
}
