package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Frence;
import com.tg.locationsystem.mapper.FrenceMapper;
import com.tg.locationsystem.pojo.FrenceVO;
import com.tg.locationsystem.pojo.User;
import com.tg.locationsystem.service.IFrenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */@Service
public class FrenceService extends BaseServiceImpl<Frence> implements IFrenceService {

        @Autowired
        private FrenceMapper frenceMapper;
        @Override
        public IBaseDao<Frence> getBaseDao() {
            return frenceMapper;
    }

    @Override
    public Frence getFrenceByname(String name, Integer id) {
        return frenceMapper.getFrenceByname(name,id);
    }

    @Override
    public List<Frence> getfrenceByUserId(Integer id) {
        return frenceMapper.getfrenceByUserId(id);
    }


    @Override
    public List<Frence> getFrenceList() {
        return frenceMapper.getFrenceList();
    }

    @Override
    public PageInfo<Frence> getfrenceByUserId(Integer pageIndex, Integer pageSize, Integer id) {


        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<Frence> list = frenceMapper.getfrenceByUserId(id);



        return new PageInfo<Frence>(list,3);
    }

    @Override
    public PageInfo<Frence> getFrenceByConditionPage(Integer pageIndex, Integer pageSize, Integer id, String msg) {

            //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<Frence> list = frenceMapper.getFrenceByCondition(id,msg);

        //System.out.println("list长度:"+list.size());

        //System.out.println("frenceVO2List长度:"+frenceVO2List.size());
        return new PageInfo<Frence>(list,3);
    }

    @Override
    public int setSwitch(Integer id, String setSwitch) {
        return frenceMapper.setSwitch(id,setSwitch);
    }


}
