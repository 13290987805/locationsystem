package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.TagTest;
import com.tg.locationsystem.entity.TagType;
import com.tg.locationsystem.mapper.TagTestMapper;
import com.tg.locationsystem.service.ITagTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hyy
 * @ Date2019/9/26
 */
@Service
public class TagTestService extends BaseServiceImpl<TagTest> implements ITagTestService{
    @Autowired
    private TagTestMapper tagTestMapper;
    @Override
    public IBaseDao<TagTest> getBaseDao() {
        return tagTestMapper;
    }
}
