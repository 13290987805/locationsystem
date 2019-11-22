package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.PersonType;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/1
 */
public interface IPersonTypeService extends IBaseService<PersonType> {
    PersonType getPersonTypeByName(String typeName, Integer id);

    PageInfo<PersonType> getPersonTypesByUserId(Integer pageIndex, Integer pageSize, Integer id);

    List<PersonType> getPersonTypes(Integer id);

    PersonType getPersonTypeByImg(String img);
}
