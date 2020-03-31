package com.tg.locationsystem.utils.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hyy
 * @ Date2020/3/24
 */
public class Tree<T> {
    /**
     * 节点ID
     */
    private String id;
    /**
     * 显示节点文本
     */
    private String title;
    /**
     * 节点属性
     */
    //private List<Map<String, Object>> attributes;
    /**
     * 节点的子节点
     */
    private List<Tree<T>> children = new ArrayList<Tree<T>>();

    /**
     * 父ID
     */
    private String parentId;

    private String checkArr="0";

    public Tree() {
    }

    public String getId() {
        return id;
    }

    public String getCheckArr() {
        return checkArr;
    }

    public void setCheckArr(String checkArr) {
        this.checkArr = checkArr;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<Tree<T>> getChildren() {
        return children;
    }

    public void setChildren(List<Tree<T>> children) {
        this.children = children;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Tree(String id, String title, List<Tree<T>> children, String parentId) {
        this.id = id;
        this.title = title;
        this.children = children;
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", children=" + children +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}
