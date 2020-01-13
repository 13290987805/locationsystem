package com.tg.locationsystem.pojo;

import com.tg.locationsystem.entity.EleCall;
import com.tg.locationsystem.entity.Person;

import java.io.Serializable;
import java.util.List;

/**
 * @author hyy
 * @ Date2020/1/7
 */
public class AreaEleCallVO implements Serializable {
    private Integer total;

    private Integer onLineTotal;

    private Integer NotonLineTotal;

    private List<PersonVO> onLineList;

    private List<PersonVO> NotonLineList;

    public AreaEleCallVO() {
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOnLineTotal() {
        return onLineTotal;
    }

    public void setOnLineTotal(Integer onLineTotal) {
        this.onLineTotal = onLineTotal;
    }

    public Integer getNotonLineTotal() {
        return NotonLineTotal;
    }

    public void setNotonLineTotal(Integer notonLineTotal) {
        NotonLineTotal = notonLineTotal;
    }

    public List<PersonVO> getOnLineList() {
        return onLineList;
    }

    public void setOnLineList(List<PersonVO> onLineList) {
        this.onLineList = onLineList;
    }

    public List<PersonVO> getNotonLineList() {
        return NotonLineList;
    }

    public void setNotonLineList(List<PersonVO> notonLineList) {
        NotonLineList = notonLineList;
    }
}
