package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.EleCall;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/12
 */
public interface IEleCallService extends IBaseService<EleCall> {

    PageInfo<EleCall> geteleCallByKeyPage(Integer pageIndex, Integer pageSize, String timeuser);

    List<EleCall> geteleCallByKey(String timeuser);
}
