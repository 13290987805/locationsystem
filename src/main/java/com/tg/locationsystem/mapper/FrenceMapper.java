package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Frence;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FrenceMapper extends IBaseDao<Frence>{
    int deleteByPrimaryKey(Integer id);

    int insert(Frence record);

    int insertSelective(Frence record);

    Frence selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Frence record);

    int updateByPrimaryKey(Frence record);

    Frence getFrenceByname(@Param("name") String name, @Param("userid") Integer id);

    List<Frence> getfrenceByUserId(Integer id);

    List<Frence> getFrenceList();

    List<Frence> getFrenceByCondition(@Param("userid") Integer id,@Param("msg") String msg);


    int setSwitch(@Param("userid") Integer id,@Param("setSwitch") String setSwitch);
}