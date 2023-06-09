package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.util.CipherUtil;
import com.ecp.jces.jctool.util.CryptoException;

public class SCP02 extends SCP {
	
	private byte[] lastCmac = new byte[8];
	private byte[] lastRmac = null;
	
	public void genSessionKey() throws CryptoException {
		derivationData[2] = (byte)(seqCounter >> 8);
		derivationData[3] = (byte)(seqCounter & 0xFF);
		
		//gen enc key
		System.arraycopy(ISCP.SCP02_CONSTANT_Constant_ENC, 0, derivationData, 0, 2);
		encKey = CipherUtil.encryptBy3descbc(sEncKey.getKeymat(), derivationData, lastCmac);
		
		//gen cmac key
		System.arraycopy(ISCP.SCP02_CONSTANT_Constant_CMAC, 0, derivationData, 0, 2);
		cmacKey = CipherUtil.encryptBy3descbc(sMacKey.getKeymat(), derivationData, lastCmac);
		
		//gen rmac key
		System.arraycopy(ISCP.SCP02_CONSTANT_Constant_RMAC, 0, derivationData, 0, 2);
		rmacKey = CipherUtil.encryptBy3descbc(sMacKey.getKeymat(), derivationData, lastCmac);
		
		//gen dek key
		System.arraycopy(ISCP.SCP02_CONSTANT_Constant_DEK, 0, derivationData, 0, 2);
		dekKey = CipherUtil.encryptBy3descbc(sDekKey.getKeymat(), derivationData, lastCmac);
		
	}

	public byte[] genCMAC(byte[] srcData) throws CryptoException {
		//修改apdu 头信息
		srcData[4] = (byte)((srcData[4] & 0xFF) + 8);
		
		byte bit = (byte)0x4;
		srcData[0] = (byte)((srcData[0] & 0xFC) | bit);
		
		
		//填充数据
		byte[] cmacData = CipherUtil.padding(srcData);
		byte[] cmac = null;
		
		if (cmacData.length > 8) {
			byte[] data1 = new byte[cmacData.length - 8];
			System.arraycopy(cmacData, 0, data1, 0, data1.length);
			
			byte[] data2 =new byte[8];
			System.arraycopy(cmacData, cmacData.length - 8, data2, 0, 8);
			
			byte[] key1 = new byte[8];
			System.arraycopy(this.cmacKey, 0, key1, 0, 8);
			
			cmac = CipherUtil.mac(key1, data1, lastCmac); //mac
			cmac = CipherUtil.encryptBy3descbc(cmacKey, data2, cmac);
		} else {
			cmac = CipherUtil.encryptBy3descbc(cmacKey, cmacData, lastCmac);
		}
		
		//组装apdu命令
		byte[] apdu = new byte[srcData.length + cmac.length];
		System.arraycopy(srcData, 0, apdu, 0, srcData.length);
		System.arraycopy(cmac, 0, apdu, srcData.length, cmac.length);
		
		
		//加密icv
		byte[] ivkey = new byte[8];
		System.arraycopy(this.cmacKey, 0, ivkey, 0, 8);
		this.lastCmac = CipherUtil.encryptBydescbc(ivkey, cmac, new byte[8]);
		
		if (lastRmac == null) {
			lastRmac = new byte[8];
			System.arraycopy(cmac, 0, lastRmac, 0, 8);
		}

		return apdu;
	}
	/*
	public byte[] genCMAC(byte[] srcData) throws CryptoException{
		srcData[4] = (byte)(srcData[4] + 8);
		
		byte[] inputData = CipherUtil.padding(srcData);
		byte[] data1 = new byte[inputData.length -8];
		System.arraycopy(inputData, 0, data1, 0, data1.length);
		
		byte[] key1 = new byte[8];
		System.arraycopy(this.cmacKey, 0, key1, 0, 8);
		
		
		byte[] data2 =new byte[8];
		System.arraycopy(inputData, inputData.length -8, data2, 0, 8);
		
		byte[] rs1 = CipherUtil.encryptBydescbc(key1, data1, icv); //单des 加密
		rs1 = CipherUtil.xor(rs1,data2); //异或		
		byte[] cmac = CipherUtil.encryptBy3descbc(cmacKey, rs1, icv);
		
		byte[] apdu = new byte[srcData.length + cmac.length];
		System.arraycopy(srcData, 0, apdu, 0, srcData.length);
		System.arraycopy(cmac, 0, apdu, srcData.length, cmac.length);
		
		this.icv = cmac;
		return apdu;
	}
*/

