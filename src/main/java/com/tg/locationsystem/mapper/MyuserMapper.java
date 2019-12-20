package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Myuser;

public interface MyuserMapper extends IBaseDao<Myuser>{
    int deleteByPrimaryKey(Integer id);

    int insert(Myuser record);

    int insertSelective(Myuser record);

    Myuser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Myuser record);

    int updateByPrimaryKey(Myuser record);

    Myuser getUserByName(String username);
}