package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.TagStatus;
import com.tg.locationsystem.pojo.QueryTagStatusVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface TagStatusMapper extends IBaseDao<TagStatus>{
    int deleteByPrimaryKey(Integer id);

    int insert(TagStatus record);

    int insertSelective(TagStatus record);

    TagStatus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TagStatus record);

    int updateByPrimaryKey(TagStatus record);

    List<TagStatus> getTagStatusByUserId(@Param("userid") Integer id);

    List<TagStatus> getTagStatusByType(@Param("userid") Integer id, @Param("typeid") String typeid);

    List<TagStatus> getTagStatusByAddress(String tagAdd);

    List<TagStatus> getTagStatusByCondition(@Param("userid")Integer id, @Param("msg")String msg,@Param("typeid")String typeid);

    int deletetagStatus(@Param("tableName")String tableName,@Param("address") String address);

    List<TagStatus> getAllTagStatusByDeal(@Param("userid") Integer id,@Param("typeid") String typeid,@Param("isdeal") String isdeal);

    List<TagStatus> getTagStatusByTypeId(@Param("userid") Integer id, @Param("typeid") String typeid);


    int updateBatch(@Param("userid") Integer userid,@Param("idList") List<Integer> list);

    List<TagStatus> getTagStatusByNoIdCards(@Param("userid") Integer id,@Param("alert_type") Integer alert_type,@Param("isDeal") String isDeal);

    List<TagStatus> getTagStatusBySomeCondition(@Param("userid") Integer id, @Param("queryTagStatusVO") QueryTagStatusVO queryTagStatusVO,@Param("idCardList") List<String> idCardList);

    List<TagStatus> getAllTagStatusByIsDeal(@Param("userid") Integer id,@Param("isdeal") String isdeal);

    int setAllAlertDeal(Integer id);
}