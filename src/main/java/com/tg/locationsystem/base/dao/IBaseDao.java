package com.tg.locationsystem.base.dao;

import java.util.List;

/**
 * 声明一些常规的方法
 * @author hyy
 *
 */
public interface IBaseDao<T> {

	int deleteByPrimaryKey(Integer id);

    int insert(T t);

    int insertSelective(T t);

    T selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(T t);

    int updateByPrimaryKey(T t);

	List<T> list();
    
}
