package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.MapRule;
import com.tg.locationsystem.mapper.MapRuleMapper;
import com.tg.locationsystem.service.IMapRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/11/20
 */
@Service
public class MapRuleMapperService extends BaseServiceImpl<MapRule> implements IMapRuleService {
    @Autowired
    private MapRuleMapper mapRuleMapper;
    @Override
    public IBaseDao<MapRule> getBaseDao() {
        return mapRuleMapper;
    }

    @Override
    public int deleteByMapKey(String mapKey) {
        return mapRuleMapper.deleteByMapKey(mapKey);
    }

    @Override
    public List<MapRule> getAllRule() {
        return mapRuleMapper.getAllRule();
    }
}
