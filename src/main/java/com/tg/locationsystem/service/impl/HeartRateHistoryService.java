package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.HeartRateHistory;
import com.tg.locationsystem.entity.Person;
import com.tg.locationsystem.mapper.HeartRateHistoryMapper;
import com.tg.locationsystem.pojo.HeartRateHistoryVO;
import com.tg.locationsystem.service.IHeartRateHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/5
 */
@Service
public class HeartRateHistoryService extends BaseServiceImpl<HeartRateHistory> implements IHeartRateHistoryService{
    @Autowired
    private HeartRateHistoryMapper heartRateHistoryMapper;
    @Autowired
    private PersonService personService;
    @Override
    public IBaseDao<HeartRateHistory> getBaseDao() {
        return heartRateHistoryMapper;
    }

    @Override
    public List<HeartRateHistory> getheartRateHistoryByCondition(String address, String startTime, String endTime) {
        return heartRateHistoryMapper.getheartRateHistoryByCondition(address,startTime,endTime) ;
    }

    @Override
    public List<HeartRateHistoryVO> getSomeheartRateHistory() {
        List<HeartRateHistoryVO> list=new ArrayList<>();
        List<HeartRateHistory> histories1=heartRateHistoryMapper.getSomeheartRateHistory("995081CF0001CADE");
        HeartRateHistory heartRateHistory1 = histories1.get(0);
        HeartRateHistoryVO heartRateHistoryVO1=new HeartRateHistoryVO();
        heartRateHistoryVO1.setId(heartRateHistory1.getId());
        heartRateHistoryVO1.setAddTime(heartRateHistory1.getAddTime());
        heartRateHistoryVO1.setPersonIdcard(heartRateHistory1.getPersonIdcard());
        heartRateHistoryVO1.setTagData(heartRateHistory1.getTagData());
        heartRateHistoryVO1.setUserId(heartRateHistory1.getUserId());
        Person person1=personService.getPersonByIdCard(heartRateHistory1.getPersonIdcard());
        if (person1!=null){
            heartRateHistoryVO1.setName(person1.getPersonName());
        }
        list.add(heartRateHistoryVO1);

        List<HeartRateHistory> histories2=heartRateHistoryMapper.getSomeheartRateHistory("165081CF0001CADE");
        HeartRateHistory heartRateHistory2 = histories2.get(0);
        HeartRateHistoryVO heartRateHistoryVO2=new HeartRateHistoryVO();
        heartRateHistoryVO2.setId(heartRateHistory2.getId());
        heartRateHistoryVO2.setAddTime(heartRateHistory2.getAddTime());
        heartRateHistoryVO2.setPersonIdcard(heartRateHistory2.getPersonIdcard());
        heartRateHistoryVO2.setTagData(heartRateHistory2.getTagData());
        heartRateHistoryVO2.setUserId(heartRateHistory2.getUserId());
        Person person2=personService.getPersonByIdCard(heartRateHistory2.getPersonIdcard());
        if (person1!=null){
            heartRateHistoryVO2.setName(person2.getPersonName());
        }
        list.add(heartRateHistoryVO2);

        List<HeartRateHistory> histories3=heartRateHistoryMapper.getSomeheartRateHistory("9D5081CF0001CADE");
        HeartRateHistory heartRateHistory3 = histories3.get(0);
        HeartRateHistoryVO heartRateHistoryVO3=new HeartRateHistoryVO();
        heartRateHistoryVO3.setId(heartRateHistory3.getId());
        heartRateHistoryVO3.setAddTime(heartRateHistory3.getAddTime());
        heartRateHistoryVO3.setPersonIdcard(heartRateHistory3.getPersonIdcard());
        heartRateHistoryVO3.setTagData(heartRateHistory3.getTagData());
        heartRateHistoryVO3.setUserId(heartRateHistory3.getUserId());
        Person person3=personService.getPersonByIdCard(heartRateHistory3.getPersonIdcard());
        if (person1!=null){
            heartRateHistoryVO3.setName(person3.getPersonName());
        }
        list.add(heartRateHistoryVO3);
        return list;
    }

    @Override
    public int deleteHistory(String tableName, String address) {
        return heartRateHistoryMapper.deleteHistory(tableName,address);
    }

    @Override
    public List<HeartRateHistory> getheartRateHistoryByPersonIdcards(List<String> personIdcard) {
        return heartRateHistoryMapper.getheartRateHistoryByPersonIdcards(personIdcard) ;
    }
}
