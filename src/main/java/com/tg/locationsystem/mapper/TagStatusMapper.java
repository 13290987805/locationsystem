package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.TagStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagStatusMapper extends IBaseDao<TagStatus>{
    int deleteByPrimaryKey(Integer id);

    int insert(TagStatus record);

    int insertSelective(TagStatus record);

    TagStatus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TagStatus record);

    int updateByPrimaryKey(TagStatus record);

    List<TagStatus> getTagStatusByUserId(@Param("userid") Integer id);

    List<TagStatus> getTagStatusByType(@Param("userid") Integer id, @Param("typeid") String typeid);

    List<TagStatus> getTagStatusByAddress(String tagAdd);

    List<TagStatus> getTagStatusByCondition(@Param("userid")Integer id, @Param("msg")String msg,@Param("typeid")String typeid);

    int deletetagStatus(@Param("tableName")String tableName,@Param("address") String address);

    List<TagStatus> getAllTagStatusByDeal(@Param("userid") Integer id,@Param("typeid") String typeid,@Param("isdeal") String isdeal);

    List<TagStatus> getTagStatusByTypeId(@Param("userid") Integer id, @Param("typeid") String typeid);
}