package com.ecp.jces.core.utils;

import java.io.UnsupportedEncodingException;

public class HexUtil {

	/**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
	 * 互为可逆的转换过程
	 * 
	 * @param strIn
	 *            需要转换的字符串
	 * @return 转换后的byte数组
	 * @throws Exception
	 *             本方法不处理任何异常，所有异常全部抛出
	 * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
	 */
	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}
	
	/**
	 * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
	 * hexStr2ByteArr(String strIn) 互为可逆的转换过程
	 * 
	 * @param arrB
	 *            需要转换的byte数组
	 * @return 转换后的字符串
	 * @throws Exception
	 *             本方法不处理任何异常，所有异常全部抛出
	 */
	public static String byteArr2HexStr(byte[] arrB)  {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}
	
	public static String byteToHex(byte arg) {
		int con = arg & 0xFF;
		String tmp = Integer.toHexString(con);
		if (tmp.length() % 2 == 0) {
			return tmp.toUpperCase();
		} else {
			return ("0" + tmp).toUpperCase();
		}
	}
	
	public static String intToHex(int arg) {
		int con = arg & 0xFF;
		String tmp = Integer.toHexString(con);
		if (tmp.length() % 2 == 0) {
			return tmp.toUpperCase();
		} else {
			return ("0" + tmp).toUpperCase();
		}
	}
	
	/*
	public static byte[] hexToBytes(String hex) {
		if (StringUtil.isHexString(hex)) {
			int len = hex.length() / 2;
			byte[] bt = new byte[len];
			int tmp;
			for (int i = 0; i < len; i++) {
				//System.out.println(hex.substring(i * 2, i * 2 + 2));
				tmp = Integer.parseInt(hex.substring(i * 2, i * 2 + 2),16);
				
				bt[i] = (byte)tmp;
				//System.out.println(bt[i]);
			}			
			return bt;
		}
		
		return null;
	}
	*/
	
	public static String byte2Ucs2(byte[] data, int off, int length) {
		if (data == null || off + length > data.length)
			return null;
		
		byte[] rs = new byte[length];
		
		for (int i = 0; i < length; i++) {
			rs[i] = data[off + i];
		}
		
		String str = "";
		try {
			str = new String(rs,"UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return str;
	}
	
	public static void main(String[] args) {
		
		int bt = 4;
		
		System.out.println(Integer.toBinaryString(bt));
		bt = bt >> 0 & 1;
		System.out.println(Integer.toBinaryString(bt));
		
		//int out = bt & 1;
		
		System.out.println(bt);
	}
	
	public static String byteArr2HexStr(byte[] arrB, String separator)  { //separator
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			if (i != 0 && separator != null) {
				sb.append(separator);
			}
			
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}
	
	public static String byteArr2Ascii(byte[] data) {
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			if (data[i] > 32 && data[i] < 127) {
				sb.append((char)data[i]);
			} else {
				sb.append(".");
			}
			
		}
		return sb.toString();
	}
	
	public static String byteArr2Ascii(byte[] data, int off, int length) {
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			if ((off + i) >= data.length)
				break;
			
			sb.append((char)data[off + i]);
			
		}
		return sb.toString();
	}
}
