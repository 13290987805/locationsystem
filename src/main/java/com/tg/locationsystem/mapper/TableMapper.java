package com.tg.locationsystem.mapper;

import com.tg.locationsystem.pojo.TestVO;
import org.apache.ibatis.annotations.Insert;
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
    Object TEST_VO2(Integer id);


    //ALTER TABLE post/*post:表名*/ ADD COLUMN h_id/*h_id:列名*/ INT;
    @Insert("ALTER TABLE test ADD COLUMN age INT")
    int insertField();

    //查询表字段
    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=#{tableName}")
    List<Map> listTableColumn(String tableName);
}
