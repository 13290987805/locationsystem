package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.EleCall;
import com.tg.locationsystem.entity.FrenceHistory;
import com.tg.locationsystem.entity.HeartRateHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EleCallMapper extends IBaseDao<EleCall>{
    int deleteByPrimaryKey(Integer id);

    int insert(EleCall record);

    int insertSelective(EleCall record);

    EleCall selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EleCall record);

    int updateByPrimaryKey(EleCall record);

    List<EleCall> geteleCallByKey(String timeuser);
}






