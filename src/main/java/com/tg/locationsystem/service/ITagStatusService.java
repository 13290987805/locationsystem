package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.TagStatus;
import com.tg.locationsystem.pojo.QueryTagStatusVO;

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


    int updateBatch(Integer userid,List<Integer> list);


    PageInfo<TagStatus> getTagStatusByNoIdCards(Integer pageIndex, Integer pageSize, Integer id, Integer alert_type, String isDeal);

    PageInfo<TagStatus> getTagStatusBySomeCondition(Integer pageIndex, Integer pageSize, Integer id, QueryTagStatusVO queryTagStatusVO,List<String> idCardList);


    PageInfo<TagStatus> getAllTagStatusByIsDeal(Integer pageIndex, Integer pageSize, Integer id, String isdeal);
}
