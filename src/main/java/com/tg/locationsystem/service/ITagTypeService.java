package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.TagType;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/1
 */
public interface ITagTypeService extends IBaseService<TagType> {
    TagType getTagTypeByName(String name, Integer id);

    PageInfo<TagType> getTagTypesByUserId(Integer pageIndex, Integer pageSize);

    List<TagType> getTagTypes();


}
