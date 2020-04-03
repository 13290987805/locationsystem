package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.StatisticsCall;
import com.tg.locationsystem.entity.SysLog;
import com.tg.locationsystem.entity.TagHistory;
import com.tg.locationsystem.mapper.SysLogMapper;
import com.tg.locationsystem.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/4/2
 */
@Service
public class SysLogService extends BaseServiceImpl<SysLog> implements ISysLogService{
    @Autowired
    private SysLogMapper sysLogMapper;
    @Override
    public IBaseDao<SysLog> getBaseDao() {
        return sysLogMapper;
    }

    @Override
    public PageInfo<SysLog> getSyslogByPidPage(Integer pageIndex, Integer pageSize, Integer pid) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<SysLog> list = sysLogMapper.getSyslogByPidPage(pid);

        return new PageInfo<SysLog>(list,3);
    }

    @Override
    public PageInfo<SysLog> getSyslogByUsernamePage(Integer pageIndex, Integer pageSize, String username) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<SysLog> list = sysLogMapper.getSyslogByUsernamePage(username);

        return new PageInfo<SysLog>(list,3);
    }

    @Override
    public PageInfo<SysLog> getSyslogByPidAndMsgPage(Integer pageIndex, Integer pageSize, Integer pid, String msg) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<SysLog> list = sysLogMapper.getSyslogByPidAndMsgPage(pid,msg);

        return new PageInfo<SysLog>(list,3);
    }

    @Override
    public PageInfo<SysLog> getSyslogByUsernameAndMsgPage(Integer pageIndex, Integer pageSize, String username, String msg) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<SysLog> list = sysLogMapper.getSyslogByUsernameAndMsgPage(username,msg);

        return new PageInfo<SysLog>(list,3);
    }


}
