package com.tg.locationsystem.pojo;

import java.util.List;

public class Area {
	private String key;
	private boolean enterable;
	private List<Area> subArea;
	public boolean isEnterable() {
		return enterable;
	}
	public void setEnterable(boolean enterable) {
		this.enterable = enterable;
	}
	public List<Area> getSubArea() {
		return subArea;
	}
	public void setSubArea(List<Area> subArea) {
		this.subArea = subArea;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
