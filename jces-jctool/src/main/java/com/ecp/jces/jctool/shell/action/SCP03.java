package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.core.IKey;
import com.ecp.jces.jctool.util.CipherUtil;
import com.ecp.jces.jctool.util.CryptoException;
import com.ecp.jces.jctool.util.HexUtil;

public class SCP03 extends SCP{

	private byte[] macCV;
	private byte[] icvCounter = new byte[16];
	
	public void init(IKey sEncKey, IKey sMacKey, IKey sDekKey, byte[] apdu, byte[] hostChallenge) {
		setStaticKey(sEncKey, sMacKey, sDekKey);
		this.hostChallenge = hostChallenge;
		
		/*
		//test code
		try {
			this.hostChallenge = HexUtil.hexStr2ByteArr("BBBBBBBBBBBBBBBB");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//test code end
		*/
		
		this.apdu = apdu;
		this.macCV = new byte[16];
		
		//分析apdu数据		
		keyVersionNo = apdu[ISCP.SCP_INIT_KEY_VERSION_OFFSET];
		
		
		System.arraycopy(apdu, ISCP.SCP03_INIT_CARD_CHALLENGE_OFFSET, cardChallenge, 0, 8);
		
		/*
		//test code
		try {
			System.arraycopy(HexUtil.hexStr2ByteArr("CCCCCCCCCCCCCCCC"), 0, cardChallenge, 0, 8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//test code end
		*/
		
		System.arraycopy(apdu, ISCP.SCP03_INIT_CARD_CRYPTO_OFFSET, cardCrypto, 0, 8);
	}
	
	public void incIcvCounter() {		
		int i = 15;
		icvCounter[i]++;
		while (icvCounter[i] == 0) {
			i--;
			if (i == 0) {
				resetIcvCounter();
			}
			icvCounter[i]++;
		}
	}
	
	public void resetIcvCounter() {
		for (int i = 0; i < icvCounter.length; i++) {
			icvCounter[i] = 0;
		}
		icvCounter[15] = 0x0;
	}
	
	public byte[] genICV(boolean isRenc) throws CryptoException {
		byte[] counter = new byte[16];
		
		if (!isRenc) {
			incIcvCounter();
		}
		
		System.arraycopy(this.icvCounter, 0, counter, 0, 16);
		if (isRenc) {
			counter[0] = (byte)0x80;
		}
		return CipherUtil.encryptByaesecb(this.encKey, counter);
	}
	
	public byte[] genHostCrypto() throws CryptoException {
		byte[] derivationData = new byte[32]; //[i]2 || Label || 0x00 || Context || [L]2
		
		derivationData[15] = (byte)0x01;
		derivationData[11] = SCP03_CONSTANT_HOST_CRYPT;
		System.arraycopy(this.hostChallenge, 0, derivationData, 16, 8);
		System.arraycopy(this.cardChallenge, 0, derivationData, 24, 8);
		derivationData[14] = (byte)0x40;
		
		System.out.println("host crypto:");
		byte[] tempHostCrypto = keyDerivation(this.cmacKey, derivationData);
		System.arraycopy(tempHostCrypto, 0, this.hostCrypto, 0, 8);
		System.out.println("hostCrypto: " + HexUtil.byteArr2HexStr(this.hostCrypto));
		return this.hostCrypto;
	}
	
	public byte[] genCMAC(byte[] srcData) throws CryptoException {
		srcData[4] = (byte)((srcData[4] & 0xFF) + 8);
		
		byte bit = (byte)0x4;
		srcData[0] = (byte)(srcData[0] | bit);
		srcData[0] &= 0xFC;
		
		System.out.println("gen mac data:" + HexUtil.byteArr2HexStr(srcData));
		System.out.println("mac CV: " + HexUtil.byteArr2HexStr(this.macCV));
		byte[] messages = new byte[srcData.length + this.macCV.length];
		System.arraycopy(this.macCV, 0, messages, 0, this.macCV.length);
		System.arraycopy(srcData, 0, messages, this.macCV.length, srcData.length);
		
		System.out.println("Cmac data:" + HexUtil.byteArr2HexStr(messages));
		System.out.println("cmacKey:" + HexUtil.byteArr2HexStr(this.cmacKey));
		byte[] cmac = mac(this.cmacKey, messages);
		System.arraycopy(cmac, 0, this.macCV, 0, this.macCV.length);
		
		System.out.println("cmac:" + HexUtil.byteArr2HexStr(cmac));
		byte[] rs = new byte[srcData.length + 8];
		System.arraycopy(srcData, 0, rs, 0, srcData.length);
		System.arraycopy(cmac, 0, rs, srcData.length, 8);

		return rs;
	}

