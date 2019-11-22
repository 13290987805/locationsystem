package com.tg.locationsystem.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/7/11
 */
@Data
public class TestUser implements Serializable {

    private Integer id;

    private String name;

    private  String pass;
}
