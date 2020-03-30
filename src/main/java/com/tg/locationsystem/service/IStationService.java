package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Station;
import com.tg.locationsystem.pojo.StationVO;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public interface IStationService extends IBaseService<Station> {
    Station getStationByAddress(String address, Integer id);


    PageInfo<Station> getStationsByUserId(Integer pageIndex, Integer pageSize, Integer id);

    List<Station> getStationByMapId(Integer id);


    PageInfo<Station> getStationsByMapKey(Integer pageIndex, Integer pageSize, Integer id);

    PageInfo<Station> getOnlineStationsByMapKey(Integer pageIndex, Integer pageSize, Integer id);

    List<StationVO> getStationsByUserIdNoPage(Integer id);
}
