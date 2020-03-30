package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.Dep;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/24
 */
public interface IDepService  extends IBaseService<Dep> {
    Dep getDepByTopPid(Integer id, Integer pid);

    List<Dep> getDepsByUserId(int i);

    List<Integer> getDepIdsByParentId(int i, int i1);
}
