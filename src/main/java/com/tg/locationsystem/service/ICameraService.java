package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Camera;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/2/26
 */
public interface ICameraService extends IBaseService<Camera> {
    PageInfo<Camera> getCaneraByMapKeyPage(String mapKey, Integer pageIndex, Integer pageSize);

    List<Camera> getCameraByMapKey(String mapKey);

    Camera selectByMapKeyAndCameraIp(String mapKey, String cameraIp);

    Camera getCameraByIp(String cameraIp);

    List<Camera> getCameraList();
}
