package com.ecp.jces.jctool.util;

import com.ecp.jces.jctool.exception.HashCodeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashCodeUtil {

    public static String hashCode(File file) throws HashCodeException {

        if (!file.isFile()) {
            throw new HashCodeException("文件" + file.getPath() + "不是一个合法的文件。");
        }

        if (!file.exists()) {
            throw new HashCodeException("文件：" + file.getPath() + "不存在。");
        }

        try ( FileInputStream fis = new FileInputStream(file);) {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }

            byte[] md5Bytes  = md.digest();
            BigInteger bigInt = new BigInteger(1, md5Bytes);//1代表绝对值
            return bigInt.toString(16);//转换为16进制
        } catch (FileNotFoundException e) {
            throw new HashCodeException("文件：" + file.getPath() + "不存在。" + e);
        } catch (IOException e) {
            throw new HashCodeException("读取文件（" + file.getPath() + "）出错。" + e);
        } catch (NoSuchAlgorithmException e) {
            throw new HashCodeException("计算Hash code 出错：" + e);
        }
    }

    public static void main(String[] args) {
        System.out.println(HashCodeUtil.hashCode(new File("D:\\project\\jces\\开发日志.txt")));
    }

}
