package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.CleConfig;

/**
 * @author hyy
 * @ Date2019/11/22
 */
public interface ICleConfigService extends IBaseService<CleConfig> {
    int deletecofigByMapKey(String mapKey);

    CleConfig getConfigByMapKey(String mapKey);
}
