package com.tg.locationsystem.pojo;

import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/12/13
 */
public class QueryFrenceHistoryCondition implements Serializable {

    private Integer frenceId;

    private String personName;

    public QueryFrenceHistoryCondition() {
    }

    public Integer getFrenceId() {
        return frenceId;
    }

    public void setFrenceId(Integer frenceId) {
        this.frenceId = frenceId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public String toString() {
        return "QueryFrenceHistoryCondition{" +
                "frenceId=" + frenceId +
                ", personName='" + personName + '\'' +
                '}';
    }
}
