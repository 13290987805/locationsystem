package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.HeartRateHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HeartRateHistoryMapper extends IBaseDao<HeartRateHistory>{
    int deleteByPrimaryKey(Integer id);

    int insert(HeartRateHistory record);

    int insertSelective(HeartRateHistory record);

    HeartRateHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HeartRateHistory record);

    int updateByPrimaryKey(HeartRateHistory record);

    List<HeartRateHistory> getheartRateHistoryByCondition(@Param("address") String address, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<HeartRateHistory> getSomeheartRateHistory(String s);

    int deleteHistory(@Param("tableName") String tableName,@Param("address") String address);


}