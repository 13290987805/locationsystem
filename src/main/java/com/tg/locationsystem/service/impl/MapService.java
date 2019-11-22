package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Goods;
import com.tg.locationsystem.entity.Map;
import com.tg.locationsystem.mapper.MapMapper;
import com.tg.locationsystem.pojo.MapVO;
import com.tg.locationsystem.service.IMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/9/10
 */
@Service
public class MapService extends BaseServiceImpl<Map> implements IMapService{
    @Autowired
    private MapMapper mapMapper;
    @Override
    public IBaseDao<Map> getBaseDao() {
        return mapMapper;
    }

    @Override
    public Map getMapByName(String name, Integer id) {
        return mapMapper.getMapByName(name,id);
    }

    @Override
    public PageInfo<Map> getMapsByUserId(Integer pageIndex, Integer pageSize, Integer id) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Map> list = mapMapper.getMapsByUserId(id);

        return new PageInfo<Map>(list,3);
    }

    @Override
    public Map getMapByUuid(String uuid) {
        return mapMapper.getMapByUuid(uuid);
    }

    @Override
    public int deleteMapByKey(String mapKey) {
        return mapMapper.deleteMapByKey(mapKey);
    }


}
