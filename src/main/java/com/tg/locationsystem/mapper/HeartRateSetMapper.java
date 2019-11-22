package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.HeartRateSet;

import java.util.List;

public interface HeartRateSetMapper extends IBaseDao<HeartRateSet>{
    int deleteByPrimaryKey(Integer id);

    int insert(HeartRateSet record);

    int insertSelective(HeartRateSet record);

    HeartRateSet selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HeartRateSet record);

    int updateByPrimaryKey(HeartRateSet record);

    HeartRateSet getHeartRateSet(Integer id);

    List<HeartRateSet> getAllHeartRateSet();


}