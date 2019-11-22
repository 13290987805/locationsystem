package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.EleCall;
import com.tg.locationsystem.entity.StatisticsCall;
import com.tg.locationsystem.mapper.EleCallMapper;
import com.tg.locationsystem.service.IEleCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/12
 */
@Service
public class EleCallService extends BaseServiceImpl<EleCall> implements IEleCallService {
    @Autowired
    private EleCallMapper eleCallMapper;
    @Override
    public IBaseDao<EleCall> getBaseDao() {
        return eleCallMapper;
    }


    @Override
    public PageInfo<EleCall> geteleCallByKeyPage(Integer pageIndex, Integer pageSize, String timeuser) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<EleCall> list = eleCallMapper.geteleCallByKey(timeuser);

        return new PageInfo<EleCall>(list,3);
    }

    @Override
    public List<EleCall> geteleCallByKey(String timeuser) {
        return  eleCallMapper.geteleCallByKey(timeuser);
    }
}
