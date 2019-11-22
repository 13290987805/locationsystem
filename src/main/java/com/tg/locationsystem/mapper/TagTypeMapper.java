package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Station;
import com.tg.locationsystem.entity.TagHistory;
import com.tg.locationsystem.entity.TagStatus;
import com.tg.locationsystem.entity.TagType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagTypeMapper extends IBaseDao<TagType>{
    int deleteByPrimaryKey(Integer id);

    int insert(TagType record);

    int insertSelective(TagType record);

    TagType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TagType record);

    int updateByPrimaryKey(TagType record);

    TagType getTagTypeByName(@Param("typename") String name,@Param("userid") Integer userid);

    List<TagType> getTagTypeList();
}

























