package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.TagStatus;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/24
 */
public interface ITagStatusService extends IBaseService<TagStatus> {
    PageInfo<TagStatus> getTagStatusByUserId(Integer pageIndex, Integer pageSize, Integer id);

    PageInfo<TagStatus> getTagStatusByType(Integer pageIndex, Integer pageSize, Integer id, String typeid);

    List<TagStatus> getTagStatusByAddress(String tagAdd);

    PageInfo<TagStatus> getTagStatusByCondition(Integer pageIndex, Integer pageSize, Integer id, String msg,String typeid);


    int deletetagStatus(String tableName, String address);

    PageInfo<TagStatus> getAllTagStatusByDeal(Integer pageIndex, Integer pageSize, Integer id,String typeid,String isdeal);

    PageInfo<TagStatus> getTagStatusByTypeId(Integer pageIndex, Integer pageSize, Integer id, String typeid);
}
