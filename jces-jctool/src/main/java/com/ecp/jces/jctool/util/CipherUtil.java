package com.ecp.jces.jctool.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class CipherUtil {
	
	 private static final String DES_EDE_ECB = "DESede/ECB/NoPadding";               // 定义 加密算法,可用 DES,DESede,Blowfish        //keybyte 为加密密钥，长度为24字节    //src为被加密的数据缓冲区（源）   
	 private static final String DES_EDE_CBC = "DESede/CBC/NoPadding";               // 定义 加密算法,可用 DES,DESede,Blowfish        //keybyte 为加密密钥，长度为24字节    //src为被加密的数据缓冲区（源）  
	 private static final String DES_CBC = "DES/CBC/NoPadding";  
	 private static final String DES_ECB = "DES/ECB/PKCS5Padding";  
	 
	 private static final String AES_CBC = "AES/CBC/PKCS5Padding";
	 private static final String AES_ECB = "AES/ECB/NoPadding";
	
	/**
	 * mode
	 * padding
	 * 
	 */
	
	public static byte[] encryptBy3descbc(byte[] keyBytes, byte[] src, byte[] icv) throws CryptoException {
		if (keyBytes != null && keyBytes.length == 16) {
			byte[] temKeyBytes = new byte[24];
			System.arraycopy(keyBytes, 0, temKeyBytes, 0, 16);
			System.arraycopy(keyBytes, 0, temKeyBytes, 16, 8);
			
			keyBytes = temKeyBytes;
		}
		
		try {
			SecureRandom sr = new SecureRandom();
			IvParameterSpec iv = new IvParameterSpec(icv);

			Cipher cp = Cipher.getInstance(DES_EDE_CBC);
			SecretKey deskey = new SecretKeySpec(keyBytes, "DESede");

			cp.init(Cipher.ENCRYPT_MODE, deskey, iv, sr);
			return cp.doFinal(src);
		} catch (NoSuchAlgorithmException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage());
		}
	}
	
	public static byte[] encryptBy3desecb(byte[] keyBytes, byte[] src) throws CryptoException {
		if (keyBytes != null && keyBytes.length == 16) {
			byte[] temKeyBytes = new byte[24];
			System.arraycopy(keyBytes, 0, temKeyBytes, 0, 16);
			System.arraycopy(keyBytes, 0, temKeyBytes, 16, 8);
			
			keyBytes = temKeyBytes;
		}
		
		try {
			Cipher cp = Cipher.getInstance(DES_EDE_ECB);
			SecretKey deskey = new SecretKeySpec(keyBytes, "DESede");

			cp.init(Cipher.ENCRYPT_MODE, deskey);
			return cp.doFinal(src);
		} catch (NoSuchAlgorithmException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage());
		}
	}
	
	public static byte[] encryptBydescbc(byte[] keyBytes, byte[] src, byte[] icv) throws CryptoException {
		try {
			SecureRandom sr = new SecureRandom();
			IvParameterSpec iv = new IvParameterSpec(icv);

			Cipher cp = Cipher.getInstance(DES_CBC);
			SecretKey deskey = new SecretKeySpec(keyBytes, "DES");

			cp.init(Cipher.ENCRYPT_MODE, deskey, iv, sr);
			return cp.doFinal(src);
		} catch (NoSuchAlgorithmException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage());
		}
	}
	
	
	public static byte[] mac(byte[] keyBytes, byte[] src, byte[] icv) throws CryptoException {
		byte[] descbc = encryptBydescbc(keyBytes, src, icv);
		byte[] mac = new byte[8];
		
		try {
			System.arraycopy(descbc, descbc.length - 8, mac, 0, 8);
		} catch (Exception e) {
			throw new CryptoException(e.getMessage());
		}
		return mac;
	}
	
	public static byte[] encryptBydesecb(byte[] keyBytes, byte[] src) throws CryptoException {
		try {
			Cipher cp = Cipher.getInstance(DES_ECB);
			SecretKey deskey = new SecretKeySpec(keyBytes, "DES");

			cp.init(Cipher.ENCRYPT_MODE, deskey);
			return cp.doFinal(src);
		} catch (NoSuchAlgorithmException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage());
		}
	}
	
	/**
	 * keyBytes 长度必须是16、24、32个字节
	 * icv 长度必须是16个字节
	 * @param keyBytes
	 * @param src
	 * @param icv
	 * @return
	 * @throws CryptoException
	 */
	public static byte[] encryptByaescbc(byte[] keyBytes, byte[] src, byte[] icv) throws CryptoException {

        try {  
            BufferedBlockCipher cipher = new BufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));  
            cipher.init(true, new ParametersWithIV(new KeyParameter(keyBytes), icv));  
            byte[] rv = new byte[cipher.getOutputSize(src.length)];  
            int oLen = cipher.processBytes(src, 0, src.length, rv, 0);  
            cipher.doFinal(rv, oLen);
            return rv;
        } catch (DataLengthException ex) {  
        	throw new CryptoException(ex.getMessage());
        } catch (IllegalStateException ex) {  
        	throw new CryptoException(ex.getMessage());
        } catch (InvalidCipherTextException ex) {  
        	throw new CryptoException(ex.getMessage());
        }  
		/*
		try {
			IvParameterSpec iv = new IvParameterSpec(icv);
	        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES"); 

	        Cipher cipher = Cipher.getInstance(AES_CBC); //"算法/模式/补码方式"  
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);  
	        return cipher.doFinal(src);
		} catch (NoSuchAlgorithmException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (java.lang.Exception ex) {
			throw new CryptoException(ex.getMessage());
		}
		*/
	}
	
	public static byte[] decryptByaescbc(byte[] keyBytes, byte[] src, byte[] icv) throws CryptoException {
        try {  
            BufferedBlockCipher cipher = new BufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));  
            cipher.init(false, new ParametersWithIV(new KeyParameter(keyBytes), icv));  
            byte[] rv = new byte[cipher.getOutputSize(src.length)];  
            int oLen = cipher.processBytes(src, 0, src.length, rv, 0);  
            cipher.doFinal(rv, oLen);
            return rv;
        } catch (DataLengthException ex) {  
        	throw new CryptoException(ex.getMessage());
        } catch (IllegalStateException ex) {  
        	throw new CryptoException(ex.getMessage());
        } catch (InvalidCipherTextException ex) {  
        	throw new CryptoException(ex.getMessage());
        }  
		/*
		try {
			IvParameterSpec iv = new IvParameterSpec(icv);
	        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES"); 

	        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding"); //"算法/模式/补码方式"  
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);  
	        return cipher.doFinal(src);
		} catch (NoSuchAlgorithmException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new CryptoException(ex.getMessage());
		}
		*/
	}
	
	/**
	 * keyBytes 长度必须是16、24、32个字节
	 * src 长度必须是16的倍数
	 * @param keyBytes
	 * @param src
	 * @return
	 * @throws CryptoException
	 */
	public static byte[] encryptByaesecb(byte[] keyBytes, byte[] src) throws CryptoException {
        try { 
        	BufferedBlockCipher cipher = new BufferedBlockCipher(new AESEngine());  
            cipher.init(true, new KeyParameter(keyBytes));  
            byte[] rv = new byte[cipher.getOutputSize(src.length)];  
            int oLen = cipher.processBytes(src, 0, src.length, rv, 0);  
            cipher.doFinal(rv, oLen);  
            return rv; 
        } catch (DataLengthException ex) {  
        	throw new CryptoException(ex.getMessage());
        } catch (IllegalStateException ex) {  
        	throw new CryptoException(ex.getMessage());
        } catch (InvalidCipherTextException ex) {  
        	throw new CryptoException(ex.getMessage());
        }  
          
        
		/*
		try {	
	        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");  
	        Cipher cipher = Cipher.getInstance(AES_ECB); //"算法/模式/补码方式"  
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);  
	        return cipher.doFinal(src);
		} catch (NoSuchAlgorithmException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			throw new CryptoException(ex.getMessage());
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new CryptoException(ex.getMessage());
		}
		*/
	}
	
	public static byte[] padding(byte[] src) {
		if (src != null) {
			int mode = src.length % 8;
			if (mode > 0) {
				byte[] rs = new byte[src.length + 8 - mode];
				System.arraycopy(src, 0, rs, 0, src.length);
				rs[src.length] = (byte)0x80;
				return rs;
			} else {
				byte[] rs = new byte[src.length + 8];
				System.arraycopy(src, 0, rs, 0, src.length);
				rs[src.length] = (byte)0x80;
				return rs;
			}
		}
		return src;
	}
	
	public static byte[] aesPadding(byte[] src) {
		if (src != null) {
			int mode = src.length % 16;
			if (mode > 0) {
				byte[] rs = new byte[src.length + 16 - mode];
				System.arraycopy(src, 0, rs, 0, src.length);
				rs[src.length] = (byte)0x80;
				return rs;
			} else {
				byte[] rs = new byte[src.length + 16];
				System.arraycopy(src, 0, rs, 0, src.length);
				rs[src.length] = (byte)0x80;
				return rs;
			}
		}
		return src;
	}
	
	public static byte[] encrypt(String content, String password) {  
        try {      
        
        	KeyGenerator kgen = KeyGenerator.getInstance("AES");  
            kgen.init(128, new SecureRandom(password.getBytes()));  
            SecretKey secretKey = kgen.generateKey();  
            byte[] enCodeFormat = secretKey.getEncoded();  
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
            byte[] byteContent = content.getBytes("utf-8");  
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化   
            byte[] result = cipher.doFinal(byteContent);  
            return result; // 加密   

        } catch (NoSuchAlgorithmException e) {  
                e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
                e.printStackTrace();  
        } catch (InvalidKeyException e) {  
                e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
                e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
                e.printStackTrace();  
        } catch (BadPaddingException e) {  
                e.printStackTrace();  
        }  
        return null;  
}  

	
	public static void testAES() {
		try {
		//byte[] src = srcContent.getBytes();
		//byte[] key = keyContent.getBytes();
		byte[] src = HexUtil.hexStr2ByteArr("52195077968b3e9add21348e6d9cdd82594412c63620ff97fd33225f839369a56405cc2bbf65179c54599ac3a571f19e01e57cc8562e5d329b7ba9437cb0480a");
		//byte[] key = HexUtil.hexStr2ByteArr("2b7e151628aed2a6abf7158809cf4f3c");
		byte[] key = HexUtil.hexStr2ByteArr("b0fa40a48b1e1afb1a13f76bd424302299bc563571bba171966d8225f700de20");
		System.out.println("src: " + HexUtil.byteArr2HexStr(src));
		System.out.println("key: " + HexUtil.byteArr2HexStr(key));
		
		
			byte[] enc = encryptByaesecb(key, src);
			//byte[] enc = encryptByaescbc(key, src, new byte[16]);
			
			System.out.println(HexUtil.byteArr2HexStr(enc));
		} catch (CryptoException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		testAES();

		/*
		try {
			String content = "1234567812345678";  
			String password = "123456781234567812345678";  
			//加密   
			System.out.println("加密前：" + content);  
			byte[] encryptResult = encrypt(content, password);
			
			System.out.println(HexUtil.byteArr2HexStr(encryptResult));
			
  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
	}
}
