package com.ecp.jces.server.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncrypt {

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String publicKeyEncrypt(String str, String publicKey,String point) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").
                generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @param point
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String privateKeyDecrypt(String str, String privateKey, String point) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }


    /**
     * RSA私钥加密
     *
     * @param str
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String privateKeyEncrypt(String str, String privateKey,String point) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        PrivateKey priKey = KeyFactory.getInstance("RSA").
                generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, priKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes()));
        return outStr;
    }

    /**
     * RSA公钥解密
     *
     * @param str
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String publicKeyDecrypt(String str, String publicKey,String point) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        PublicKey pubKey =  KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    public static void main(String[] args) throws Exception {
        String point = "test";
        //生成公钥和私钥
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC62qHlLssgFr3YjRJ3XJbI9z4sngP4UJY5RwZVGBkDeIlncyOOXeAmL22UwwCI/R/Qk1ZxjPVrNzk401ohfvkzyOqLI6lZk+drRc6tgfNtBP4DncOFt5ljpg7P/IiauPVnzl1+aj/M+igmHzlk6XFefP0ZAyL2lAnY9HpsYt45IQIDAQAB";
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALraoeUuyyAWvdiNEndclsj3PiyeA/hQljlHBlUYGQN4iWdzI45d4CYvbZTDAIj9H9CTVnGM9Ws3OTjTWiF++TPI6osjqVmT52tFzq2B820E/gOdw4W3mWOmDs/8iJq49WfOXX5qP8z6KCYfOWTpcV58/RkDIvaUCdj0emxi3jkhAgMBAAECgYBsXY+20sSK/6VqiaZEAzaPppQwXBQBVvtIjKBniWo4URefH29niZoNi0QKGm2dBPAGjux7Pjy2yXguNKT4+CKdoCCPL8w4wVctZydh2zvYxFROHm3fAjZxX/T4jnHdjvm8UHc1SSsp1Zbp0atGWj3M256+QyV1wJvCTyADvXUAAQJBANrI/zvUkv8zTQFF9YELhfNWQY0GwJFvSvVJHVkH+jALbQVh0PUWpVESMgjxOJeEcVAqeYsLaHa4KQdI5UGt+gECQQDaozHPZiY9hU1RtAjB0uo/NNy4IE9cAthdUWn7GbyaNx5VU1xqHJRyLG1EUWMVECwZ8HYuhB/GO7ytbwsbav8hAkBDl2s6+b58N0YoCYwCVSQJ/HD998MQa1i2FOOLOOqzYyRIUGL1BQ0H0d5BYvy/2rwDL0wjh9+7JcQPKxcgxnYBAkB9WonpdpDAPxh4mrdWoZ3qhV1zOBsoDv2Ma2xPKo9m/+klVLksoZw+5df0DpG3duFCMgsgWmJ5XrCr43nDy5HhAkBP2HcJa6BZLP0zQgjrv0z6LOP4PVlD1ERXwe8tNigBmYeHtSrcPnZz3dHeGuoMSNhcnpyevgq1LcHEfIiHkgug";
        System.out.println("point:"+point);
        System.out.println("公密:"+publicKey);
        System.out.println("私密:"+privateKey);
        //待加密文本
        JSONObject param = new JSONObject();
        param.put("addTime","20220208");
        param.put("account","demo");
        System.out.println("原数据:"+param.toJSONString());
        System.out.println("=====================");
        String message1En = publicKeyEncrypt(param.toJSONString(), publicKey,point);
        System.out.println("公钥加密:" + message1En);
        String message1De = privateKeyDecrypt(message1En, privateKey,point);
        System.out.println("私钥解密:" + message1De);
        System.out.println("=====================");
        String message2En = privateKeyEncrypt(param.toJSONString(), privateKey,point);
        System.out.println("私钥加密:"+message2En);
        String message2De = publicKeyDecrypt(message2En, publicKey,point);
        System.out.println("公钥解密:"+message2De);


        String pointT = "researchHome2022@test";
        String publicKey2 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKaHugW3mdLrxf2C0aS+HeNNqlG3oJgkX4Z56VEw5ir9ZEGpwLGUR1TDHz8+Un+I4NwuyB+Vy3elJy3t8dAVOket/6Y2OkRrq0LoS4fCHi59MttArQpkdL5at0b/AWRyXskQ/6tJTB3bvY11Lzy5gvcujCwA7IThyUDM/utLnEUQIDAQAB";

        String message4En = "TeDPULpQSL+FCN4B2hGvB4tA6pqqWEg5pyJ9GCEkXbGHB3T59gFdAnEUGVPK0R6d63P6Px5eQbpy5UUBrir1JsT2h1FZceKd53JVif5BX/fytCT0ztySgDj2trqyJIAX34/KlOZEedWlN4VXtaPnmvopxybrEBc1R+kP7TfMLx8=";
        String message3De = publicKeyDecrypt(message4En, publicKey2,pointT);
        System.out.println("公钥解密:"+message3De);


        String message5En = publicKeyEncrypt("baiyang", publicKey2,pointT);
        System.out.println("公钥加密:"+message5En);

    }
}
