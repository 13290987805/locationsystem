package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.FrenceHistory;
import com.tg.locationsystem.pojo.FrenceHistoryVO;

import java.util.List;
import java.util.Map;

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

    PageInfo<FrenceHistory> getFrenceHistoryByFrenceId(Integer pageIndex, Integer pageSize, Integer id, Integer frenceId);

    PageInfo<FrenceHistory> getFrenceHistoryByPersonIdCard(Integer pageIndex, Integer pageSize, Integer id, String idCard);


    List<FrenceHistory> getFrenceHistorysByPersonIdCard(Integer id, String idCard);


    PageInfo<FrenceHistory> getFrenceHistoryByPersonIdcardss(Integer pageIndex, Integer pageSize, List<String> list);

    PageInfo<FrenceHistoryVO> getFrenceHistoryByListPage(Integer pageIndex, Integer pageSize, List<FrenceHistoryVO> frenceVOList);

    PageInfo<FrenceHistory> getFrenceHistoryByFrenceIdAndPersonName(Integer pageIndex, Integer pageSize, Integer frenceId, List<String> list);


    int updateBatch(Integer id, List<Integer> idsList);

    PageInfo<FrenceHistory> getAllFrenceHistoryByIsDeal(Integer pageIndex, Integer pageSize, Integer id, String isdeal);
}
