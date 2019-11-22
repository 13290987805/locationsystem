package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Person;
import com.tg.locationsystem.entity.PersonType;
import com.tg.locationsystem.mapper.PersonMapper;
import com.tg.locationsystem.mapper.PersonTypeMapper;
import com.tg.locationsystem.pojo.PersonVO;
import com.tg.locationsystem.pojo.PersonsByMsg;
import com.tg.locationsystem.service.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Service
public class PersonService extends BaseServiceImpl<Person> implements IPersonService {
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private PersonTypeMapper personTypeMapper;
    @Override
    public IBaseDao<Person> getBaseDao() {
        return personMapper;
    }

    @Override
    public Person getPersonByIdCard(String idCard) {
        return personMapper.getPersonByIdCard(idCard);
    }

    @Override
    public PageInfo<Person> getPersonsByUserId(Integer pageIndex, Integer pageSize, Integer id) {


        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<Person> list = personMapper.getPersonsByUserId(id);


        return new PageInfo<Person>(list,3);
    }

    @Override
    public PageInfo<Person> getPersonsByteTypePage(Integer pageIndex, Integer pageSize, Integer typeid, Integer userid) {

        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<Person> list = personMapper.getPersonsByteType(typeid,userid);

        return new PageInfo<Person>(list,3);
    }

    @Override
    public List<Person> getPersonsByType(Integer typeid, Integer userid) {
        return personMapper.getPersonsByteType(typeid,userid);
    }

    @Override
    public Person getPersonByAddress(Integer id, String address) {

        return personMapper.getPersonByAddress(id,address);
    }

    @Override
    public PageInfo<Person> getPersonsByMsg(Integer pageIndex, Integer pageSize, Integer id, String msg) {

        //设置分页
        PageHelper.startPage(pageIndex, pageSize);

       //人员表查询结果
        List<Person> list = personMapper.getPersonsByMsg(id, msg);

        return new PageInfo<Person>(list,3);
    }

    @Override
    public Person getPersonByImg(String img) {
        return personMapper.getPersonByImg(img);
    }

    @Override
    public Person getPersonByOnlyAddress(String tagAddress) {
        return personMapper.getPersonByOnlyAddress(tagAddress);
    }

}
