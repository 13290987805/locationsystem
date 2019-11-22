package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.EleCall;
import com.tg.locationsystem.entity.EleCallSet;

import java.util.List;

public interface EleCallSetMapper extends IBaseDao<EleCallSet>{
    int deleteByPrimaryKey(Integer id);

    int insert(EleCallSet record);

    int insertSelective(EleCallSet record);

    EleCallSet selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EleCallSet record);

    int updateByPrimaryKey(EleCallSet record);

    EleCallSet getEleCallSetByUserid(Integer id);

    List<EleCallSet> getEleCallSetList();
}











