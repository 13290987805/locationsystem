package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.GoodsType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsTypeMapper extends IBaseDao<GoodsType>{
    int deleteByPrimaryKey(Integer id);

    int insert(GoodsType record);

    int insertSelective(GoodsType record);

    GoodsType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GoodsType record);

    int updateByPrimaryKey(GoodsType record);

    GoodsType getGoodsTypeByName(@Param("typename") String name,@Param("userid") Integer id);

    List<GoodsType> getGoodsTypeList(Integer id);

    GoodsType getGoodsTypeByImg(String img);

    List<GoodsType> getGoodsTypeByMsg(@Param("userid") Integer id, @Param("msg") String msg);
}