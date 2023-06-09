package com.eastcompeace.capAnalysis;

public class StringUtil {
	public static boolean isNull(String str) {
		return str == null || "null".equals(str);
	}
	
	public static boolean isEmpty(String str) {
		return isNull(str) || "".equals(str.trim());
	}

}
