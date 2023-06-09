package com.ecp.jces.file.util;

import com.ecp.jces.code.ConfigKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


/**
 * @author kangjunrong
 */
public class StrUtil {
    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static String getValue(Map<String, List<String>> params, String paramname) {
        if (!params.isEmpty()) {
            List<String> value = params.get(paramname);
            if (value == null) return "";
            return value.get(0);
        }
        return "";
    }

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

    public static <T> String join(String delimiter, List<T> values) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(values.get(i));
        }
        return sb.toString();
    }

    public static String newGuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getSixRandom() {
        Random random = new Random();
        String code = String.valueOf(random.nextInt(999999));
        while (code.length() != 6) {
            code = String.valueOf(random.nextInt(999999));
        }
        return code;
    }

    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }

    public static boolean isDigit(String str) {
        return str.matches("[0-9]+");
    }

    public static boolean isNumber(String str) {// 判断小数，与判断整型的区别在与d后面的小数点（红色）
        return str.matches("\\d+\\.\\d+$");
    }

    public static boolean isEmail(String str) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return str.matches(check);

    }


    /**
     * 验证手机号码
     *
     * @param str
     * @return
     */
    public static boolean isPhone(String str) {
        String check = "^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0,5-9]))\\d{8}$";
        return str.matches(check);
    }

    public static String idFormat(String id) {
        if (isBlank(id)) {
            return id;
        }
        if (id.length() < 15) {
            return id;
        }
        // 可以用 StringBuilder 这个类，里面有一个接口replace，如下
        StringBuilder sb = new StringBuilder(id);
        sb.replace(8, 14, "******");
        return sb.toString();
    }

    public static void getCallBackParam(Map<String, String> params, Map<String, String[]> requestParams) {
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
    }

    public static String decodeAes(String path) {
        try {
            return AesUtil.AESDncode(ConfigKey.PrivateKey, path);
        } catch (InvalidKeyException | BadPaddingException |
                NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | IOException e) {
            return null;
        }
    }
}
