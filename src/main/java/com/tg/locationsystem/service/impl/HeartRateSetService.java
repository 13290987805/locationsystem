package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.GoodsType;
import com.tg.locationsystem.entity.HeartRateSet;
import com.tg.locationsystem.mapper.HeartRateSetMapper;
import com.tg.locationsystem.service.IHeartRateSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/5
 */
@Service
public class HeartRateSetService extends BaseServiceImpl<HeartRateSet> implements IHeartRateSetService{
    @Autowired
    private HeartRateSetMapper heartRateSetMapper;

    @Override
    public IBaseDao<HeartRateSet> getBaseDao() {
        return heartRateSetMapper;
    }

    @Override
    public HeartRateSet getHeartRateSet(Integer id) {
        return heartRateSetMapper.getHeartRateSet(id) ;
    }

    @Override
    public List<HeartRateSet> getAllHeartRateSet() {
        return heartRateSetMapper.getAllHeartRateSet();
    }


}
