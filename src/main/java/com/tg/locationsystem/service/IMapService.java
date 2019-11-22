package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Map;
import com.tg.locationsystem.pojo.MapVO;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/9/10
 */
public interface IMapService extends IBaseService<Map> {
    Map getMapByName(String name, Integer id);


    PageInfo<Map> getMapsByUserId(Integer pageIndex, Integer pageSize, Integer id);

    Map getMapByUuid(String uuid);

    int deleteMapByKey(String mapKey);
}
