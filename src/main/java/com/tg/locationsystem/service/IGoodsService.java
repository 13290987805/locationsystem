package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Goods;
import com.tg.locationsystem.pojo.GoodsVO;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public interface IGoodsService extends IBaseService<Goods> {
    Goods getGoodsByIdCard(String reserved1String, Integer id);

    PageInfo<Goods> getGoodsByUserId(Integer pageIndex, Integer pageSize, Integer id);

    PageInfo<Goods> getGoodsByteTypePage(Integer pageIndex, Integer pageSize, Integer typeid, Integer id);

    List<Goods> getGoodsByType(Integer typeid, Integer id);

    Goods getGoodsByAddress(Integer id, String address);

    Goods getGoodsByImg(String img);


    Goods getGoodsByOnlyAddress(String address);

    PageInfo<Goods> getGoodsByMsg(Integer pageIndex, Integer pageSize, Integer id, String msg);
}
