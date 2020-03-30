package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.MyuserRole;
import com.tg.locationsystem.mapper.MyuserRoleMapper;
import com.tg.locationsystem.service.IMyuserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hyy
 * @ Date2020/3/28
 */
@Service
public class MyuserRoleService  extends BaseServiceImpl<MyuserRole> implements IMyuserRoleService {
    @Autowired
    private MyuserRoleMapper myuserRoleMapper;

    @Override
    public IBaseDao<MyuserRole> getBaseDao() {
        return myuserRoleMapper;
    }

    @Override
    public MyuserRole getMyuserRoleByRoleId(Integer roleId) {
        return myuserRoleMapper.getMyuserRoleByRoleId(roleId);
    }
}
