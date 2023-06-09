package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.core.IKey;
import com.ecp.jces.jctool.util.CryptoException;

public interface ISCP {

	public static final String KEY_TYPE_AES = "AES";
	public static final String KEY_TYPE_DES_ECB = "DES-ECB";
	
	public static final int SCP_ID_01 = 01;
	public static final int SCP_ID_02 = 02;
	public static final int SCP_ID_03 = 03;
	
	public static final int SCP_INIT_KEY_VERSION_OFFSET = 10;
	public static final int SCP_INIT_SCPID_OFFSET = 11;
	public static final int SCP_INIT_SEQ_COUNTER_OFFSET = 12;
	public static final int SCP_INIT_CARD_CHALLENGE_OFFSET = 12;
	public static final int SCP_INIT_CARD_CRYPTO_OFFSET = 20;
	
	//scp03
	public static final int SCP03_INIT_CARD_CHALLENGE_OFFSET = 13;
	public static final int SCP03_INIT_CARD_CRYPTO_OFFSET = 21;
	
	public static final byte[] SCP02_CONSTANT_Constant_CMAC = {(byte)0x01, (byte)0x01};
	public static final byte[] SCP02_CONSTANT_Constant_RMAC = {(byte)0x01, (byte)0x02};
	public static final byte[] SCP02_CONSTANT_Constant_ENC = {(byte)0x01, (byte)0x82};
	public static final byte[] SCP02_CONSTANT_Constant_DEK = {(byte)0x01, (byte)0x81};
	
	public static final byte SCP03_CONSTANT_ENC = (byte)0x04;
	public static final byte SCP03_CONSTANT_MAC = (byte)0x06;
	public static final byte SCP03_CONSTANT_RMAC = (byte)0x07;
	public static final byte SCP03_CONSTANT_CARD_CRYPT = (byte)0x00;
	public static final byte SCP03_CONSTANT_HOST_CRYPT = (byte)0x01;
	
	public static final byte SCP_SL_CMAC = (byte)0x1;
	public static final byte SCP_SL_ENC = (byte)0x3;
	public static final byte SCP_SL_CRMAC = (byte)0x11;
	public static final byte SCP_SL_CRMAC_ENC = (byte)0x13;
	public static final byte SCP_SL_CRMAC_ENC_DEC = (byte)0x33;
	public static final byte SCP_SL_RMAC = 0x10;
	
	
	
	
	
	//public void setStaticKey(IKey sEncKey, IKey sMacKey, IKey sDekKey);
	
	public void init(IKey sEncKey, IKey sMacKey, IKey sDekKey, byte[] apdu, byte[] hostCrypto);
	
	public void genSessionKey() throws CryptoException;
	
	public byte[] genCMAC(byte[] srcData) throws CryptoException;
	
	public boolean veriRMAC(byte[] apdu, byte[] mac) throws CryptoException;
	/*
	public byte[] decryptionDataField();
	*/
	
	public byte[] keyEncryption(byte[] keyBytes) throws CryptoException;
	public byte[] genHostCrypto() throws CryptoException;	
	public boolean veriCardCrypto() throws CryptoException;
	
	public byte[] cmdEncryption(byte[] apduData) throws CryptoException;
	public byte[] resDecryption(byte[] apduData) throws CryptoException;
	
	public byte[] keyCheckValue(byte[] key) throws CryptoException;
	
	
	public int getSecurityLevel();

	public void setSecurityLevel(int securityLevel);
	
	//public byte[] getICV();
	//public void setICV(byte[] icv);
	
	public byte[] getCMACKey();
	
	public byte[] getRMACKey();
	
	public byte[] getENCKey();
	
	public byte[] getDEKKey();
	
	public byte[] padKey(byte[] keyBytes) throws CryptoException;
	
	public String getKeyType();
	
	public byte[] deriveKey(byte[] keyBytes, byte[] deriveData) throws CryptoException ;
}