	/*
	private void setL(byte[] keyBytes, byte[] derivationData) throws CryptoException {
		switch (keyBytes.length) {
		case 16:
			derivationData[30] = (byte)0x00;
			derivationData[31] = (byte)0x80;
			break;
		case 24:
			derivationData[30] = (byte)0x00;
			derivationData[31] = (byte)0xC0;
			break;
		case 32:
			derivationData[30] = (byte)0x01;
			derivationData[31] = (byte)0x00;
			break;

		default:
			throw new CryptoException("Wrong key length: must be equal to 128, 192 or 256");
		}
	}
	*/
	private byte[] genDerivation(byte[] src, byte counter, byte constant, int length) throws CryptoException {
		byte[] deriData = new byte[src.length];
		System.arraycopy(src, 0, deriData, 0, src.length);
		
		deriData[15] = counter;
		deriData[11] = constant;
		
		switch (length) {
		case 16:
			deriData[13] = (byte)0x00;
			deriData[14] = (byte)0x80;
			break;
		case 24:
			deriData[13] = (byte)0x00;
			deriData[14] = (byte)0xC0;
			break;
		case 32:
			deriData[13] = (byte)0x01;
			deriData[14] = (byte)0x00;
			break;

		default:
			throw new CryptoException("Wrong key length: must be equal to 128, 192 or 256");
		}
		
		return deriData;
	}
	/**
	 * 
	 */
	public void genSessionKey() throws CryptoException {
		byte[] srcDerivationData = new byte[32];
		
		
		//[i]2 || Label || 0x00 || Context || [L]2
		srcDerivationData[15] = (byte)0x01; //counter i
		//label: A 12 byte “label” consisting of 11 bytes with value ‘00’ followed by a one byte derivation constant
		//derivationData[0] = SCP03_CONSTANT_ENC;
		//A one byte “separation indicator” with value ‘00’
		
		//Context
		System.arraycopy(this.hostChallenge, 0, srcDerivationData, 16, 8);
		System.arraycopy(this.cardChallenge, 0, srcDerivationData, 24, 8);		
		
		//A 2 byte integer “L” specifying the length in bits of the derived data (value ‘0040’, ‘0080’, ‘00C0’ or ‘0100’)
		//derivationData[31] = (byte)0x80;
		
		//enc key
		//derivationData[12] = SCP03_CONSTANT_ENC;
		//setL(this.sEncKey.getKeymat(), derivationData);
		System.out.println("enc session key:");
		this.encKey = keyDerivation(this.sEncKey.getKeymat(), 
				genDerivation(srcDerivationData, (byte)1, SCP03_CONSTANT_ENC, this.sEncKey.getKeymat().length));
		
		//cmac key
		//derivationData[12] = SCP03_CONSTANT_MAC;
		//setL(this.sMacKey.getKeymat(), derivationData);
		System.out.println("cmac session key:");
		this.cmacKey = keyDerivation(this.sMacKey.getKeymat(), 
				genDerivation(srcDerivationData, (byte)1, SCP03_CONSTANT_MAC, this.sMacKey.getKeymat().length));
		
		//rmac key
		//derivationData[12] = SCP03_CONSTANT_RMAC;
		//setL(this.sMacKey.getKeymat(), derivationData);
		System.out.println("rmac session key:");
		this.rmacKey = keyDerivation(this.sMacKey.getKeymat(), 
				genDerivation(srcDerivationData, (byte)1, SCP03_CONSTANT_RMAC, this.sMacKey.getKeymat().length));
	}

