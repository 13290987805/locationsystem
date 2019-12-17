package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Goods;
import com.tg.locationsystem.entity.GoodsType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper extends IBaseDao<Goods>{
    int deleteByPrimaryKey(Integer id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKey(Goods record);

    Goods getGoodsByIdCard(@Param("reserved1String")String reserved1String, @Param("id")Integer id);

    List<Goods> getGoodsByUserId(Integer id);

    List<Goods> getGoodsByteType(@Param("typeid") Integer typeid, @Param("userid") Integer userid);

    Goods getGoodsByAddress(@Param("userid")Integer id,@Param("address") String address);

    Goods getGoodsByOnlyAddress(String address);

    Goods getGoodsByImg(String img);

    Goods getGoodsByByIdCard(String personIdcard);

    List<Goods> getGoodsByMsg(@Param("userid") Integer id,@Param("msg") String msg);


    List<Goods> getGoodsByGoodsTypeId(@Param("userid") Integer id, @Param("typeid") Integer id1);

    Goods getGoodsByGoodsIdCard(String goodsIdcard);
}