package com.tg.locationsystem.mapper;

import com.tg.locationsystem.pojo.TestVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author hyy
 * @ Date2019/10/21
 */
public interface TableMapper {
    @Select("select TABLE_NAME from information_schema.TABLES where TABLE_SCHEMA=(select database())")
    List<String> listTable();

    @Select("SELECT\n" +
            "\ttag.address,\n" +
            "\ttag.map_key,\n" +
            "\ttag.x,\n" +
            "\ttag.y,\n" +
            "\tperson.person_name\n" +
            "FROM\n" +
            "\ttag\n" +
            "LEFT JOIN person ON tag.address = person.tag_address\n" +
            "WHERE\n" +
            "\ttag.id = #{id}")
    TestVO TEST_VO(Integer id);
}
