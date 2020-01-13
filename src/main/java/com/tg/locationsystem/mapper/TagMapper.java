package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Tag;
import com.tg.locationsystem.pojo.QueryTagCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper extends IBaseDao<Tag>{
    int deleteByPrimaryKey(Integer id);

    int insert(Tag record);

    int insertSelective(Tag record);

    Tag selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Tag record);

    int updateByPrimaryKey(Tag record);

    Tag getTagByAddress(@Param("address") String address, @Param("id")Integer id);

    List<Tag> getTagsByUserId(Integer id);

    List<Tag> getUsedTagsPage(Integer id);

    List<Tag> getNoUsedTagsList(Integer id);

    List<Tag> getTagsByType(@Param("typeid") Integer typeid, @Param("userid") Integer userid);

    List<Tag> getOnlineTag(Integer id);

    List<Tag> getOfflineTag(Integer id);

    List<Tag> getUserTags(Integer id);

    List<Tag> getTagByCondition(@Param("userid") Integer id,@Param("queryTagCondition") QueryTagCondition queryTagCondition);

    Tag getUseTagByAddress(String add);

    Tag getTagByOnlyAddress(String aAddress);

    List<Tag> getTagList();


    List<Tag> getTagsByUsed(Integer userId);
//得到地图下所有的标签
    List<Tag> getTagsByMapUUID(String mapUUID);
    //得到地图下所有已使用的标签
    List<Tag> getTagsByMapUUIDAndUsed(String mapKey);
}