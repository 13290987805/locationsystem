package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.PersonType;
import com.tg.locationsystem.pojo.PersonsByMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PersonTypeMapper extends IBaseDao<PersonType>{
    int deleteByPrimaryKey(Integer id);

    int insert(PersonType record);

    int insertSelective(PersonType record);

    PersonType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PersonType record);

    int updateByPrimaryKey(PersonType record);

    PersonType getPersonTypeByName(@Param("typeName") String typeName, @Param("id") Integer id);

    List<PersonType> getPersonTypeList(Integer id);

    List<PersonType> getPersonsTypeByMsg(@Param("userid") Integer id,@Param("msg") String msg);

    PersonType getPersonTypeByImg(String img);
}