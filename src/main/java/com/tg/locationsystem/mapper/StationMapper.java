package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Station;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StationMapper extends IBaseDao<Station>{
    int deleteByPrimaryKey(Integer id);

    int insert(Station record);

    int insertSelective(Station record);

    Station selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Station record);

    int updateByPrimaryKey(Station record);

    Station getStationByAddress(@Param("address") String address, @Param("id")Integer id);

    List<Station> getStationsByUserId(Integer id);

    List<Station> getStationByMapId(Integer id);

    List<Station> getStationsByMapKey(String mapkey);

    List<Station> getOnlineStationsByMapKey(Integer id);

    List<Station> getAllStationList();
}


























