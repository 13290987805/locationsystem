package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.FrenceHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FrenceHistoryMapper extends IBaseDao<FrenceHistory>{
    int deleteByPrimaryKey(Integer id);

    int insert(FrenceHistory record);

    int insertSelective(FrenceHistory record);

    FrenceHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FrenceHistory record);

    int updateByPrimaryKey(FrenceHistory record);

    List<FrenceHistory> getFrenceHistoryList(Integer userid);

    List<FrenceHistory> getFrenceHistory(@Param("frenceid") Integer frenceid, @Param("userid") Integer userid);

    int deleteHistory(@Param("tableName") String tableName,@Param("address") String address);

    int deleteHistoryByFrenceId(Integer frenceid);

    List<FrenceHistory> getFrenceHistoryByPersonIdCard(@Param("userid") Integer id,@Param("idCard") String idCard);

    List<FrenceHistory> getFrenceHistorysByPersonIdCard(@Param("userid") Integer id, @Param("idCard") String idCard);

    List<FrenceHistory> getFrenceHistoryByPersonIdCardss(@Param("list") List<String> list);

    List<FrenceHistory> getFrenceHistoryByFrenceIdAndPersonName(@Param("frenceId")Integer frenceId, @Param("list")List<String> list);


}