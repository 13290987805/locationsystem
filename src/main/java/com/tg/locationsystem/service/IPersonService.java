package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Person;
import com.tg.locationsystem.entity.PersonType;
import com.tg.locationsystem.pojo.PersonVO;
import com.tg.locationsystem.pojo.PersonsByMsg;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public interface IPersonService extends IBaseService<Person>{
    Person getPersonByIdCard(String idCard);

    PageInfo<Person> getPersonsByUserId(Integer pageIndex, Integer pageSize, Integer id);

    PageInfo<Person> getPersonsByteTypePage(Integer pageIndex, Integer pageSize, Integer typeid, Integer id);

    List<Person> getPersonsByType(Integer typeid, Integer id);

    Person getPersonByAddress(Integer id, String address);

    PageInfo<Person> getPersonsByMsg(Integer pageIndex, Integer pageSize, Integer id, String msg);

    Person getPersonByImg(String img);

    Person getPersonByOnlyAddress(String tagAddress);



}
