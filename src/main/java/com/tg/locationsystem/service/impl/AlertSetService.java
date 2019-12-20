package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.AlertSet;
import com.tg.locationsystem.mapper.AlertSetMapper;
import com.tg.locationsystem.service.IAlertSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/12/18
 */
@Service
public class AlertSetService extends BaseServiceImpl<AlertSet> implements IAlertSetService{
    @Autowired
    private AlertSetMapper alertSetMapper;

    @Override
    public IBaseDao<AlertSet> getBaseDao() {
        return alertSetMapper;
    }

    @Override
    public List<AlertSet> getAllSetList() {
        return alertSetMapper.getAllSetList();
    }

    @Override
    public AlertSet getAlertSetByUserId(Integer id) {
        return alertSetMapper.getAlertSetByUserId(id);
    }
}
