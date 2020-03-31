package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.MyuserRole;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/28
 */
public interface IMyuserRoleService extends IBaseService<MyuserRole> {
    MyuserRole getMyuserRoleByRoleId(Integer roleId);

    int deleteByRoleId(Integer roleId);

    List<MyuserRole> getmyuserRoleByUserId(Integer id);
}
