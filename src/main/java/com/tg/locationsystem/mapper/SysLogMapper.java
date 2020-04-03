package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.SysLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysLogMapper extends IBaseDao<SysLog>{
    int deleteByPrimaryKey(Integer id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

    List<SysLog> getSyslogByPidPage(Integer pid);

    List<SysLog> getSyslogByUsernamePage(String username);

    List<SysLog> getSyslogByPidAndMsgPage(@Param("pid") Integer pid,@Param("msg") String msg);


    List<SysLog> getSyslogByUsernameAndMsgPage(@Param("username") String username,@Param("msg") String msg);
}