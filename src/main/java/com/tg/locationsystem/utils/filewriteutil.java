package com.tg.locationsystem.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author hyy
 * @ Date2019/8/7
 */
public class filewriteutil {
/*
* 使用java中的转义符"\r\n"

比较方便的用法是System.getProperty("line.separator")。
* */
    public static void filewrite(String str, String ResultfilePath) throws IOException {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(ResultfilePath, true);
            writer.write(str + System.getProperty("line.separator"));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {

        }

    }
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            //System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            //System.out.println("创建目录" + destDirName + "成功！");
            return true;
        } else {
           // System.out.println("创建目录" + destDirName + "失败！");
            return false;
        }
    }

}
