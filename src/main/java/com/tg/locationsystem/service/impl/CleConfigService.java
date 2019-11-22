package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.CleConfig;
import com.tg.locationsystem.mapper.CleConfigMapper;
import com.tg.locationsystem.service.ICleConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hyy
 * @ Date2019/11/22
 */
@Service
public class CleConfigService extends BaseServiceImpl<CleConfig> implements ICleConfigService{
    @Autowired
    private CleConfigMapper cleConfigMapper;
    @Override
    public IBaseDao<CleConfig> getBaseDao() {
        return cleConfigMapper;
    }

    @Override
    public int deletecofigByMapKey(String mapKey) {
        return cleConfigMapper.deletecofigByMapKey(mapKey);
    }

    @Override
    public CleConfig getConfigByMapKey(String mapKey) {
        return cleConfigMapper.getConfigByMapKey(mapKey);
    }
}
