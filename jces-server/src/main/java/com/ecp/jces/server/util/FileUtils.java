package com.ecp.jces.server.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.ecp.jces.common.TypeDict;
import com.ecp.jces.core.utils.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.global.LogMsg;

/**
 * @program: cas
 * @description:
 * @create: 2020-08-19 15:04
 **/
@Component
public class FileUtils {
    /**
     * 最大文件长度20M
     */
    private static final int MAX_FILE_SIZE = 20971520;
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    @Value("${param.third.uploadUrl}")
    private String uploadUrl;

    public String uploadFile(byte[] array, String filename, String childPath) {
        try {
            if (array.length > MAX_FILE_SIZE) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "上传的文件过大");
            }
            // 构造永久路径来存储上传的文件
            // 这个路径相对当前应用的目录
            String uploadPath = uploadUrl + childPath + File.separator;
            // 如果目录不存在则创建
            File uploadDir = new File(uploadPath);
            if (!uploadDir.isDirectory()) {
                if(uploadDir.mkdirs()){
                    log.info(uploadDir.getName() +  "   创建成功");
                } else {
                    log.error(uploadDir.getName() + "   创建失败");
                }
            }
            log.info(LogMsg.to("uploadPath", uploadPath));
            log.info(LogMsg.to("filename", filename));

            filename = filename.substring(0, filename.lastIndexOf('.')) +
                    filename.substring(filename.lastIndexOf('.'), filename.length());
            String filePath = uploadPath + filename;
            Files.write(Paths.get(filePath), array);
            return filePath;
        } catch (Exception e) {
            log.error("uploadFile error", e);
            throw new FrameworkRuntimeException(ResultCode.Fail, e.getMessage());
        }
    }

    public String checkFileType(byte[] bytes) throws Exception {

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = bytes[i];
        }
        String hexStr = HexUtil.byteArr2HexStr(b);
        hexStr = hexStr.toUpperCase();
        System.out.println("头文件是：" + hexStr);
        return TypeDict.checkType(hexStr);
    }

    public boolean deleteAllFile(String dir) {
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除文件夹失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子文件夹
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子文件夹
            else if (files[i].isDirectory()) {
                flag = deleteAllFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除文件夹失败！");
            return false;
        }
        // 删除当前文件夹
        if (dirFile.delete()) {
            System.out.println("删除文件夹" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径只有单个文件
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println(fileName + "不存在！");
            return false;
        }
    }
}
