package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.TagType;
import com.tg.locationsystem.mapper.TagTypeMapper;
import com.tg.locationsystem.service.ITagTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/2
 */
@Service
public class TagTypeService extends BaseServiceImpl<TagType> implements ITagTypeService{
    @Autowired
    private TagTypeMapper tagTypeMapper;
    @Override
    public IBaseDao<TagType> getBaseDao() {

        return tagTypeMapper;
    }

    @Override
    public TagType getTagTypeByName(String name, Integer userid) {

        return tagTypeMapper.getTagTypeByName(name,userid);
    }

    @Override
    public PageInfo<TagType> getTagTypesByUserId(Integer pageIndex, Integer pageSize) {

        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<TagType> list = tagTypeMapper.getTagTypeList();

        return new PageInfo<TagType>(list,3);
    }

    @Override
    public List<TagType> getTagTypes() {

        return tagTypeMapper.getTagTypeList();
    }


}
