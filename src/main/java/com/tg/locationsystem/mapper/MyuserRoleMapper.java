package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.MyuserRole;


import java.util.Set;

public interface MyuserRoleMapper extends IBaseDao<MyuserRole>{
    int deleteByPrimaryKey(Integer id);

    int insert(MyuserRole record);

    int insertSelective(MyuserRole record);

    MyuserRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MyuserRole record);

    int updateByPrimaryKey(MyuserRole record);

    Set<MyuserRole> getMyuserRoleSet(String username);
}