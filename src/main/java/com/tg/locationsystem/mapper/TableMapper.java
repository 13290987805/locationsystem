package com.tg.locationsystem.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author hyy
 * @ Date2019/10/21
 */
public interface TableMapper {
    @Select("select TABLE_NAME from information_schema.TABLES where TABLE_SCHEMA=(select database())")
    List<String> listTable();
}
