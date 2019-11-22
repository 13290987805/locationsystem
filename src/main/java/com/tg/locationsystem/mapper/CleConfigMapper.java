package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.CleConfig;

public interface CleConfigMapper extends IBaseDao<CleConfig>{
    int deleteByPrimaryKey(Integer id);

    int insert(CleConfig record);

    int insertSelective(CleConfig record);

    CleConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CleConfig record);

    int updateByPrimaryKey(CleConfig record);

    int deletecofigByMapKey(String mapKey);

    CleConfig getConfigByMapKey(String mapKey);
}