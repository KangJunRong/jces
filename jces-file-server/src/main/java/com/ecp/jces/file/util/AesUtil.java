/**
 * @description :
 * @date : 2018年2月28日 下午2:33:03
 */
package com.ecp.jces.file.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AesUtil {

    public static String CIPHER_ALGORITHM = "AES";

    //解决windows与linux下加解密不一样
    private static Key getKey(String strKey) {
        try {
            if (strKey == null) {
                strKey = "";
            }
            KeyGenerator _generator = KeyGenerator.getInstance(CIPHER_ALGORITHM);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(strKey.getBytes());
            _generator.init(128, secureRandom);
            return _generator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(" 初始化密钥出现异常 ");
        }
    }

    /*
     * 加密
     * 1.构造密钥生成器
     * 2.根据ecnodeRules规则初始化密钥生成器
     * 3.产生密钥
     * 4.创建和初始化密码器
     * 5.内容加密
     * 6.返回字符串
     */
    public static String AESEncode(String encodeRules, String content) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        SecureRandom sr = new SecureRandom();
        Key secureKey = getKey(encodeRules);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secureKey, sr);
        byte[] bt = cipher.doFinal(content.getBytes());
        return new BASE64Encoder().encode(bt);
    }

    /*
     * 解密
     * 解密过程：
     * 1.同加密1-4步
     * 2.将加密后的字符串反纺成byte[]数组
     * 3.将加密内容解密
     */
    public static String AESDncode(String encodeRules, String content) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        SecureRandom sr = new SecureRandom();
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        Key secureKey = getKey(encodeRules);
        cipher.init(Cipher.DECRYPT_MODE, secureKey, sr);
        byte[] res = new BASE64Decoder().decodeBuffer(content);
        res = cipher.doFinal(res);
        return new String(res);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException {
        /*
         * 加密
         */
        System.out.println("使用AES对称加密，请输入加密的规则");
        String encodeRules = "7pA1OhvF7mi0rySCmnXQxZUpMdntbBeyEMtQ8860HfYh4qx9CTOpIkKN6YxUxoiP";
        System.out.println("请输入要加密的内容:");
        String content = "Qwe@7332371";

        String data = AESEncode(encodeRules, content);
        System.out.println("根据输入的规则   " + encodeRules + " 加密后的密文是:" + data);

       /*解密*/
        System.out.println("使用AES对称解密，请输入加密的规则：(须与加密相同)");
        encodeRules = "7pA1OhvF7mi0rySCmnXQxZUpMdntbBeyEMtQ8860HfYh4qx9CTOpIkKN6YxUxoiP";
        System.out.println("请输入要解密的内容（密文）:");
        content = data;
        System.out.println("根据输入的规则  " + encodeRules + "  解密后的明文是:" + AESDncode(encodeRules, content));

    }
}
