package com.eastcompeace.capAnalysis;

public class Log {
	public static final boolean isDebug = false;
	
	
	public static final boolean isPrintStackTrace = false;
	public static void println(Object o) {
		if(isDebug) {
			System.out.println(o);
		}
	}
	
	public static void println(String o) {
		if(isDebug) {
			System.out.println(o);
		}
	}
	
	public static void println(Boolean o) {
		if(isDebug) {
			System.out.println(o);
		}
	}

	
	public static void println(int o) {
		if(isDebug) {
			System.out.println(o);
		}
	}
	
	public static void println(Exception e) {
		if(isPrintStackTrace) {
			e.printStackTrace();
		}
	}

}
