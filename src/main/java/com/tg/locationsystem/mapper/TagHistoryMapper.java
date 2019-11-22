package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.TagHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagHistoryMapper extends IBaseDao<TagHistory>{
    int deleteByPrimaryKey(Integer id);

    int insert(TagHistory record);

    int insertSelective(TagHistory record);

    TagHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TagHistory record);

    int updateByPrimaryKey(TagHistory record);

    List<TagHistory> getTagHistoryByAdd(@Param("format")String format, @Param("tagAddress")String tagAddress);

    List<TagHistory> getHistoryByAddAndTime(@Param("format") String format, @Param("tagAddress") String tagAddress, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<TagHistory> getHistoryTest(@Param("format")String format, @Param("address")String add);

    int existTable(String format);

    int createNewTable(@Param("format") String format);

    int inserttagHistory(@Param("format")String format,@Param("tagHistory") TagHistory tagHistory);

    List<TagHistory> getTagHistoryByAddAndMap(@Param("format") String format,@Param("tagAddress") String tagAddress,@Param("mapkey") String mapkey);

    List<TagHistory> getHistoryByAddAndTimeAndMap(@Param("format") String format,@Param("tagAddress") String tagAddress,@Param("startTime") String startTime,@Param("endTime") String endTime, @Param("mapkey") String mapkey);

    int deleteHistory(@Param("tableName") String tableName,@Param("address") String address);

}