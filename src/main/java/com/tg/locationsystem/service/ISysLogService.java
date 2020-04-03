package com.tg.locationsystem.service;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.SysLog;

/**
 * @author hyy
 * @ Date2020/4/2
 */
public interface ISysLogService extends IBaseService<SysLog> {
    PageInfo<SysLog> getSyslogByPidPage(Integer pageIndex, Integer pageSize, Integer id);

    PageInfo<SysLog> getSyslogByUsernamePage(Integer pageIndex, Integer pageSize, String username);

    PageInfo<SysLog> getSyslogByPidAndMsgPage(Integer pageIndex, Integer pageSize, Integer id, String msg);


    PageInfo<SysLog> getSyslogByUsernameAndMsgPage(Integer pageIndex, Integer pageSize, String username, String msg);
}
