package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.MapRule;

import java.util.List;

public interface MapRuleMapper extends IBaseDao<MapRule>{
    int deleteByPrimaryKey(Integer id);

    int insert(MapRule record);

    int insertSelective(MapRule record);

    MapRule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MapRule record);

    int updateByPrimaryKeyWithBLOBs(MapRule record);

    int updateByPrimaryKey(MapRule record);

    int deleteByMapKey(String mapKey);

    List<MapRule> getAllRule();
}