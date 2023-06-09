package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.core.IKey;
import com.ecp.jces.jctool.util.CipherUtil;
import com.ecp.jces.jctool.util.CryptoException;

public abstract class SCP implements ISCP {

	protected IKey sEncKey;
	protected IKey sMacKey;
	protected IKey sDekKey;
	
	protected byte[] encKey;
	protected byte[] macKey;
	protected byte[] cmacKey;
	protected byte[] rmacKey;
	protected byte[] dekKey;
	
	protected byte[] derivationData = new byte[16];
	
	protected byte[] hostCrypto = new byte[8];
	protected byte[] hostChallenge = new byte[8];
	protected byte[] cardCrypto = new byte[8];
	protected byte[] cardChallenge = new byte[8];
	
	protected byte[] apdu;
	protected int seqCounter;
	protected byte keyVersionNo;
	
	protected int statu;
	protected int securityLevel;
	
	/*
	public void genSessionKey() {
		
	}
	*/
	
	public boolean veriCardCrypto() throws CryptoException {
		byte[] src = new byte[24];
		
		System.arraycopy(this.hostChallenge, 0, src, 0, 8);
		System.arraycopy(this.cardChallenge, 0, src, 8, 8);
		
		src[16] = (byte)0x80;
		
		byte[] encrypt =  CipherUtil.encryptBy3descbc(encKey, src, new byte[8]);
		byte[] tmpCardCrypto = new byte[8];
		System.arraycopy(encrypt, encrypt.length - 8, tmpCardCrypto, 0, 8); 
				
		return compare(this.cardCrypto, tmpCardCrypto);
	}
	
	public byte[] genHostCrypto() throws CryptoException {
		byte[] src = new byte[24];
		
		System.arraycopy(this.cardChallenge, 0, src, 0, 8);
		System.arraycopy(this.hostChallenge, 0, src, 8, 8);
		src[16] = (byte)0x80;
		
		byte[] encrypt = CipherUtil.encryptBy3descbc(encKey, src, new byte[8]);
		System.arraycopy(encrypt, encrypt.length - 8, this.hostCrypto, 0, 8); 
		return this.hostCrypto;
	}
	
	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}

	public static byte[] genHostChallenge() {
		byte[] hostChallenge = new byte[8];
		for (int i = 0; i < hostChallenge.length; i++) {
			hostChallenge[i] = (byte)(Math.random() * 1000);
		}
		return hostChallenge;
	}
	
	/*
	public SCP(IKey sEncKey, IKey sMacKey, IKey sDekKey) {
		setStaticKey(sEncKey, sMacKey, sDekKey);
		
		//this.derivationData = new byte[16];
		//this.icv = new byte[8];
	}
	*/
	
	public void init(IKey sEncKey, IKey sMacKey, IKey sDekKey, byte[] apdu, byte[] hostChallenge) {
		setStaticKey(sEncKey, sMacKey, sDekKey);
		this.hostChallenge = hostChallenge;
		this.apdu = apdu;
		
		//分析apdu数据
		seqCounter = ((apdu[ISCP.SCP_INIT_SEQ_COUNTER_OFFSET] & 0xFF) << 8) | (apdu[ISCP.SCP_INIT_SEQ_COUNTER_OFFSET + 1] & 0xFF);
		
		keyVersionNo = apdu[ISCP.SCP_INIT_KEY_VERSION_OFFSET];
		
		System.arraycopy(apdu, ISCP.SCP_INIT_CARD_CHALLENGE_OFFSET, cardChallenge, 0, 8);
		System.arraycopy(apdu, ISCP.SCP_INIT_CARD_CRYPTO_OFFSET, cardCrypto, 0, 8);
	}
	
	protected void setStaticKey(IKey sEncKey, IKey sMacKey, IKey sDekKey) {
		this.sEncKey = sEncKey;
		this.sMacKey = sMacKey;
		this.sDekKey = sDekKey;
	}
	
	protected boolean compare(byte[] b1, byte[] b2) {
		if (b1 == null || b2 == null || b1.length != b2.length)
			return false;
		
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}
	
	
	public byte[] getCMACKey() {
		return this.cmacKey;
	}

	public byte[] getENCKey() {
		return this.encKey;
	}

	public byte[] getRMACKey() {
		return this.rmacKey;
	}
	
	public byte[] getDEKKey() {
		return this.dekKey;
	}
	
	public byte[] resDecryption(byte[] apduData) throws CryptoException {
		return apduData;
	}
	
	public byte[] keyEncryption(byte[] keyData) throws CryptoException {
		
		byte[] rsAllData = CipherUtil.encryptBy3desecb(this.dekKey, keyData);
		byte[] keyEncData = new byte[rsAllData.length - 8];
		System.arraycopy(rsAllData, 0, keyEncData, 0, keyEncData.length);
		return rsAllData;
	}
	
	public byte[] keyCheckValue(byte[] key) throws CryptoException {
		byte[] checkValue = new byte[3];
		if (key.length != 16)
			throw new CryptoException("128 bit key required.");
		
		try {
			//byte[] encData = Cryto.ebc3DesEncrypt(Const.GP_KEY_VALUE_DATA, key);
			byte[] encData = CipherUtil.encryptBy3desecb(key, new byte[8]);
			
			if (encData != null && encData.length > 3) {
				System.arraycopy(encData, 0, checkValue, 0, 3);
			}
		} catch (CryptoException e) {
			//e.printStackTrace();
			throw e;
		}
		
		return checkValue;
	}
	
	public byte[] padKey(byte[] keyBytes) throws CryptoException {
		if (keyBytes != null) {
			int mode = keyBytes.length % 8;
			if (mode > 0) {
				byte[] rs = new byte[keyBytes.length + 8 - mode];
				System.arraycopy(keyBytes, 0, rs, 0, keyBytes.length);
				rs[keyBytes.length] = (byte)0x80;
				return rs;
			}
		}
		return keyBytes;

	}
	
	public byte[] deriveKey(byte[] keyBytes, byte[] deriveData) throws CryptoException {
		return CipherUtil.encryptBy3desecb(keyBytes, deriveData);
	}
}