	public boolean veriRMAC(byte[] apdu, byte[] mac) throws CryptoException {
		return true;
		/*
		System.out.println("lastRmac: " + HexUtil.byteArr2HexStr(lastRmac));
		System.out.println("rmac data: " + HexUtil.byteArr2HexStr(apdu));
		//修改apdu 头信息
		apdu[APDU.LC] = (byte)((apdu[APDU.LC] & 0xFF) + 8);
		
		byte bit = (byte)0x4;
		apdu[APDU.CLS] = (byte)((apdu[APDU.CLS] & 0xFC) | bit);
		
		
		//填充数据
		byte[] cmacData = CipherUtil.padding(apdu);
		byte[] cmac = null;
		
		if (cmacData.length > 8) {
			byte[] data1 = new byte[cmacData.length - 8];
			System.arraycopy(cmacData, 0, data1, 0, data1.length);
			
			byte[] data2 =new byte[8];
			System.arraycopy(cmacData, cmacData.length - 8, data2, 0, 8);
			
			byte[] key1 = new byte[8];
			System.arraycopy(this.rmacKey, 0, key1, 0, 8);
			
			cmac = CipherUtil.mac(key1, data1, lastRmac); //mac
			cmac = CipherUtil.encryptBy3descbc(this.rmacKey, data2, cmac);
		} else {
			cmac = CipherUtil.encryptBy3descbc(rmacKey, cmacData, lastRmac);
		}
		
		System.arraycopy(cmac, 0, this.lastRmac, 0, 8);
		return compare(cmac, mac);
		*/
	}

	public byte[] cmdEncryption(byte[] srcData) throws CryptoException {
		if (srcData == null || srcData.length < APDU.HEADER_LENGTH) {
			return srcData;
		}
		
		int lc = srcData[APDU.LC] & 0xFF;
		byte[] dataField = new byte[lc];
		
		//算mac
		byte[] macData  = genCMAC(srcData);
		//byte[] mac = new byte[8];
		//System.arraycopy(macData, macData.length - 8, mac, 0, 8);
		
		//加密命令数据
		System.arraycopy(srcData, APDU.DATA, dataField, 0, lc);
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
		/*
		int p3 = cmdBytes[Apdu.P3] & 0xFF;
		if (p3 - 8 <= 0) {
			outError("加密命令数据失败，数据长度不正确.");
			return new ScriptCommand(ScriptCommand.NOT);
		}
		
		byte[] cmdData = new byte[p3 - 8];
		System.arraycopy(cmdBytes, 5, cmdData, 0, p3-8);
		//byte[] cmdDataPadding = SCPCryto.scp02Macpadding(cmdData);
		byte[] cmdDataPadding = CipherUtil.padding(cmdData);
		int addLen = cmdDataPadding.length - cmdData.length;
		byte[] tmpCmdData = new byte[cmdBytes.length + addLen];
		
		System.arraycopy(cmdBytes, 0, tmpCmdData, 0, 4);
		tmpCmdData[Apdu.P3] =  (byte)((cmdBytes[Apdu.P3] & 0xFF) + addLen);
		
		//byte[] enData = SCPCryto.scp2Enc(cmdDataPadding, getCurCad().encSessionKey);
		byte[] enData = scp.cmdEncryption(cmdDataPadding);
		System.arraycopy(enData, 0, tmpCmdData, 5, enData.length);
		System.arraycopy(cmdBytes, cmdBytes.length - 8 , tmpCmdData, 5 + enData.length, 8);
		cmdBytes = tmpCmdData;
		System.out.println("enc: " + HexUtil.byteArr2HexStr(cmdBytes));
		*/
		
		
		
		//return CipherUtil.encryptBy3descbc(this.encKey, srcData, new byte[8]);
	}
	
	public static void  main(String[] arg) {
		ISCP scp = new SCP02();
		
	}

	public String getKeyType() {
		return "DES-ECB";
	}
}
