package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Goods;
import com.tg.locationsystem.entity.GoodsType;
import com.tg.locationsystem.entity.Person;
import com.tg.locationsystem.mapper.GoodsMapper;
import com.tg.locationsystem.mapper.GoodsTypeMapper;
import com.tg.locationsystem.pojo.GoodsVO;
import com.tg.locationsystem.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Service
public class GoodsService extends BaseServiceImpl<Goods> implements IGoodsService {
   @Autowired
    private GoodsMapper goodsMapper;
   @Autowired
   private GoodsTypeMapper goodsTypeMapper;
    @Override
    public IBaseDao<Goods> getBaseDao() {
        return goodsMapper;
    }

    @Override
    public Goods getGoodsByIdCard(String reserved1String, Integer id) {
        return goodsMapper.getGoodsByIdCard(reserved1String, id);
    }

    @Override
    public PageInfo<Goods> getGoodsByUserId(Integer pageIndex, Integer pageSize, Integer id) {


        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Goods> list = goodsMapper.getGoodsByUserId(id);

        return new PageInfo<Goods>(list,3);
    }

    @Override
    public PageInfo<Goods> getGoodsByteTypePage(Integer pageIndex, Integer pageSize, Integer typeid, Integer userid) {

        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Goods> list = goodsMapper.getGoodsByteType(typeid,userid);
        return new PageInfo<Goods>(list,3);
    }

    @Override
    public List<Goods> getGoodsByType(Integer typeid, Integer userid) {
        return goodsMapper.getGoodsByteType(typeid,userid);
    }

    @Override
    public Goods getGoodsByAddress(Integer id, String address) {
        return goodsMapper.getGoodsByAddress(id,address);
    }

    @Override
    public Goods getGoodsByImg(String img) {
        return goodsMapper.getGoodsByImg(img);
    }

    @Override
    public Goods getGoodsByOnlyAddress(String address) {
        return goodsMapper.getGoodsByOnlyAddress(address);
    }

    @Override
    public PageInfo<Goods> getGoodsByMsg(Integer pageIndex, Integer pageSize, Integer id, String msg) {
        //设置分页
        PageHelper.startPage(pageIndex, pageSize);

        //人员表查询结果
        List<Goods> list = goodsMapper.getGoodsByMsg(id, msg);

        return new PageInfo<Goods>(list,3);
    }

    @Override
    public Goods getGoodsByGoodsIdCard(String goodsIdcard) {
        return goodsMapper.getGoodsByGoodsIdCard(goodsIdcard);
    }
}
