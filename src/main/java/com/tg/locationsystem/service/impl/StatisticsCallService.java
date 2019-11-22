package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Goods;
import com.tg.locationsystem.entity.StatisticsCall;
import com.tg.locationsystem.mapper.StatisticsCallMapper;
import com.tg.locationsystem.service.IStatisticsCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/12
 */
@Service
public class StatisticsCallService extends BaseServiceImpl<StatisticsCall> implements IStatisticsCallService{
    @Autowired
    private StatisticsCallMapper statisticsCallMapper;
    @Override
    public IBaseDao<StatisticsCall> getBaseDao() {
        return statisticsCallMapper;
    }

    @Override
    public PageInfo<StatisticsCall> getStatisticsCallByUserIdPage(Integer pageIndex, Integer pageSize, Integer id) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<StatisticsCall> list = statisticsCallMapper.getStatisticsCallByUserId(id);

        return new PageInfo<StatisticsCall>(list,3);
    }

    @Override
    public List<StatisticsCall> getStatisticsCallByUserId(Integer id) {
        return statisticsCallMapper.getStatisticsCallByUserId(id);
    }
}
