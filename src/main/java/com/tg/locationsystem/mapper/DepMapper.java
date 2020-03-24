package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.Dep;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DepMapper extends IBaseDao<Dep>{
    int deleteByPrimaryKey(Integer id);

    int insert(Dep record);

    int insertSelective(Dep record);

    Dep selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Dep record);

    int updateByPrimaryKey(Dep record);

    Dep getDepByTopPid(@Param("userid") Integer id, @Param("pid") Integer pid);

    List<Dep> getDepsByUserId(int useri);

    @Select("\n" +
            "select id from (\n" +
            "              select t1.id,\n" +
            "              if(find_in_set(pid, @pids) > 0, @pids := concat(@pids, ',', id), 0) as ischild\n" +
            "              from (\n" +
            "                   select id,pid from dep t WHERE t.user_id=#{userid} order by pid, id\n" +
            "                  ) t1,\n" +
            "                  (select @pids := #{parentId}) t2\n" +
            "             ) t3 where ischild != 0")
    List<Integer> getDepIdsByParentId(@Param("userid")int userid, @Param("parentId")int parentId);
}