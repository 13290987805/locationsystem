package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.TagHistory;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public interface ITagHistoryService extends IBaseService<TagHistory> {
    List<TagHistory> getTagHistoryByAdd(String format,String tagAddress);

    List<TagHistory> getHistoryByAddAndTime(String format,String tagAddress, String startTime, String endTime);


    List<TagHistory> getHistoryTest(String format, String s);

    int existTable(String format);

    int createNewTable(String format);

    int inserttagHistory(String format, TagHistory tagHistory);

    List<TagHistory> getTagHistoryByAddAndMap(String format, String tagAddress, String mapkey);

    List<TagHistory> getHistoryByAddAndTimeAndMap(String format, String tagAddress, String startTime, String endTime, String mapkey);

    int deleteHistory(String tableName, String address);
}
