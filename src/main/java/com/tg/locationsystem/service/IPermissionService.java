package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Permission;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/30
 */
public interface IPermissionService extends IBaseService<Permission> {
    List<Permission> getAllPermission();
}
