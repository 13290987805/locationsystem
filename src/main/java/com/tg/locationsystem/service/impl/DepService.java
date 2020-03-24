package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.CleConfig;
import com.tg.locationsystem.entity.Dep;
import com.tg.locationsystem.mapper.DepMapper;
import com.tg.locationsystem.service.IDepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/24
 */
@Service
public class DepService extends BaseServiceImpl<Dep> implements IDepService{
    @Autowired
    private DepMapper depMapper;
    @Override
    public IBaseDao<Dep> getBaseDao() {
        return depMapper;
    }

    @Override
    public Dep getDepByTopPid(Integer id, Integer pid) {
        return depMapper.getDepByTopPid(id,pid);
    }

    @Override
    public List<Dep> getDepsByUserId(int useri) {
        return depMapper.getDepsByUserId(useri);
    }

    @Override
    public List<Integer> getDepIdsByParentId(int userid, int parentId) {
        return depMapper.getDepIdsByParentId(userid,parentId);
    }
}
