package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Person;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PersonMapper extends IBaseDao<Person>{
    int deleteByPrimaryKey(Integer id);

    int insert(Person record);

    int insertSelective(Person record);

    Person selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Person record);

    int updateByPrimaryKey(Person record);

    Person getPersonByIdCard(String idCard);

    List<Person> getPersonsByUserId(Integer id);

    List<Person> getPersonsByteType(@Param("typeid") Integer typeid, @Param("userid")Integer userid);

    Person getPersonByAddress(@Param("userid")Integer id,@Param("address") String address);

    Person getPersonByOnlyAddress(String address);

    List<Person> getPersonsByMsg(@Param("userid")Integer id,@Param("msg") String msg);

    List<Person> getPersonsBypersonTypeId(@Param("userid")Integer id, @Param("typeid")Integer id1);

    Person getPersonByImg(String img);

    List<Person> getPersonsByName(@Param("userid") Integer userid,@Param("personName") String personName);


    Person getPersonByPersonIdCard(String idCard);

    List<Person> getPersonsByNoTag(Integer id);
}