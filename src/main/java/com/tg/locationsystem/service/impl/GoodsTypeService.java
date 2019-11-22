package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.GoodsType;
import com.tg.locationsystem.entity.PersonType;
import com.tg.locationsystem.mapper.GoodsTypeMapper;
import com.tg.locationsystem.service.IGoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/1
 */
@Service
public class GoodsTypeService extends BaseServiceImpl<GoodsType> implements IGoodsTypeService {
    @Autowired
    private GoodsTypeMapper goodsTypeMapper;
    @Override
    public IBaseDao<GoodsType> getBaseDao() {
        return goodsTypeMapper;
    }

    @Override
    public GoodsType getGoodsTypeByName(String name, Integer id) {
        return goodsTypeMapper.getGoodsTypeByName(name,id);
    }

    @Override
    public PageInfo<GoodsType> getGoodsTypesByUserId(Integer pageIndex, Integer pageSize, Integer id) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<GoodsType> list = goodsTypeMapper.getGoodsTypeList(id);

        return new PageInfo<GoodsType>(list,3);
    }

    @Override
    public List<GoodsType> getGoodsTypes(Integer id) {
        return goodsTypeMapper.getGoodsTypeList(id);
    }

    @Override
    public GoodsType getGoodsTypeByImg(String img) {
        return goodsTypeMapper.getGoodsTypeByImg(img);
    }
}
