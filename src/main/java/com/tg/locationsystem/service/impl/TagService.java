package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Goods;
import com.tg.locationsystem.entity.Person;
import com.tg.locationsystem.entity.Tag;
import com.tg.locationsystem.entity.TagType;
import com.tg.locationsystem.mapper.GoodsMapper;
import com.tg.locationsystem.mapper.PersonMapper;
import com.tg.locationsystem.mapper.TagMapper;
import com.tg.locationsystem.mapper.TagTypeMapper;
import com.tg.locationsystem.pojo.QueryTagCondition;
import com.tg.locationsystem.pojo.TagVO;
import com.tg.locationsystem.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Service
public class TagService extends BaseServiceImpl<Tag> implements ITagService{
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TagTypeMapper tagTypeMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Override
    public IBaseDao<Tag> getBaseDao() {
        return tagMapper;
    }

    @Override
    public Tag getTagByAddress(String address) {
        return tagMapper.getTagByAddress(address);
    }

    @Override
    public PageInfo<Tag> getTagsByUserId(Integer pageIndex, Integer pageSize, Integer id) {


        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<Tag> list = tagMapper.getTagsByUserId(id);


        return new PageInfo<Tag>(list,3);
    }

    @Override
    public PageInfo<Tag> getUsedTagsPage(Integer pageIndex, Integer pageSize, Integer id) {

        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<Tag> list = tagMapper.getUsedTagsPage(id);

        return new PageInfo<Tag>(list,3);
    }

    @Override
    public List<TagVO> getUsedTags(Integer id) {
        List<Tag> list = tagMapper.getUserTags(id);
        List<TagVO> tagVOList=new ArrayList<>();
        for (Tag tag : list) {
            TagVO tagVO=new TagVO();
            tagVO.setAddress(tag.getAddress());
            tagVO.setId(tag.getId());
            tagVO.setLastonline(tag.getLastonline());
            tagVO.setUserId(tag.getUserId());
            if (tag.getX()!=null){
                tagVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagVO.setZ(tag.getZ());
            }
            tagVO.setElectric(tag.getElectric());
            tagVO.setUsed("已使用");
            //在线离线
            if ("1".equals(tag.getIsonline())){
                tagVO.setIsonline("在线");
            }
            if ("0".equals(tag.getIsonline())){
                tagVO.setIsonline("离线");
            }

            //标签类型
            if (tag.getTagTypeid()!=null){
                TagType tagType = tagTypeMapper.selectByPrimaryKey(tag.getTagTypeid());
                if (tagType!=null){
                    tagVO.setTagTypename(tagType.getName());
                }
            }
            //使用者
            if (tag.getAddress()!=null){
                Person person= personMapper.getPersonByOnlyAddress(tag.getAddress());
                if (person!=null){
                    tagVO.setType("person");
                    tagVO.setUsername(person.getPersonName());
                }else {
                    Goods goods=goodsMapper.getGoodsByOnlyAddress(tag.getAddress());
                    if (goods!=null){
                        tagVO.setType("goods");
                        tagVO.setUsername(goods.getGoodsName());
                    }
                }
                tagVOList.add(tagVO);
            }
        }
        return tagVOList;
    }

    @Override
    public PageInfo<Tag> getNoUsedTagsPage(Integer pageIndex, Integer pageSize, Integer id) {

        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<Tag> list = tagMapper.getNoUsedTagsList(id);


        return new PageInfo<Tag>(list,3);
    }

    @Override
    public List<TagVO> getNoUsedTags(Integer id) {
        List<Tag> list = tagMapper.getNoUsedTagsList(id);
        List<TagVO> tagVOList=new ArrayList<>();
        for (Tag tag : list) {
            TagVO tagVO=new TagVO();
            tagVO.setAddress(tag.getAddress());
            tagVO.setId(tag.getId());
            tagVO.setLastonline(tag.getLastonline());
            tagVO.setUserId(tag.getUserId());
            if (tag.getX()!=null){
                tagVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagVO.setZ(tag.getZ());
            }
            tagVO.setElectric(tag.getElectric());

            tagVO.setUsed("未使用");
            //在线离线
            if ("1".equals(tag.getIsonline())){
                tagVO.setIsonline("在线");
            }
            if ("0".equals(tag.getIsonline())){
                tagVO.setIsonline("离线");
            }

            //标签类型
            if (tag.getTagTypeid()!=null){
                TagType tagType = tagTypeMapper.selectByPrimaryKey(tag.getTagTypeid());
                if (tagType!=null){
                    tagVO.setTagTypename(tagType.getName());
                }
            }
            //使用者
           /* if (tag.getAddress()!=null){
                Person person= personMapper.getPersonByOnlyAddress(tag.getAddress());
                if (person!=null){
                    tagVO.setType("person");
                    tagVO.setUsername(person.getPersonName());
                }else {
                    Goods goods=goodsMapper.getGoodsByOnlyAddress(tag.getAddress());
                    if (goods!=null){
                        tagVO.setType("goods");
                        tagVO.setUsername(goods.getGoodsName());
                    }
                }

            }*/
            tagVOList.add(tagVO);
        }
        return tagVOList;
    }

