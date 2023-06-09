package com.ecp.jces.server.util;

import com.alibaba.fastjson.JSONObject;
import com.ecp.jces.core.utils.HexUtil;
import com.ecp.jces.form.ApiForbiddenForm;
import com.ecp.jces.vo.ApiForbiddenVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RegCodeUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegCodeUtil.class);

//   private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJy/DuXHNe7IMTbLnjgVtNf5bekHehPR1r1ElVVbJnHDUXFJyFjRGC8/tMTmFoH6vEPuoVyavOqF0EsNn3DQNtrWOcYd6bLSO+0HG+FA/rWtrmBkTlszfkHGa7chOYs0ioHjnazBhfHxnju0AqaJulb+zdGR3ESUzG846X8kCW67AgMBAAECgYEAmOGctS7tAp3NqoVFJ6y8O16z/fJl9kjXqrjm8l66C4GWwOlW+JHQhd0cAmPslhApahSo2/qswg2MEHgUB+N9a8AF7qauGbLC3fVlf2kUZXnYE2U0Ag27YwBeYZyDnjTQ1mpPbrcQaThtuCPyEWydtL/nQxpe4IdOmdoUPdZVUeECQQDk1pgdyDfjSQQXDogI18R42T+YAdgpWlJqWKM/rzfa+zskHabp/daIh6i8qflKEFBNRiVzMF8REworvNfXajQxAkEAr1nmy057dGAT6egfkum1Wf4Aabcvv/WDx6RLAWcabQP4svkl8pOYmw3ZWrt0BPFy55Sk8swh+v6yFXXVCZcyqwJBALlQTr9PdHLKnlVNnzwJ/0EeLTGbzMEwM62LOihIbKMY8Du/B3xV43WM5khiHU6tB1qMg6dUdKgPEx14QBJiHwECQD2WS9NYS1ACdrwOxRgqcYGcDrUSm/jF2HUEJTvm5Icpf+4MR+G9KTo8UPznHcamrv7bXFvSipTHY/DZo+tsaWUCQCcYMhVqzoxKGzGEnqCRV01VhjX1PepE4aQnP4EYSJ6tij3sDM0MT9AatY+eKA4SXYuqWnSb6XGDuSuH68jcTkA=";
//    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcvw7lxzXuyDE2y544FbTX+W3pB3oT0da9RJVVWyZxw1FxSchY0RgvP7TE5haB+rxD7qFcmrzqhdBLDZ9w0Dba1jnGHemy0jvtBxvhQP61ra5gZE5bM35Bxmu3ITmLNIqB452swYXx8Z47tAKmibpW/s3RkdxElMxvOOl/JAluuwIDAQAB";

    private static final String LIC_AES_KEY = "8e73b0f7da0e6452c810f32b809079e5";
    private static final String LIC_AES_ICV = "00000000000000000000000000000756";

    /**
     * @param machineCode 机器码
     * @param effectDate  生效时间，格式：yyyy-MM-dd
     * @param expiryDate  失效时间，格式：yyyy-MM-dd
     * @return
     */
    public static String genLicense(String machineCode, String effectDate, String expiryDate, List<ApiForbiddenForm> list,String PRIVATE_KEY) {
        JSONObject lic = new JSONObject();
        lic.put("machineCode", machineCode);
        lic.put("effectDate", effectDate);
        lic.put("expiryDate", expiryDate);
        lic.put("role", "appDev");

        if (list != null && list.size() > 0) {
            lic.put("list", list);
        }


        //AES 加密
        byte[] ciphertext = null;
        try {
            IvParameterSpec iv = new IvParameterSpec(HexUtil.hexStr2ByteArr(LIC_AES_ICV));
            SecretKeySpec skeySpec = new SecretKeySpec(HexUtil.hexStr2ByteArr(LIC_AES_KEY), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            ciphertext = cipher.doFinal(lic.toJSONString().getBytes());
        } /*catch (NoSuchAlgorithmException ex) {

        } catch (NoSuchPaddingException ex) {

        }*/ catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }

        //签名
        PrivateKey privateKey = null;
        String sign = null;
        try {
            privateKey = generatePrivateKeyByStr(PRIVATE_KEY, "RSA");
            Signature signature = Signature.getInstance("MD5withRSA");

            signature.initSign(privateKey);

            signature.update(ciphertext);
            byte[] result = signature.sign();
            sign = Base64.getEncoder().encodeToString(result);
            System.out.println("sign: " + sign);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }


        return Base64.getEncoder().encodeToString(ciphertext) + sign;
    }

    private static PrivateKey generatePrivateKeyByStr(String privateKeyStr, String algorithm) throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);

        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        return rsaPrivateKey;

    }

/*    public static void main(String[] args) {
        List<ApiForbiddenForm> list = new ArrayList<>();
        System.out.println(genLicense("UD0TA-JSBT9-M7HVP-5ZIXG", "2021-02-03", "2021-12-03", list));
    }*/

}
