package com.tg.locationsystem.entity;

import java.io.Serializable;

public class MapRule implements Serializable{
    private Integer id;

    private String mapKey;

    private String mapRule;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey == null ? null : mapKey.trim();
    }

    public String getMapRule() {
        return mapRule;
    }

    public void setMapRule(String mapRule) {
        this.mapRule = mapRule == null ? null : mapRule.trim();
    }

    @Override
    public String toString() {
        return "MapRule{" +
                "id=" + id +
                ", mapKey='" + mapKey + '\'' +
                ", mapRule='" + mapRule + '\'' +
                '}';
    }
}