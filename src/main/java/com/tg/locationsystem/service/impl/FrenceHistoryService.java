package com.tg.locationsystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Frence;
import com.tg.locationsystem.entity.FrenceHistory;
import com.tg.locationsystem.entity.Goods;
import com.tg.locationsystem.entity.Person;
import com.tg.locationsystem.mapper.*;
import com.tg.locationsystem.pojo.FrenceHistoryVO;
import com.tg.locationsystem.service.IFrenceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Service
public class FrenceHistoryService extends BaseServiceImpl<FrenceHistory> implements IFrenceHistoryService {
    @Autowired
    private FrenceHistoryMapper frenceHistoryMapper;
    @Autowired
    private FrenceMapper frenceMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Override
    public IBaseDao<FrenceHistory> getBaseDao() {
        return frenceHistoryMapper;
    }

    @Override
    public PageInfo<FrenceHistory> getFrenceHistoryPage(Integer pageIndex, Integer pageSize, Integer userid) {


        //System.out.println("list长度:"+list.size());
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        List<FrenceHistory> list = frenceHistoryMapper.getFrenceHistoryList(userid);

        return new PageInfo<>(list);
    }

    @Override
    public List<FrenceHistoryVO> getFrenceHistory(Integer frenceid, Integer id) {
        List<FrenceHistory> list = frenceHistoryMapper.getFrenceHistory(frenceid, id);

        List<FrenceHistoryVO> frenceVOList=new ArrayList<>(list.size());
        for (FrenceHistory frenceHistory : list) {
            FrenceHistoryVO frenceVO=new FrenceHistoryVO();
            frenceVO.setId(frenceHistory.getId());
            frenceVO.setPersonIdcard(frenceHistory.getPersonIdcard());
            frenceVO.setX(frenceHistory.getX());
            frenceVO.setY(frenceHistory.getY());
            frenceVO.setStatus(frenceHistory.getStatus());
            frenceVO.setTime(frenceHistory.getTime());
            frenceVO.setFrenceId(frenceHistory.getFrenceId());
            frenceVO.setUserId(frenceHistory.getUserId());
            frenceVO.setMapkey(frenceHistory.getMapKey());
            Frence frence = frenceMapper.selectByPrimaryKey(frenceHistory.getFrenceId());
            if (frence!=null){
                frenceVO.setFrenceName(frence.getName());
                String type = frence.getType();
                frenceVO.setAlert_type(type);
                if ("in".equals(type)){
                    frenceVO.setAlert_type("进入");
                }
                if ("out".equals(type)){
                    frenceVO.setAlert_type("离开");
                }
                if ("on".equals(type)){
                    frenceVO.setAlert_type("停留");
                }
                frenceVO.setData(frence.getData());
            }
            Person person = personMapper.getPersonByIdCard(frenceHistory.getPersonIdcard());
            if (person!=null){
                frenceVO.setTagName(person.getPersonName());
                frenceVO.setImg(person.getImg());
            }else {
                Goods goods = goodsMapper.getGoodsByByIdCard(frenceHistory.getPersonIdcard());
                if (goods!=null){
                    frenceVO.setTagName(goods.getGoodsName());
                    frenceVO.setImg(goods.getImg());
                }
            }
            frenceVOList.add(frenceVO);
        }
        return frenceVOList;
    }

    @Override
    public PageInfo<FrenceHistory> test(int i, int i1, int i2) {
        //设置分页
        PageHelper.startPage(i,i1);

        List<FrenceHistory> list = frenceHistoryMapper.getFrenceHistoryList(i2);
        return new PageInfo<>(list);
    }

    @Override
    public int deleteHistory(String tableName, String address) {
        return frenceHistoryMapper.deleteHistory(tableName,address);
    }

    @Override
    public int deleteHistoryByFrenceId(Integer frenceid) {
        return frenceHistoryMapper.deleteHistoryByFrenceId(frenceid);
    }

    @Override
    public PageInfo<FrenceHistory> getFrenceHistoryByFrenceId(Integer pageIndex, Integer pageSize, Integer id, Integer frenceId) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<FrenceHistory> list = frenceHistoryMapper.getFrenceHistory(frenceId, id);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<FrenceHistory> getFrenceHistoryByPersonIdCard(Integer pageIndex, Integer pageSize, Integer id, String idCard) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<FrenceHistory> list = frenceHistoryMapper.getFrenceHistoryByPersonIdCard(id,idCard);
        return new PageInfo<>(list);
    }




    @Override
    public List<FrenceHistory> getFrenceHistorysByPersonIdCard(Integer id, String idCard) {
        return frenceHistoryMapper.getFrenceHistorysByPersonIdCard(id,idCard);
    }

    @Override
    public PageInfo<FrenceHistory> getFrenceHistoryByPersonIdcardss(Integer pageIndex, Integer pageSize, List<String> list) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<FrenceHistory> frenceHistoryByPersonIdCardss = frenceHistoryMapper.getFrenceHistoryByPersonIdCardss(list);

        return new PageInfo<>(frenceHistoryByPersonIdCardss);
    }

    @Override
    public PageInfo<FrenceHistoryVO> getFrenceHistoryByListPage(Integer pageIndex, Integer pageSize, List<FrenceHistoryVO> frenceVOList) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);

        return new PageInfo<>(frenceVOList);
    }

    @Override
    public PageInfo<FrenceHistory> getFrenceHistoryByFrenceIdAndPersonName(Integer pageIndex, Integer pageSize, Integer frenceId, List<String> list) {
        //设置分页
        PageHelper.startPage(pageIndex,pageSize);
        List<FrenceHistory> getFrenceHistoryByFrenceIdAndPersonName = frenceHistoryMapper.getFrenceHistoryByFrenceIdAndPersonName(frenceId,list);

        return new PageInfo<>(getFrenceHistoryByFrenceIdAndPersonName);
    }

    @Override
    public int updateBatch(Integer id, List<Integer> idsList) {
        return frenceHistoryMapper.updateBatch(id,idsList);
    }


}
