package com.tg.locationsystem.utils.test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/24
 */
public class BuildTree {
    public static <T> Tree<T> build(List<Tree<T>> nodes) {

        if(nodes == null){
            return null;
        }
        List<Tree<T>> topNodes = new ArrayList<Tree<T>>();
        List<Tree<T>> topNodes2 = new ArrayList<Tree<T>>();

        for (Tree<T> children : nodes) {

            String pid = children.getParentId();
            if (pid == null || "".equals(pid) ) {
                topNodes.add(children);

                continue;
            }else if (pid.equals("0")){
                topNodes2.add(children);
            }

            for (Tree<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    continue;
                }
            }

        }

        Tree<T> root = new Tree<T>();
        //System.out.println("topNodes个数:"+topNodes.size());
        if (topNodes.size() == 1) {
            root = topNodes.get(0);
        } else {
            root=topNodes2.get(0);
            /*root.setItemtype_id("-1");
            root.setParent_itemtypeid("");
            root.setChildren(topNodes);
            root.setItemtype_name("顶级节点");*/
        }

        return root;
    }


}
