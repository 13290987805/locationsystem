package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Station;
import com.tg.locationsystem.mapper.StationMapper;
import com.tg.locationsystem.service.IStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Service
public class StationService  extends BaseServiceImpl<Station> implements IStationService{
    @Autowired
    private StationMapper stationMapper;
    @Override
    public IBaseDao<Station> getBaseDao() {
        return stationMapper;
    }

    @Override
    public Station getStationByAddress(String address, Integer id) {
        return stationMapper.getStationByAddress(address,id);
    }

    @Override
    public PageInfo<Station> getStationsByUserId(Integer pageIndex, Integer pageSize, Integer id) {


        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Station> list = stationMapper.getStationsByUserId(id);

        return new PageInfo<Station>(list,3);
    }

    @Override
    public List<Station> getStationByMapId(Integer id) {
        return stationMapper.getStationByMapId(id);
    }

    @Override
    public PageInfo<Station> getStationsByMapKey(Integer pageIndex, Integer pageSize, Integer id) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Station> list = stationMapper.getStationByMapId(id);

        return new PageInfo<Station>(list,3);
    }

    @Override
    public PageInfo<Station> getOnlineStationsByMapKey(Integer pageIndex, Integer pageSize, Integer id) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Station> list = stationMapper.getOnlineStationsByMapKey(id);
        //System.out.println("个数:"+list.size());

        return new PageInfo<Station>(list,3);
    }


}
