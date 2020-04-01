package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Myuser;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public interface IMyUserService extends IBaseService<Myuser> {
    Myuser getUserByName(String username);

    PageInfo<Myuser> getUsersByUserId(Integer id, Integer pageIndex, Integer pageSize);

    Myuser getUserByUsername(String username);
}
