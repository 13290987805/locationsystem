package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.mapper.*;
import com.tg.locationsystem.pojo.TagStatusVO;
import com.tg.locationsystem.pojo.TagVO;
import com.tg.locationsystem.service.ITagService;
import com.tg.locationsystem.service.ITagStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/24
 */
@Service
public class TagStatusService extends BaseServiceImpl<TagStatus> implements ITagStatusService{
    @Autowired
    private TagStatusMapper tagStatusMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private PersonTypeMapper personTypeMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsTypeMapper goodsTypeMapper;
    @Autowired
    private TagMapper tagMapper;
    @Override
    public IBaseDao<TagStatus> getBaseDao() {
        return tagStatusMapper;
    }

    @Override
    public PageInfo<TagStatus> getTagStatusByUserId(Integer pageIndex, Integer pageSize, Integer id) {

        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<TagStatus> list = tagStatusMapper.getTagStatusByUserId(id);


         return new PageInfo<TagStatus>(list,3);
    }

    @Override
    public PageInfo<TagStatus> getTagStatusByType(Integer pageIndex, Integer pageSize, Integer id, String typeid) {

        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<TagStatus> list = tagStatusMapper.getTagStatusByType(id,typeid);


        return new PageInfo<TagStatus>(list,3);
    }

    @Override
    public List<TagStatus> getTagStatusByAddress(String tagAdd) {
        return tagStatusMapper.getTagStatusByAddress(tagAdd);
    }

    @Override
    public PageInfo<TagStatus> getTagStatusByCondition(Integer pageIndex, Integer pageSize, Integer id, String msg,String typeid) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<TagStatus> list = tagStatusMapper.getTagStatusByCondition(id,msg,typeid);


        return new PageInfo<TagStatus>(list,3);
    }

    @Override
    public int deletetagStatus(String tableName, String address) {
        return tagStatusMapper.deletetagStatus(tableName,address);
    }

    @Override
    public PageInfo<TagStatus> getAllTagStatusByDeal(Integer pageIndex, Integer pageSize, Integer id,String typeid,String isdeal) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<TagStatus> list = tagStatusMapper.getAllTagStatusByDeal(id,typeid,isdeal);


        return new PageInfo<TagStatus>(list,3);
    }

    @Override
    public PageInfo<TagStatus> getTagStatusByTypeId(Integer pageIndex, Integer pageSize, Integer id, String typeid) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<TagStatus> list = tagStatusMapper.getTagStatusByTypeId(id,typeid);


        return new PageInfo<TagStatus>(list,3);
    }

    @Override
    public int updateBatch(Integer userid,List<Integer> list) {
        return tagStatusMapper.updateBatch(userid,list);
    }


}
