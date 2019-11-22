package com.tg.locationsystem.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author hyy
 * @ Date2019/6/27
 */
public class UploadFileUtil {
    private static final Logger log = LoggerFactory.getLogger(UploadFileUtil.class);

    /**
     * 验证文件是否已经存在
     * @param uploadPath
     * @param fileName
     */
    public static String checkFile(String uploadPath,String fileName) {
        Map<String, Object> data = new HashMap<String, Object>();
        boolean result = Boolean.TRUE;
        try {
            String filePath = uploadPath + File.separator + fileName;
            File date = new File(filePath);
            if (date.exists()) {
                data.put("isExists", Boolean.TRUE);
                data.put("fileMsg", "文件已存在!是否覆盖!");
            } else {
                data.put("isExists", Boolean.FALSE);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = Boolean.FALSE;
            data.put("errorMsg", "文件上传失败!");
        }
        data.put("success", result);
        String jsonStr = JSONObject.fromObject(data).toString();
        return jsonStr;
    }

    /**
     * 文件上传
     * @param uploadfile
     * @param request
     * @param uploadPath
     * @param fileName
     */
    public static String uploadFile(MultipartFile uploadfile, String uploadPath,String fileName) {
        Map<String, Object> data = new HashMap<String, Object>();
        boolean result = Boolean.TRUE;
        String uploadFileName = null;
        try {
            InputStreamReader read = new InputStreamReader(uploadfile.getInputStream(), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            StringBuffer jsonContent = new StringBuffer();
            String content = "";
            while ((content = bufferedReader.readLine()) != null) {
                jsonContent.append(content);
            }
            String filePath = uploadPath + File.separator + fileName;
            File date = new File(filePath);
            FileOutputStream fop = null;
            fop = new FileOutputStream(date);
            byte[] contentInBytes = jsonContent.toString().getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
            read.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = Boolean.FALSE;
            data.put("errorMsg", "文件上传失败!");
        }
        data.put("success", result);
        data.put("resultMsg", uploadFileName + "上传成功!");
        String jsonStr = JSONObject.fromObject(data).toString();
        return jsonStr;
    }

    public static void isChartPathExist(String dirPath) {

         File file = new File(dirPath);
        if (!file.exists()) {
        file.mkdirs();
    }
 }

}
