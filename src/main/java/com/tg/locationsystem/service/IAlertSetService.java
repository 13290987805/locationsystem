package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.AlertSet;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/12/18
 */
public interface IAlertSetService extends IBaseService<AlertSet> {
    List<AlertSet> getAllSetList();

    AlertSet getAlertSetByUserId(Integer id);
}
