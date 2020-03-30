package com.tg.locationsystem.utils.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hyy
 * @ Date2020/3/24
 */
public class Test {

    public static void main(String[] args) {
        List<Tree<Test>> trees = new ArrayList<Tree<Test>>();
        List<Test> tests = new ArrayList<Test>();
        tests.add(new Test("0", "", "关于本人"));
        tests.add(new Test("1", "0", "技术学习"));
        tests.add(new Test("2", "0", "兴趣"));
        tests.add(new Test("3", "1", "JAVA"));
        tests.add(new Test("4", "1", "oracle"));
        tests.add(new Test("5", "1", "spring"));
        tests.add(new Test("6", "1", "springmvc"));
        tests.add(new Test("7", "1", "fastdfs"));
        tests.add(new Test("8", "1", "linux"));
        tests.add(new Test("9", "2", "骑行"));
        tests.add(new Test("10", "2", "吃喝玩乐"));
        tests.add(new Test("11", "2", "学习"));
        tests.add(new Test("12", "3", "String"));
        tests.add(new Test("13", "4", "sql"));
        tests.add(new Test("14", "5", "ioc"));
        tests.add(new Test("15", "5", "aop"));
        tests.add(new Test("16", "1", "等等"));
        tests.add(new Test("17", "2", "等等"));
        tests.add(new Test("18", "3", "等等"));
        tests.add(new Test("19", "4", "等等"));
        tests.add(new Test("20", "5", "等等"));

        for (Test test : tests) {
            Tree<Test> tree = new Tree<Test>();
            tree.setId(test.getId());
            tree.setParentId(test.getPid());
            tree.setTitle(test.getText());
            List<Map<String, Object>> lmp = new ArrayList<Map<String, Object>>();
            Map<String, Object> mp = new HashMap<String, Object>();
            /*mp.put("COSTDEVICE_NUMBER", "");
            mp.put("PRICE_PER", "");
            mp.put("ORDER_INDEX", "");
            mp.put("ADJUST_DATE", "");
            mp.put("IS_LEAF", "");*/
            lmp.add(mp);
            trees.add(tree);
        }

        Tree<Test> t = BuildTree.build(trees);
        System.out.println(t);
    }


    private String id;
    private String pid;
    private String text;

    public Test() {
    }

    public Test(String id, String pid, String text) {
        this.id = id;
        this.pid = pid;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
