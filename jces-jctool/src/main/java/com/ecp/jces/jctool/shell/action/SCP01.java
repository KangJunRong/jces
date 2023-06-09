package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.util.CipherUtil;
import com.ecp.jces.jctool.util.CryptoException;

public class SCP01 extends SCP {

	private byte[] lastCmac = new byte[8];
	
	public void genSessionKey() throws CryptoException {
		byte[] derivationData = new byte[16];
		System.arraycopy(this.cardChallenge, 0, derivationData, 8, 4);
		System.arraycopy(this.cardChallenge, 4, derivationData, 0, 4);
		
		System.arraycopy(this.hostChallenge, 0, derivationData, 4, 4);
		System.arraycopy(this.hostChallenge, 4, derivationData, 12, 4);
		
		
		this.encKey = CipherUtil.encryptBy3desecb(this.sEncKey.getKeymat(), derivationData);
		this.cmacKey = CipherUtil.encryptBy3desecb(this.sMacKey.getKeymat(), derivationData);
	}

	public byte[] genCMAC(byte[] srcData) throws CryptoException  {
		//修改apdu 头信息
		srcData[APDU.LC] = (byte)((srcData[APDU.LC] & 0xFF) + 8);
		
		byte bit = (byte)0x4;
		srcData[APDU.CLS] = (byte)((srcData[APDU.CLS] & 0xFC) | bit);
		
		
		//填充数据
		byte[] cmacData = CipherUtil.padding(srcData);
		
		int blockNo = cmacData.length / 8;
		byte[] cmac = this.lastCmac;
		byte[] textData = new byte[8];
		for (int i = 0; i < blockNo; i++) {
			System.arraycopy(cmacData, i * 8, textData, 0, 8);
			cmac = CipherUtil.encryptBy3descbc(cmacKey, textData, cmac);
		}
		
		byte[] apdu = new byte[srcData.length + cmac.length];
		System.arraycopy(srcData, 0, apdu, 0, srcData.length);
		System.arraycopy(cmac, 0, apdu, srcData.length, cmac.length);

		System.arraycopy(cmac, 0, this.lastCmac, 0, 8);
		
		return apdu;
	}

	public boolean veriRMAC(byte[] apdu, byte[] mac) throws CryptoException {
		return true;
	}
	
	public byte[] cmdEncryption(byte[] srcData) throws CryptoException {
		if (srcData == null || srcData.length < APDU.HEADER_LENGTH) {
			return srcData;
		}
		
		int lc = srcData[APDU.LC] & 0xFF;
		byte[] dataField = new byte[lc + 1];
		
		//算mac
		byte[] macData  = genCMAC(srcData);
		//byte[] mac = new byte[8];
		//System.arraycopy(macData, macData.length - 8, mac, 0, 8);
		
		//加密命令数据
		dataField[0] = (byte)lc;
		System.arraycopy(srcData, APDU.DATA, dataField, 1, lc);
		dataField = CipherUtil.padding(dataField);
		dataField = CipherUtil.encryptBy3descbc(this.encKey, dataField, new byte[8]);
		//int padLen = dataField.length - lc;
		
		//组装新的命令
		byte[] newData = new byte[APDU.HEADER_LENGTH + dataField.length + 8];
		System.arraycopy(srcData, 0, newData, 0, APDU.HEADER_LENGTH);
		newData[APDU.LC] = (byte)(dataField.length + 8);
		System.arraycopy(dataField, 0, newData, APDU.DATA, dataField.length);
		System.arraycopy(macData, macData.length - 8, newData, APDU.DATA + dataField.length, 8);
		
		return newData;
	}

	public String getKeyType() {
		return "DES-ECB";
	}
}
