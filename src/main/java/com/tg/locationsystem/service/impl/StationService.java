package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Map;
import com.tg.locationsystem.entity.Station;
import com.tg.locationsystem.mapper.MapMapper;
import com.tg.locationsystem.mapper.StationMapper;
import com.tg.locationsystem.pojo.StationVO;
import com.tg.locationsystem.service.IStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Service
public class StationService  extends BaseServiceImpl<Station> implements IStationService{
    @Autowired
    private StationMapper stationMapper;
    @Autowired
    private MapMapper mapMapper;
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

    @Override
    public List<StationVO> getStationsByUserIdNoPage(Integer id) {
        List<Station> list = stationMapper.getStationsByUserId(id);
        List<StationVO> stationVOList=new ArrayList<>();
        for (Station station : list){
            StationVO stationVO = new StationVO();
            stationVO.setAddr(station.getAddr());
            stationVO.setAntDelayRx(station.getAntDelayRx());
            stationVO.setAntDelayTx(station.getAntDelayTx());
            stationVO.setDimension(station.getDimension());
            stationVO.setId(station.getId());
            stationVO.setIsmaster(station.getIsmaster());
            stationVO.setMasteranchoraddress(station.getMasteranchoraddress());
            stationVO.setMasterLagDelay(station.getMasterLagDelay());
            stationVO.setSid(station.getSid());
            stationVO.setStationStatus(station.getStationStatus());
            stationVO.setUserId(station.getUserId());
            stationVO.setMasterAddr(station.getMasterAddr());
            stationVO.setRfdistance(station.getRfdistance());
            Map map = mapMapper.selectByPrimaryKey(station.getMapId());
            stationVO.setMapId(station.getMapId());
            if (map != null) {
                stationVO.setMapName(map.getMapName());
            }
            stationVOList.add(stationVO);
        }
        return stationVOList;
    }


}
