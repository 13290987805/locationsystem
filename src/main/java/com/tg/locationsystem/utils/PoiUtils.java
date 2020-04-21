package com.tg.locationsystem.utils;

import com.tg.locationsystem.entity.Tag;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2020/4/14
 */
public class PoiUtils {
    public static List<Tag> importTagByExcel(MultipartFile file) {
        Tag tag ;//定义员工POJO类
        List<Tag> tagList = new ArrayList<>();//解析出来的封装好要返回的对象

        if (file.isEmpty()) {//先判断客户端上传的excel有没有效
           return  null;
        }
        try {
            if (file.getOriginalFilename().endsWith("xls")){
                //获得上传的excel文件
                HSSFWorkbook workbook =
                        new HSSFWorkbook(new POIFSFileSystem(file.getInputStream()));
                //获取第一个sheet
                HSSFSheet sheet=workbook.getSheetAt( 0);
                //获取行数
                int rowNums=sheet.getPhysicalNumberOfRows();
                //遍历行数
                for (int i = 1; i < rowNums; i++) {
                    //得到该行的数据
                    HSSFRow row = sheet.getRow(i);
                    tag=new Tag();
                    for (int j = 0; j < row.getLastCellNum(); j++) {

                        if (j==0){
                            HSSFCell cell = row.getCell(j);
                            tag.setAddress(String.valueOf(cell));
                        }
                        if (j==1){
                            HSSFCell cell = row.getCell(j);
                            String type = String.valueOf(cell);
                            if ("手表".equals(type)){
                                tag.setTagTypeid(1);
                            }else if ("工卡".equals(type)){
                                tag.setTagTypeid(2);
                            }else if ("资产标签".equals(type)){
                                tag.setTagTypeid(3);
                            }else {
                                return null;
                            }
                        }
                    }
                    tagList.add(tag);
                }

            }else if (file.getOriginalFilename().endsWith("xlsx")){
                //获得上传的excel文件
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

                //获取第一个sheet
                XSSFSheet sheet=workbook.getSheetAt( 0);
                //获取行数
                int rowNums=sheet.getPhysicalNumberOfRows();

                //遍历行数
                for(int i= 1;i<rowNums;i++) {
                    //得到该行的数据
                    XSSFRow row = sheet.getRow(i);
                    tag=new Tag();
                    for (int j=0;j<row.getLastCellNum();j++){
                        if (j==0){
                            XSSFCell cell = row.getCell(j);
                            tag.setAddress(String.valueOf(cell));
                        }
                       if (j==1){
                           XSSFCell cell = row.getCell(j);
                           String type = String.valueOf(cell);
                           if ("手表".equals(type)){
                               tag.setTagTypeid(1);
                           }else if ("工卡".equals(type)){
                               tag.setTagTypeid(2);
                           }else if ("资产标签".equals(type)){
                               tag.setTagTypeid(3);
                           }else {
                               return null;
                           }
                       }
                    }
                    tagList.add(tag);

                }

            }else {
                return null;
            }


        }catch (Exception e){
            return  null;
        }
        return tagList;
    }
}
