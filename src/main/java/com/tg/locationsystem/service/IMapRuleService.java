package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Map;
import com.tg.locationsystem.entity.MapRule;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/11/20
 */
public interface IMapRuleService extends IBaseService<MapRule> {
    int deleteByMapKey(String mapKey);

    List<MapRule> getAllRule();
}
