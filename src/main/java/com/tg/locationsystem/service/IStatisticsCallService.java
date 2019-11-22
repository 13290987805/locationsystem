package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.StatisticsCall;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/12
 */

public interface IStatisticsCallService extends IBaseService<StatisticsCall> {
    PageInfo<StatisticsCall> getStatisticsCallByUserIdPage(Integer pageIndex, Integer pageSize, Integer id);

    List<StatisticsCall> getStatisticsCallByUserId(Integer id);
}