	public boolean veriCardCrypto() throws CryptoException {
		byte[] derivationData = new byte[32]; //[i]2 || Label || 0x00 || Context || [L]2
		
		derivationData[15] = (byte)0x01;
		derivationData[11] = SCP03_CONSTANT_CARD_CRYPT;
		System.arraycopy(this.hostChallenge, 0, derivationData, 16, 8);
		System.arraycopy(this.cardChallenge, 0, derivationData, 24, 8);
		derivationData[14] = (byte)0x40;
		byte[] veriCardCrypto = keyDerivation(this.cmacKey, derivationData);
		return compare(this.cardCrypto, veriCardCrypto);
	}

	/**
	 * res = response data field + status words
	 */
	public boolean veriRMAC(byte[] res, byte[] mac) throws CryptoException {
		
		if (res.length <= 2) {
			return true;
		}
		
		if (res.length <= 10 && res.length > 2) {
			return false;
		}
		
		byte[] srcData = new byte[res.length - 8 + this.macCV.length];
		System.arraycopy(this.macCV, 0, srcData, 0, this.macCV.length);
		System.arraycopy(res, 0, srcData, this.macCV.length, res.length - 10);
		System.arraycopy(res, res.length - 2, srcData, srcData.length - 2, 2);
		
		byte[] rmac = new byte[8];
		System.arraycopy(res, res.length -10, rmac, 0, 8);
		
		byte[] newRmac = new byte[8];
		System.arraycopy(mac(this.rmacKey, srcData), 0, newRmac, 0, 8);

		return compare(rmac, newRmac);
	}

	public byte[] encryptDataField(byte[] srcData) throws CryptoException {
		return null;
	}
	
	private byte[] subkey(byte[] keyBytes) throws CryptoException {
		byte[] srcData = new byte[16];
		byte[] subKey = new byte[32];
		byte[] r128 = new byte[16];
		r128[15] = (byte)0x87;
		boolean isXor;
		
		try {
			byte[] encData = CipherUtil.encryptByaesecb(keyBytes, srcData);
			System.out.println("subKey CIPHK: " + HexUtil.byteArr2HexStr(encData));
			
			//计算key1
			System.arraycopy(encData, 0, subKey, 0, 16);
			if ((subKey[0] & (byte)0x80) == (byte)0x80) {
				isXor = true;
			} else {
				isXor = false;
			}
			
			for (int i = 0; i < 16; i++) {
				subKey[i] <<= 1;
				if (i != 15) {
					subKey[i] |= ((subKey[i + 1] & 0xFF) >> 7);
				}
				
				if (isXor) {
					subKey[i] ^= (r128[i] & 0xFF);
				}
			}
			
			
			//计算key2
			System.arraycopy(subKey, 0, subKey, 16, 16);
			if ((subKey[16] & (byte)0x80) == (byte)0x80) {
				isXor = true;
			} else {
				isXor = false;
			}
			
			for (int i = 0; i < 16; i++) {
				subKey[16 + i] <<= 1;
				if (i != 15) {
					subKey[16 + i] |= ((subKey[ 16 + i + 1] & 0xFF) >> 7);
				}
				
				if (isXor) {
					subKey[16 + i] ^= (r128[i] & 0xFF);
				}
			}
			
			System.out.println("subkey:" + HexUtil.byteArr2HexStr(subKey));
			return subKey;
		} catch (CryptoException ex) {
			throw ex;
		}
	}
	
	private byte[] cmac(byte[] keyBytes, byte[] subKey, byte[] messages) throws CryptoException {

		try {			
			int msgLen = messages.length;
			int blocks = msgLen / 16;
			int padLen = msgLen % 16;
			byte[] srcData = null;
			if (padLen != 0) {
				srcData = new byte[blocks * 16 + 16];
				srcData[msgLen] = (byte)0x80;
				System.arraycopy(messages, 0, srcData, 0, msgLen);

				for (int i = 0; i < 16; i++) {
					srcData[blocks * 16 + i] ^= (subKey[16 + i] & 0xFF);
				}
				blocks++;
			} else {
				srcData = messages;
				
				for (int i = 0; i < 16; i++) {
					srcData[msgLen - 16 + i] ^= (subKey[i] & 0xFF);
				}
			}

	
			byte[] cmac = null;
			byte[] dataIn = new byte[16];
			byte[] icv = new byte[16];
			
			for (int i = 0; i < blocks; i++) {
				System.arraycopy(srcData, 16 * i, dataIn, 0, 16);
				cmac = CipherUtil.encryptByaescbc(keyBytes, dataIn, icv);
				System.arraycopy(cmac, 0, icv, 0, 16);
			}
			
			System.out.println("cmac: " + HexUtil.byteArr2HexStr(cmac));
			return cmac;
		} catch (CryptoException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CryptoException();
		}
	}
	
