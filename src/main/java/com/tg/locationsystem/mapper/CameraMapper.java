package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Camera;

import java.util.List;

public interface CameraMapper extends IBaseDao<Camera> {
    int deleteByPrimaryKey(Integer id);

    int insert(Camera record);

    int insertSelective(Camera record);

    Camera selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Camera record);

    int updateByPrimaryKey(Camera record);

    List<Camera> getCaneraByMapKey(String mapKey);

    Camera selectByMapKeyAndCameraIp(String mapKey, String cameraIp);

    Camera getCameraByIp(String cameraIp);
}