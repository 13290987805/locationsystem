package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Map;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MapMapper extends IBaseDao<Map>{
    int deleteByPrimaryKey(Integer id);

    int insert(Map record);

    int insertSelective(Map record);

    Map selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Map record);

    int updateByPrimaryKey(Map record);

    Map getMapByName(@Param("name") String name, @Param("userid") Integer id);

    List<Map> getMapsByUserId(Integer id);

    Map getMapByUuid(String uuid);

    int deleteMapByKey(String mapKey);
}