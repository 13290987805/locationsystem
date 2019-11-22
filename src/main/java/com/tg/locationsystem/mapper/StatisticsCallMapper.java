package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.StatisticsCall;

import java.util.List;

public interface StatisticsCallMapper extends IBaseDao<StatisticsCall>{
    int deleteByPrimaryKey(Integer id);

    int insert(StatisticsCall record);

    int insertSelective(StatisticsCall record);

    StatisticsCall selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StatisticsCall record);

    int updateByPrimaryKey(StatisticsCall record);

    List<StatisticsCall> getStatisticsCallByUserId(Integer id);
}