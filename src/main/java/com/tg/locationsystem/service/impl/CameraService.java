package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Camera;
import com.tg.locationsystem.entity.Goods;
import com.tg.locationsystem.mapper.CameraMapper;
import com.tg.locationsystem.service.ICameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/2/26
 */
@Service
public class CameraService extends BaseServiceImpl<Camera> implements ICameraService {
    @Autowired
    private CameraMapper cameraMapper;

    @Override
    public IBaseDao<Camera> getBaseDao() {
        return cameraMapper;
    }

    @Override
    public PageInfo<Camera> getCaneraByMapKeyPage(String mapKey, Integer pageIndex, Integer pageSize) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Camera> list = cameraMapper.getCaneraByMapKey(mapKey);

        return new PageInfo<Camera>(list,3);
    }

    @Override
    public List<Camera> getCameraByMapKey(String mapKey) {
        return cameraMapper.getCaneraByMapKey(mapKey);
    }

    @Override
    public Camera getCameraByIp(String cameraIp) {
        return cameraMapper.getCameraByIp(cameraIp);
    }
}
