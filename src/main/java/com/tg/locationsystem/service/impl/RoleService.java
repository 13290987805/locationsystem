package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Role;
import com.tg.locationsystem.entity.Station;
import com.tg.locationsystem.mapper.RoleMapper;
import com.tg.locationsystem.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/30
 */
@Service
public class RoleService  extends BaseServiceImpl<Role> implements IRoleService{
    @Autowired
    private RoleMapper roleMapper;
    @Override
    public IBaseDao<Role> getBaseDao() {
        return roleMapper;
    }

    @Override
    public List<Role> getRoleByUserId(Integer id) {
        return roleMapper.getRoleByUserId(id);
    }
}