	public byte[] mac(byte[] keyBytes, byte[] messages) throws CryptoException {

		try {
			byte[] subKey = subkey(keyBytes);
			System.out.println("subKey: " + HexUtil.byteArr2HexStr(subKey));
			
			return cmac(keyBytes, subKey, messages);
		} catch (CryptoException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CryptoException();
		}
	}
	
	public byte[] keyDerivation(byte[] keyBytes, byte[] derivationData) throws CryptoException {		
		//int keyLen = keyBytes.length;
		int dataLen = (((derivationData[13] & 0xFF) << 8) | (derivationData[14] & 0xFF)) / 8;
		byte[] cmac = null;
		byte[] subKey = null;
		byte[] keyDerivation = new byte[dataLen];
		
		try {
			switch (dataLen) {
			case 8:
			case 16:
				subKey = subkey(keyBytes);
				cmac = cmac(keyBytes, subKey, derivationData);
				System.arraycopy(cmac, 0, keyDerivation, 0, keyDerivation.length);
				break;
			case 24:
			case 32:
				byte[] tmpKeyDerivation = new byte[32];
				byte[] derivationData2 = new byte[derivationData.length];
				System.arraycopy(derivationData, 0, derivationData2, 0, derivationData.length);
				
				subKey = subkey(keyBytes);
				System.out.println("derivationData: " + HexUtil.byteArr2HexStr(derivationData));
				cmac = cmac(keyBytes, subKey, derivationData);
				System.arraycopy(cmac, 0, tmpKeyDerivation, 0, 16);
				
				derivationData2[15] = (byte)0x02;
				System.out.println("derivationData: " + HexUtil.byteArr2HexStr(derivationData2));
				cmac = cmac(keyBytes, subKey, derivationData2);
				System.arraycopy(cmac, 0, tmpKeyDerivation, 16, 16);
				System.arraycopy(tmpKeyDerivation, 0, keyDerivation, 0, keyDerivation.length);
				break;
			default:
				throw new CryptoException("Wrong keysize: must be equal to 128, 192 or 256");
			}
			
			return keyDerivation;
		} catch (CryptoException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage());
		}
	}
	
	public byte[] cmdEncryption(byte[] apduData) throws CryptoException {	
		byte bit = (byte)0x4;
		apduData[APDU.CLS] = (byte)(apduData[APDU.CLS] & 0xFC | bit);
		
		int lc = apduData[APDU.LC] & 0xFF;
		
		if (lc > 0) {
			byte[] dataField = new byte[lc];
			System.arraycopy(apduData, APDU.DATA, dataField, 0, lc);
			dataField = CipherUtil.aesPadding(dataField);
			dataField = CipherUtil.encryptByaescbc(this.encKey, dataField, genICV(false));
			
			//int dataFieldLen = dataField.length - 16; 
			int dataFieldLen = dataField.length; 
			byte[] cmacData = new byte[APDU.HEADER_LENGTH + dataFieldLen];
			System.arraycopy(apduData, 0, cmacData, 0, APDU.HEADER_LENGTH);
			cmacData[APDU.LC] =  (byte)dataFieldLen;
			System.arraycopy(dataField, 0, cmacData, APDU.DATA, dataFieldLen);
	
			return genCMAC(cmacData);
		} else {
			incIcvCounter();
			return genCMAC(apduData);
		}
	}

	public byte[] resDecryption(byte[] res) throws CryptoException {
		if (res.length <= 2) {
			return res;
		}
		
		if (res.length <= 10 && res.length > 2) {
			return res;
		}
		
		byte[] dataField = new byte[res.length - 10];
		System.arraycopy(res, 0, dataField, 0, dataField.length);
		/*
		System.out.println("res dec: " + HexUtil.byteArr2HexStr(dataField) + 
				"    key: " + HexUtil.byteArr2HexStr(this.encKey) +
				"    ICV: " + HexUtil.byteArr2HexStr(genICV(true)));
				*/
		dataField = CipherUtil.decryptByaescbc(this.encKey, dataField, genICV(true));
		//System.out.println("text: " + HexUtil.byteArr2HexStr(dataField));
		
		int dataLen = dataField.length;
		byte curData;
		for (int i = dataField.length - 1; i >= 0; i--) {
			curData = dataField[i];
			if ((curData & 0xFF) == 0x80 || curData == 0) {
				if ((curData & 0xFF) == 0x80) {
					dataLen = i;
					break;
				}
			} else {
				break;
			}
		}
		byte[] resText = new byte[dataLen];
		System.arraycopy(dataField, 0, resText, 0, dataLen);
		//System.arraycopy(res, res.length - 2, resText, dataLen, 2);
		
		return resText;
	}
	
	/*
	public byte[] decrytResDataField(byte[] res) throws CryptoException {
		if (res.length < 18) {
			return res;
		}
		
		byte[] dataField = new byte[res.length - 10];
		System.arraycopy(res, 0, dataField, 0, dataField.length);
		
		byte[] srcData = CipherUtil.decryptByaescbc(this.rmacKey, dataField, genICV(true));
		
		//去填充
		int padLen = srcData.length;
		for (int i = (srcData.length - 1); i >= 0; i--) {
			if ((srcData[i] & 0xFF) == 0x80) {
				padLen = i + 1;
				break;
			}
		}
		
		byte[] textData = new byte[padLen + 2];
		System.arraycopy(srcData, 0, textData, 0, padLen);
		System.arraycopy(res, res.length - 2, textData, padLen, 2);
		
		return textData;
	}
	*/
	public static void main(String[] args) {
		SCP03 scp = new SCP03();
		try {
			byte[] icvCounter = new byte[16];
			icvCounter[15] = (byte)0x01;
					
			byte[] icv = CipherUtil.encryptByaesecb(HexUtil.hexStr2ByteArr("3CF125A11B478A5191B9C2F49265295603FBF3657FC35B749C13D506BFBFB88E"), icvCounter);
			System.out.println("icv: " + HexUtil.byteArr2HexStr(icv));
		} catch (CryptoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] keyEncryption(byte[] keyBytes) throws CryptoException {
		byte[] srcData = null;
		
		switch (keyBytes.length) {
		case 16:
		case 32:
			//srcData = new byte[keyBytes.length];
			srcData = keyBytes;
			break;
		case 24:
			srcData = new byte[keyBytes.length + 8];
			System.arraycopy(keyBytes, 0, srcData, 0, keyBytes.length);
			srcData[keyBytes.length] = (byte)0x80;
			break;
		default:
			throw new CryptoException("Wrong keysize: must be equal to 128, 192 or 256");
		}
		
		byte[] encData = CipherUtil.encryptByaescbc(this.sDekKey.getKeymat(), srcData, new byte[16]);
		/*
		byte[] rsData = new byte[encData.length -16];
		System.arraycopy(encData, 0, rsData, 0, rsData.length);
		return rsData;
		*/
		return encData;
	}
	
	public byte[] keyCheckValue(byte[] key) throws CryptoException {
		byte[] checkValue = new byte[3];
		
		byte[] srcData = new byte[16];
		srcData[15] = 0x01;
		byte[] encData = CipherUtil.encryptByaesecb(key, srcData);
		System.arraycopy(encData, 0, checkValue, 0, 3);
		
		return checkValue;
	}
	
	public byte[] padKey(byte[] keyBytes) throws CryptoException {
		if (keyBytes != null) {
			int mode = keyBytes.length % 16;
			if (mode > 0) {
				byte[] rs = new byte[keyBytes.length + 16 - mode];
				System.arraycopy(keyBytes, 0, rs, 0, keyBytes.length);
				rs[keyBytes.length] = (byte)0x80;
				return rs;
			}
		}
		return keyBytes;

	}

	public String getKeyType() {
		return "AES";
	}
	
	public byte[] deriveKey(byte[] keyBytes, byte[] deriveData) throws CryptoException {
		return CipherUtil.encryptByaesecb(keyBytes, deriveData);
	}
}
