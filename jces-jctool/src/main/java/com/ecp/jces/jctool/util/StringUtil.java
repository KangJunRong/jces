package com.ecp.jces.jctool.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtil {
	
	public static final String RETURN_REPLACER = new String(new byte[]{0x1c});
	
	/**
	 * 利率
	 * @param str
	 * @return
	 */
	public static boolean isInterestRates(String str) {
		if (!isEmpty(str) && str.matches("^\\d+(\\.\\d+)?$")) {
			return true;
		}
		return false;
	}
	
	public static boolean isSum(String str) {
		if (!isEmpty(str) && str.matches("^\\d+\\.\\d2$")) {
			return true;
		}
		return false;
	}
	
	public static boolean isNull(String str) {
		if (str != null && !"null".equals(str)){
			return false;
		}
		return true;
	}
	
	public static boolean isEmpty(String str) {
		if (!isNull(str) && !"".equals(str.trim())) {
			return false;
		}
		return true;
	}
	
	/**
	 * 金额
	 * @param str
	 * @return
	 */
	public static boolean isMoney(String str) {
		if (!isEmpty(str) && str.matches("^\\d{1,3}(,\\d{3})*$|[\\d]*")) {
			return true;
		}
		return false;
	}
	
	public static boolean isHexString(String hex) {
		if (!isEmpty(hex) && hex.matches("^[\\d,A-F,a-f]*")) {
			if (hex.length() % 2 == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isDigiString(String digiStr) {
		if (!isEmpty(digiStr) && digiStr.matches("^[\\d]*$")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 日期
	 * @param str
	 * @return
	 */
	public static boolean isDate(String str) {
		//(([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29
		if (!isEmpty(str) && str.matches("((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))")) {
		//if (!isEmpty(str) && str.matches("(([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29")) {
			return true;
		}
		return false;
	}
	
	public static boolean isNumber(String str) {
		if (!isEmpty(str) && str.matches("^[\\d]*$")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 如果money1 > money2 返回真；否则返回假
	 * @param money1
	 * @param money2
	 * @return
	 */
	public static boolean compareMoney(String money1, String money2) {
		if (isMoney(money1) && isMoney(money2)) {
			money1 = money1.replaceAll(",", "");
			money2 = money2.replaceAll(",", "");
			
			long m1 = Long.parseLong(money1);
			long m2 = Long.parseLong(money2);
			
			if (m1 > m2) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 回车反转化，把特定字符转换成回车
	 * @param str
	 * @return
	 */
	public static String unchangeForReturn(String str) {
		if (str == null) {
			return null;
		}

		return str.replaceAll(RETURN_REPLACER,"\r\n");
	}
	
	/**
	 * 回车转化，把回车转换成特定字符
	 * @param str
	 * @return
	 */
	public static String changeForReturn(String str) {
		if (str == null) {
			return null;
		}

		String temp = str.replaceAll("\r\n", "\r");
		temp = temp.replaceAll("\n", "\r");
		return temp.replaceAll("\r", RETURN_REPLACER);
	}
	public static void main(String[] args) {
		//System.out.println(isMoney("1"));
//		String 
		
		String str = "1234567890";
		System.out.println("str:" +cut4(str));
		System.out.println(decodePhone("683129745047F0"));
		if (isSum("200809.09")) {
			System.out.println("是");
		} else {
			System.out.println("不是");
		}
		
	}
	
	
	public static void splitSxfx(String sxfx, List varietyAry, List moneyAry, List duedateAry) {
		if (isEmpty(sxfx) || varietyAry == null || duedateAry == null) {
			return ;
		}
		
		String[] rows = sxfx.split("#");
		
		for (int i = 0; i < rows.length; i++) {
			String[] cols = rows[i].split(";");
			if (cols.length < 3)
				continue;
			
			varietyAry.add(cols[0]);
			moneyAry.add(cols[1]);
			duedateAry.add(cols[2]);
		}
	}
	
	public static String isoToGb2312(String str) {
		if (!isEmpty(str)) {
			try {
				return new String(str.getBytes("iso-8859-1"), "gb2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public static String toTlvHex(String key, int value) {
		if (StringUtil.isEmpty(key)) {
			return "";
		}
		String toval = Integer.toHexString(value);
		int length = toval.length() / 2;
		
		return key + Integer.toHexString(length) + toval;
	}
	
	
	public static String toTlvAscii(String key, String value) {
		if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
			return "";
		}
		String toval = value;
		int length = toval.length();
		
		return key + Integer.toHexString(length) + toval;
	}
	
	public static String toTlv(String key, int value) {
		if (StringUtil.isEmpty(key) || value > 0) {
			return "";
		}
		
		return key + 1 + Integer.toHexString(value);
	}
	
	public String DecodeUCS2(String str) {
		if (str != null) {
			String src = "23" + str;
		    byte[] bytes = new byte[src.length() / 2];   
		  
		    for (int i = 0; i < src.length(); i += 2) {
		    	//System.out.println(src.substring(i, i + 2));
		        bytes[i / 2] = (byte) (Integer.parseInt(src.substring(i, i + 2), 16));   
		    }   
		    String reValue = "";   
		    try {   
		        reValue = new String(bytes, "UTF-16BE");   
		    } catch (Exception e) {   
		        e.printStackTrace();
		    }   
		    if (reValue.length() > 1) {
		    	return reValue.substring(1);
		    } else {
		    	return reValue;
		    }
		     
		}
		  
		return "";
	} 
	
	public static String encodeUsc2(String str) {
		return str;
	}
	
	public static String getAccoutLast4(String account) {
		if (!StringUtil.isEmpty(account) && account.length() > 3) {
			account.substring(account.length() -4);
		}
		return account;
	}
	
	public static String toTlvUsc2(String key, String value) {
		if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
			return "";
		}
		String toval = encodeUsc2(value);
		int length = toval.length();
		return key + Integer.toHexString(length) + toval;
	}
	
	
	public static String intToString(int i) {
		return "" + i;
	}
	
	public static String encodePhone(String ph) {
		if (StringUtil.isEmpty(ph)) {
			return "";
		}
		
		String tphone = ph;
		if ((ph.length() % 2) != 0) {
			tphone += "F";
		}
		
		StringBuffer sb = new StringBuffer("9168");
		for (int i = 1; i <= tphone.length(); ) {
			sb.append(tphone.substring(i, i + 1));
			sb.append(tphone.substring(i - 1, i));
			i += 2;
		}
		
		return sb.toString();
		
	}
	
	public static String cut4(String str) {
		if (!StringUtil.isEmpty(str)) {
			if (str.length() > 4) {
				return str.substring(str.length() -4, str.length());
			} else {
				return str;
			}
		}

		return "";
	}
	
    /** 
     * 将byte数组按1个byte分解为2个字符 
     * @param bytes 
     * @return 
     */  
    public static String dumpBytes(byte[] bytes) {  
        StringBuffer sb = new StringBuffer();  
        String s = null;
        for (int i = 0; i < bytes.length; i++) {  
            int n = bytes[i] >= 0 ? bytes[i] : 256 + bytes[i];  
            s = Integer.toHexString(n);  
            if (s.length() < 2) {  
                s = "0" + s;  
            }  
            if (s.length() > 2) {  
                s = s.substring(s.length() - 2);  
            }  
            sb.append(s);  
        }  
        return sb.toString().toUpperCase();  
    }  
	
	
	/*
	public static String[] splitParam(String data) {
		List paramList = new ArrayList();
		int startPosition = 0;
		int endPosition = 0;
		int position = 0;
		int maxPosition = data.length();
		
		while (position < maxPosition) {
			char curChar = data.charAt(position);
			position++;
			
			if (curChar == ' ') {
				endPosition = position -1;
				char nextChar = curChar;
				while (nextChar == curChar) {
					nextChar = data.charAt(position);
					position++;
				}
				
				paramList.add(data.substring(startPosition, endPosition));
				if (nextChar == '"') { //查找下一个'"'字符
					do {
						nextChar = data.charAt(position);
						position++;
					} while(nextChar != '"');
					
					
				} else {
					startPosition = position;
					endPosition = position;
				}
			}
			
		}
		
	}
	*/  
	  
	public static String decodePhone(String ph) {
		if (StringUtil.isEmpty(ph)) {
			return "";
		}
		
		if ((ph.length() % 2) != 0) {
			return "";
		}
		
		if (ph.startsWith("68")) {
			ph = ph.substring(2);
		}
		
		StringBuffer sb = new StringBuffer("");
		for (int i = 1; i <= ph.length(); ) {
			sb.append(ph.substring(i, i + 1));
			sb.append(ph.substring(i - 1, i));
			i += 2;
		}
		
		return sb.toString().toUpperCase().replaceAll("F", "");
		
	}
}
