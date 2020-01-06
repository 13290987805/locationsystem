package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Frence;
import com.tg.locationsystem.pojo.FrenceVO;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public interface IFrenceService extends IBaseService<Frence> {
    Frence getFrenceByname(String name, Integer id);


    List<Frence> getfrenceByUserId(Integer id);

    List<Frence> getFrenceList();

    PageInfo<Frence> getfrenceByUserId(Integer pageIndex, Integer pageSize, Integer id);

    PageInfo<Frence> getFrenceByConditionPage(Integer pageIndex, Integer pageSize, Integer id, String msg);


    int setSwitch(Integer id, String setSwitch);
}
