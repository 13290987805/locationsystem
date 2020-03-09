package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Camera;

import java.util.List;

public interface CameraMapper extends IBaseService<Camera>{
    int deleteByPrimaryKey(Integer id);

    int insert(Camera record);

    int insertSelective(Camera record);

    Camera selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Camera record);

    int updateByPrimaryKey(Camera record);

    List<Camera> getCaneraByMapKey(String mapKey);
}