/**
 * @description :
 * @date : 2018年2月28日 下午2:33:03
 */
package com.ecp.jces.core.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AesUtil2 {

    private static String html_aesKey = "CmccJavaAes16Enc";

    /**
     * PKCS5Padding -- Pkcs7 两种padding方法都可以
     *
     * @param content 3c2b1416d82883dfeaa6a9aa5ecb8245  16进制
     * @param content
     * @return
     */
    public static String decryptAES2(String content) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(html_aesKey.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // "算法/模式/补码方式"
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            return new String(cipher.doFinal(parseHexStr2Byte(content)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES加密
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptData(String data){
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(html_aesKey.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);// 初始化
            //return new BASE64Encoder().encode(cipher.doFinal(data.getBytes()));
            return HexUtil.byteArr2HexStr(cipher.doFinal(data.getBytes()));
        }catch (Exception e){
            return null;
        }

    }


    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException {
        /*
         * 解密
         */
        System.out.println(decryptAES2("c4469bda845df0fcacd223e55f17a117"));
        /*
         * 加密
         */
        System.out.println(encryptData("1234568822"));
    }
}
