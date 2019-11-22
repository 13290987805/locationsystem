package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.TagTest;

public interface TagTestMapper extends IBaseDao<TagTest>{
    int deleteByPrimaryKey(Integer id);

    int insert(TagTest record);

    int insertSelective(TagTest record);

    TagTest selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TagTest record);

    int updateByPrimaryKey(TagTest record);
}