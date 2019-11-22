package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.EleCallSet;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/12
 */
public interface IEleCallSetService  extends IBaseService<EleCallSet> {
    EleCallSet getEleCallSetByUserid(Integer id);

    List<EleCallSet> getEleCallSetList();
}
