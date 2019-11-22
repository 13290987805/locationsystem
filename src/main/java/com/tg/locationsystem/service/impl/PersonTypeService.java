package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Person;
import com.tg.locationsystem.entity.PersonType;
import com.tg.locationsystem.mapper.PersonTypeMapper;
import com.tg.locationsystem.service.IPersonTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/1
 */
@Service
public class PersonTypeService extends BaseServiceImpl<PersonType> implements IPersonTypeService{
    @Autowired
    private PersonTypeMapper personTypeMapper;
    @Override
    public IBaseDao<PersonType> getBaseDao() {
        return personTypeMapper;
    }

    @Override
    public PersonType getPersonTypeByName(String typeName, Integer id) {
        return personTypeMapper.getPersonTypeByName(typeName,id);
    }

    @Override
    public PageInfo<PersonType> getPersonTypesByUserId(Integer pageIndex, Integer pageSize, Integer id) {


        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<PersonType> list = personTypeMapper.getPersonTypeList(id);

        return new PageInfo<PersonType>(list,3);
    }

    @Override
    public List<PersonType> getPersonTypes(Integer id) {
        return personTypeMapper.getPersonTypeList(id);
    }

    @Override
    public PersonType getPersonTypeByImg(String img) {
        return personTypeMapper.getPersonTypeByImg(img);
    }
}
