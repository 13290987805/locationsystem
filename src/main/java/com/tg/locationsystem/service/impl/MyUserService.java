package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.mapper.MyuserMapper;
import com.tg.locationsystem.service.IMyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Service
public class MyUserService extends BaseServiceImpl<Myuser> implements IMyUserService{
    @Autowired
    private MyuserMapper myuserMapper;
    @Override
    public IBaseDao<Myuser> getBaseDao() {
        return myuserMapper;
    }

    @Override
    public Myuser getUserByName(String username) {
        return myuserMapper.getUserByName(username);
    }
}
