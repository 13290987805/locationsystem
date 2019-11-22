package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.FrenceHistory;
import com.tg.locationsystem.pojo.FrenceHistoryVO;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public interface IFrenceHistoryService extends IBaseService<FrenceHistory> {
    PageInfo<FrenceHistory> getFrenceHistoryPage(Integer pageIndex, Integer pageSize, Integer id);

    List<FrenceHistoryVO> getFrenceHistory(Integer frenceid, Integer id);

    PageInfo<FrenceHistory> test(int i, int i1, int i2);

    int deleteHistory(String tableName, String address);


    int deleteHistoryByFrenceId(Integer frenceid);
}
