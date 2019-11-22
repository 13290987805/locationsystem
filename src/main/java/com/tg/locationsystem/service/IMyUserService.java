package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Myuser;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public interface IMyUserService extends IBaseService<Myuser> {
    Myuser getUserByName(String username);
}
