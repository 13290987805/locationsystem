package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Permission;
import com.tg.locationsystem.entity.Person;
import com.tg.locationsystem.mapper.PermissionMapper;
import com.tg.locationsystem.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/30
 */
@Service
public class PermissionService  extends BaseServiceImpl<Permission> implements IPermissionService{
    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public IBaseDao<Permission> getBaseDao() {
        return permissionMapper;
    }

    @Override
    public List<Permission> getAllPermission() {
        return permissionMapper.getAllPermission();
    }
}
