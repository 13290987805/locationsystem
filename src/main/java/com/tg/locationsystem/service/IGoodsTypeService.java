package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.GoodsType;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/1
 */
public interface IGoodsTypeService extends IBaseService<GoodsType> {
    GoodsType getGoodsTypeByName(String name, Integer id);

    PageInfo<GoodsType> getGoodsTypesByUserId(Integer pageIndex, Integer pageSize, Integer id);

    List<GoodsType> getGoodsTypes(Integer id);

    GoodsType getGoodsTypeByImg(String img);
}
