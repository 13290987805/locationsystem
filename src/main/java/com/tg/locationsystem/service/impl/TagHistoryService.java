package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.TagHistory;
import com.tg.locationsystem.mapper.TagHistoryMapper;
import com.tg.locationsystem.service.ITagHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Service
public class TagHistoryService extends BaseServiceImpl<TagHistory> implements ITagHistoryService {
    @Autowired
    private TagHistoryMapper tagHistoryMapper;


    @Override
    public IBaseDao<TagHistory> getBaseDao() {
        return tagHistoryMapper;
    }

    /*
    * 根据标签add得到标签历史对象集合
    * */
    @Override
    public List<TagHistory> getTagHistoryByAdd(String format,String tagAddress) {
        return tagHistoryMapper.getTagHistoryByAdd(format,tagAddress);
    }

    @Override
    public List<TagHistory> getHistoryByAddAndTime(String format,String tagAddress, String startTime, String endTime) {
        return tagHistoryMapper.getHistoryByAddAndTime(format,tagAddress,startTime,endTime) ;
    }

    @Override
    public List<TagHistory> getHistoryTest(String format, String add) {
        return tagHistoryMapper.getHistoryTest(format,add) ;
    }

    @Override
    public int existTable(String format) {
        return tagHistoryMapper.existTable(format);
    }

    @Override
    public int createNewTable(String format) {
        return tagHistoryMapper.createNewTable(format);
    }

    @Override
    public int inserttagHistory(String format, TagHistory tagHistory) {
        return tagHistoryMapper.inserttagHistory(format,tagHistory) ;
    }

    @Override
    public List<TagHistory> getTagHistoryByAddAndMap(String format, String tagAddress, String mapkey) {
        //
        return tagHistoryMapper.getTagHistoryByAddAndMap(format,tagAddress,mapkey);
    }

    @Override
    public List<TagHistory> getHistoryByAddAndTimeAndMap(String format, String tagAddress, String startTime, String endTime, String mapkey) {
        return tagHistoryMapper.getHistoryByAddAndTimeAndMap(format,tagAddress, startTime, endTime,mapkey) ;
    }

    @Override
    public int deleteHistory(String tableName, String address) {
        return tagHistoryMapper.deleteHistory(tableName,address);
    }


}
