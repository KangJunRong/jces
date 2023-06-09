package com.ecp.jces.file.util;


import com.ecp.jces.code.ResultCode;
import com.ecp.jces.common.TypeDict;
import com.ecp.jces.core.utils.HexUtil;
import com.ecp.jces.exception.FrameworkRuntimeException;

import com.ecp.jces.global.LogMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @program: JCES
 * @description:
 * @create: 2020-08-19 15:04
 **/
@Component
public class FileUtils {
    /**
     * 最大文件长度20M
     */
    private static final int MAX_FILE_SIZE = 20971520;
    /*
    * 最大EXE文件长度
    **/
    private static final int MAX_EXE_SIZE = 104857600;
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    @Value("${param.jces.uploadUrl:/opt/jces/upload/}")
    private String uploadUrl;

    public String uploadExe(byte[] array, String filename, String childPath){
        try {
            if (array.length > MAX_EXE_SIZE) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "上传的文件过大");
            }
            // 构造永久路径来存储上传的文件
            // 这个路径相对当前应用的目录
            String uploadPath = uploadUrl + childPath + File.separator;
            // 如果目录不存在则创建
            File uploadDir = new File(uploadPath);
            if (!uploadDir.isDirectory()) {
                uploadDir.mkdirs();
            }
            log.info(LogMsg.to("uploadPath", uploadPath));
            log.info(LogMsg.to("filename", filename));

            filename = filename.substring(0, filename.lastIndexOf(".")) +
                    filename.substring(filename.lastIndexOf("."), filename.length());
            String filePath = uploadPath + filename;
            Files.write(Paths.get(filePath), array);
            return filePath;
        } catch (Exception e) {
            log.error("uploadFile error", e);
            throw new FrameworkRuntimeException(ResultCode.Fail, e.getMessage());
        }
    }

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
                uploadDir.mkdirs();
            }
            log.info(LogMsg.to("uploadPath", uploadPath));
            log.info(LogMsg.to("filename", filename));

            filename = filename.substring(0, filename.lastIndexOf(".")) +
                    filename.substring(filename.lastIndexOf("."), filename.length());
            String filePath = uploadPath + filename;
            Files.write(Paths.get(filePath), array);
            return filePath;
        } catch (Exception e) {
            log.error("uploadFile error", e);
            throw new FrameworkRuntimeException(ResultCode.Fail, e.getMessage());
        }
    }

    /**
     * 读取源文件内容
     *
     * @param filename String 文件路径
     * @return byte[] 文件内容
     * @throws IOException
     */
    public static byte[] readFile(String filename) throws IOException {
        if (filename == null || "".equals(filename)) {
            throw new IllegalArgumentException("无效的文件路径");
        }
        File file = new File(filename);
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            long len = file.length();
            byte[] bytes = new byte[(int) len];
            int r = bufferedInputStream.read(bytes);
            if (r != len) {
                throw new IllegalArgumentException("读取文件不正确");
            }
            bufferedInputStream.close();
            return bytes;
        }
    }

    public static String checkFileType(byte[] bytes) throws Exception {
        byte[] b = new byte[4];
        System.arraycopy(bytes, 0, b, 0, 4);
        String hexStr = HexUtil.byteArr2HexStr(b);
        hexStr = hexStr.toUpperCase();
        log.info("头文件是：" + hexStr);
        return TypeDict.checkType(hexStr);
    }

    public static boolean deleteAllFile(String dir) {
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            log.info("删除文件夹失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子文件夹
        File[] files = dirFile.listFiles();
        assert files != null;
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子文件夹
            else if (file.isDirectory()) {
                flag = deleteAllFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            log.info("删除文件夹" + dir + "失败！");
            return false;
        }
        // 删除当前文件夹
        if (dirFile.delete()) {
            log.info("删除文件夹" + dir + "成功！");
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
                log.info("删除文件" + fileName + "成功！");
                return true;
            } else {
                log.info("删除文件" + fileName + "失败！");
                return false;
            }
        } else {
            log.info(fileName + "不存在！");
            return false;
        }
    }
}
