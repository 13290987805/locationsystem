package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Tag;
import com.tg.locationsystem.pojo.QueryTagCondition;
import com.tg.locationsystem.pojo.TagVO;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public interface ITagService extends IBaseService<Tag> {
    Tag getTagByAddress(String address);

    PageInfo<Tag> getTagsByUserId(Integer pageIndex, Integer pageSize, Integer id);


    PageInfo<Tag> getUsedTagsPage(Integer pageIndex, Integer pageSize, Integer id);

    List<TagVO> getUsedTags(Integer id);

    PageInfo<Tag> getNoUsedTagsPage(Integer pageIndex, Integer pageSize, Integer id);

    List<TagVO> getNoUsedTags(Integer id);

    PageInfo<Tag> getTagsByTypePage(Integer pageIndex, Integer pageSize, Integer typeid, Integer id);

    List<Tag> getTagsByType(Integer typeid, Integer id);

    List<Tag> getOnlineTag(Integer id);

    List<Tag> getOfflineTag(Integer id);

    List<Tag> getUserTags(Integer id);

    List<TagVO> getTagByCondition(Integer id, QueryTagCondition queryTagCondition);

    PageInfo<Tag> getTagByConditionPage(Integer pageIndex, Integer pageSize, Integer id, QueryTagCondition queryTagCondition);


    Tag getUseTagByAddress(String s);

    Tag getTagByOnlyAddress(String aAddress);

    List<Tag> getTagList();


    List<Tag> getTagsByUsed(Integer userId);

    List<Tag> getTagsByMapUUID(String mapUUID);

    List<Tag> getTagsByMapUUIDAndUsed(String mapKey);
}
