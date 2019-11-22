package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.EleCallSet;
import com.tg.locationsystem.mapper.EleCallSetMapper;
import com.tg.locationsystem.service.IEleCallSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/12
 */
@Service
public class EleCallSetService extends BaseServiceImpl<EleCallSet> implements IEleCallSetService{
    @Autowired
    private EleCallSetMapper eleCallSetMapper;
    @Override
    public IBaseDao<EleCallSet> getBaseDao() {
        return eleCallSetMapper;
    }

    @Override
    public EleCallSet getEleCallSetByUserid(Integer id) {
        return eleCallSetMapper.getEleCallSetByUserid(id);
    }

    @Override
    public List<EleCallSet> getEleCallSetList() {
        return eleCallSetMapper.getEleCallSetList();
    }
}
