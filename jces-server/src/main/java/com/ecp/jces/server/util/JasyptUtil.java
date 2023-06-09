package com.ecp.jces.server.util;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

public class JasyptUtil {


    /*public static void main(final String[] args) {
        String pass = "SAS#1234@ecp";// 待加密的明文密码
        try {
            String mima = encode(pass);
            System.out.println("【" + pass + "】被加密成【" + mima + "】");

            String jiemi = decode(mima);
            System.out.println("【" + mima + "】被解密成【" + jiemi + "】");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static String encode(String pass) throws Exception {
        StringEncryptor stringEncryptor = JasyptUtil.getInstance();
        return stringEncryptor.encrypt(pass);
    }

    public static String decode(String pass) throws Exception {
        StringEncryptor stringEncryptor = JasyptUtil.getInstance();
        return stringEncryptor.decrypt(pass);
    }

    private static StringEncryptor stringEncryptor = null;//org.jasypt.encryption.StringEncryptor对象

    public static StringEncryptor getInstance() throws Exception {
        if (stringEncryptor == null) {
            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            config.setPassword("miyao");
            config.setPoolSize("1");
            encryptor.setConfig(config);

            stringEncryptor = encryptor;
        }
        return stringEncryptor;
    }
}
