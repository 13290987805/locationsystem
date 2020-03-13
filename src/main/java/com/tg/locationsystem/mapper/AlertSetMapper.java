package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.AlertSet;
import com.tg.locationsystem.entity.Camera;
import com.tg.locationsystem.entity.Frence;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlertSetMapper extends IBaseDao<AlertSet>{
    int deleteByPrimaryKey(Integer id);

    int insert(AlertSet record);

    int insertSelective(AlertSet record);

    AlertSet selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlertSet record);

    int updateByPrimaryKey(AlertSet record);

    List<AlertSet> getAllSetList();

    AlertSet getAlertSetByUserId(Integer id);
}



























