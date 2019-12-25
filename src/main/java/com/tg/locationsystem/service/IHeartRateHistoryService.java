package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.HeartRateHistory;
import com.tg.locationsystem.pojo.HeartRateHistoryVO;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/5
 */
public interface IHeartRateHistoryService extends IBaseService<HeartRateHistory> {
    List<HeartRateHistory> getheartRateHistoryByCondition(String address, String startTime, String endTime);

    List<HeartRateHistoryVO> getSomeheartRateHistory();

    int deleteHistory(String tableName, String address);

    List<HeartRateHistory> getheartRateHistoryByPersonIdcards(List<String> personIdcard);
}
