package com.ecp.jces.server.util;

import com.ecp.jces.code.ConfigKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class StrUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrUtil.class);
    /**
     * key不可修改
     **/
    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isBlank(Integer value) {
        return value == null;
    }

    public static boolean isBlank(Long value) {
        return value == null;
    }

    public static boolean isBlank(Double value) {
        return value == null;
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static boolean isNotBlank(Double value) {
        return !isBlank(value);
    }

    public static boolean isNotBlank(Integer value) {
        return !isBlank(value);
    }

    public static boolean isNotBlank(Long value) {
        return !isBlank(value);
    }

    public static boolean isDigit(String str) {
        return str.matches("[0-9]+");
    }

    public static String newGuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    private static Pattern pMobile = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
//    private static Pattern pWeChatParams = Pattern.compile("\\{\\{[^\\}]*\\}\\}");

    public static String join(String delimiter, String[] values) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    /**
     * 手机号验证
     * 2016年12月5日下午4:34:46
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        if (isBlank(str)) {
            return false;
        }

        return pMobile.matcher(str).matches();
    }

    //重载一个join方法 ,操作list
    //内部不锁定,效率最高,但是当写多线程时要考虑并发操作的问题。
    public static String join(String delimiter, List<String> values) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(values.get(i));
        }
        return sb.toString();
    }

    //生成随机数 KJR
    public static String GetRandomNumberString(int weishu) {
        String str = "";
/*        str += (int) (Math.random() * 9 + 1);
        for (int i = 1; i < weishu; i++) {
            str += (int) (Math.random() * 10);
        }*/
        SecureRandom  random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1 );
        for (int i = 0 ; i<5;i++){
            sb.append(random.nextInt(10));
        }
        str = sb.toString();
        return str;
    }


    //生成文件夹 
    public static void makeDir(String path) {
        File file = new File(path);
        //如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            if(file.mkdir()){
                LOGGER.info(path + " 目录创建成功");
            }
            else {
                LOGGER.error(path + "   目录创建失败");
            }
        }
        file = null;
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            if(file.delete()){
                LOGGER.info(file.getName() + "删除成功");
            } else {
                LOGGER.error(file.getName() + "删除失败");
            }
            flag = true;
        }
        return flag;
    }


    public static boolean compare_date(String date1, String date2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;

        dt1 = sdf.parse(date1);
        dt2 = sdf.parse(date2);

        if (dt1.getTime() < dt2.getTime()) {
            return true;
        }
        return false;
    }

    public static String getFileNameFromUrl(String url) throws UnsupportedEncodingException {
        String deUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.name());
        String name = null;
        int index = deUrl.lastIndexOf(File.separator);
        if (index > 0) {
            name = deUrl.substring(index + 1);
            if (name.trim().length() > 0) {
                return name;
            }
        }
        return name;
    }

    public static String encodeAes(String str) {
        try {
            return AesUtil.AESEncode(ConfigKey.PrivateKey, str);
        } catch (InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException e) {
           LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static String decodeAes(String str) {
        try {
            return AesUtil.AESDncode(ConfigKey.PrivateKey, str);
        } catch (InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | IOException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}
