package com.tg.locationsystem.pojo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author hyy
 * @ Date2019/12/24
 */
public class QueryTagStatusVO implements Serializable{

    private String idCards;

    private String isDeal;

    private Integer alert_type;

    public QueryTagStatusVO() {
    }


    public String getIdCards() {
        return idCards;
    }

    public void setIdCards(String idCards) {
        this.idCards = idCards;
    }

    public String getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(String isDeal) {
        this.isDeal = isDeal;
    }

    public Integer getAlert_type() {
        return alert_type;
    }

    public void setAlert_type(Integer alert_type) {
        this.alert_type = alert_type;
    }

    @Override
    public String toString() {
        return "QueryTagStatusVO{" +
                "idCards='" + idCards + '\'' +
                ", isDeal='" + isDeal + '\'' +
                ", alert_type=" + alert_type +
                '}';
    }
}