    @Override
    public PageInfo<Tag> getTagsByTypePage(Integer pageIndex, Integer pageSize, Integer typeid, Integer userid) {

        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Tag> list = tagMapper.getTagsByType(typeid,userid);
        return new PageInfo<Tag>(list,3);
    }

    @Override
    public List<Tag> getTagsByType(Integer typeid, Integer userid) {
        return tagMapper.getTagsByType(typeid,userid);
    }

    @Override
    public List<Tag> getOnlineTag(Integer id) {
        return tagMapper.getOnlineTag(id);
    }

    @Override
    public List<Tag> getOfflineTag(Integer id) {
        return  tagMapper.getOfflineTag(id);
    }

    @Override
    public List<Tag> getUserTags(Integer id) {
        return tagMapper.getUserTags(id);
    }

    @Override
    public List<TagVO> getTagByCondition(Integer id, QueryTagCondition queryTagCondition) {
        List<Tag> list =tagMapper.getTagByCondition(id,queryTagCondition) ;
        List<TagVO> tagVOList=new ArrayList<>();
        for (Tag tag : list) {
            TagVO tagVO=new TagVO();
            tagVO.setAddress(tag.getAddress());
            tagVO.setId(tag.getId());
            tagVO.setLastonline(tag.getLastonline());
            tagVO.setUserId(tag.getUserId());
            tagVO.setElectric(tag.getElectric());
            if (tag.getX()!=null){
                tagVO.setX(tag.getX());
            }
            if (tag.getY()!=null){
                tagVO.setY(tag.getY());
            }
            if (tag.getZ()!=null){
                tagVO.setZ(tag.getZ());
            }

            //在线离线
            if ("1".equals(tag.getIsonline())){
                tagVO.setIsonline("在线");
            }
            if ("0".equals(tag.getIsonline())){
                tagVO.setIsonline("离线");
            }

            //是否使用
            if ("0".equals(tag.getUsed())){
                tagVO.setUsed("未使用");
                tagVO.setUsername("无");
            }
            if ("1".equals(tag.getUsed())){
                tagVO.setUsed("已使用");
                //使用者
                if (tag.getAddress()!=null){
                    Person person= personMapper.getPersonByOnlyAddress(tag.getAddress());
                    if (person!=null){
                        tagVO.setType("person");
                        tagVO.setUsername(person.getPersonName());
                    }else {
                        Goods goods=goodsMapper.getGoodsByOnlyAddress(tag.getAddress());
                        if (goods!=null){
                            tagVO.setType("goods");
                            tagVO.setUsername(goods.getGoodsName());
                        }
                    }
                }
            }
            //标签类型
            if (tag.getTagTypeid()!=null){
                TagType tagType = tagTypeMapper.selectByPrimaryKey(tag.getTagTypeid());
                if (tagType!=null){
                    tagVO.setTagTypename(tagType.getName());
                }
            }
            tagVOList.add(tagVO);
        }
        return tagVOList;

    }

    @Override
    public PageInfo<Tag> getTagByConditionPage(Integer pageIndex, Integer pageSize, Integer id, QueryTagCondition queryTagCondition) {

        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Tag> list =tagMapper.getTagByCondition(id,queryTagCondition) ;

        return new PageInfo<Tag>(list,3);
    }

    @Override
    public Tag getUseTagByAddress(String add) {
        return tagMapper.getUseTagByAddress(add);
    }

    @Override
    public Tag getTagByOnlyAddress(String aAddress) {
        return tagMapper.getTagByOnlyAddress(aAddress);
    }

    @Override
    public List<Tag> getTagList() {
        return tagMapper. getTagList();
    }

    @Override
    public List<Tag> getTagsByUsed(Integer userId) {
        return tagMapper.getTagsByUsed(userId);
    }

    @Override
    public List<Tag> getTagsByMapUUID(String mapUUID) {
        //得到地图下所有的标签
        return tagMapper.getTagsByMapUUID(mapUUID);
    }

    @Override
    public List<Tag> getTagsByMapUUIDAndUsed(String mapKey) {
        //得到地图下所有已使用的标签
        return tagMapper.getTagsByMapUUIDAndUsed(mapKey);
    }


}
