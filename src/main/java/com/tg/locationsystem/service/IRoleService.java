package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Role;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/30
 */
public interface IRoleService extends IBaseService<Role> {
    List<Role> getRoleByUserId(Integer id);
}
