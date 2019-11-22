package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.HeartRateSet;
import com.tg.locationsystem.entity.Myuser;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/5
 */
public interface IHeartRateSetService extends IBaseService<HeartRateSet> {
    HeartRateSet getHeartRateSet(Integer id);

    List<HeartRateSet> getAllHeartRateSet();


}
