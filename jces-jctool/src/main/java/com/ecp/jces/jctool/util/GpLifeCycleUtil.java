package com.ecp.jces.jctool.util;

import com.ecp.jces.jctool.Const;

public class GpLifeCycleUtil {

	public final static String EXE_LOAD_FIEL_LIFE_CYCLE_LOADED = "00000001";
	
	public final static byte ISD_LIFE_CYCLE_OP_READY = 1;
	public final static byte ISD_LIFE_CYCLE_INITIALIZED = 7;
	public final static byte ISD_LIFE_CYCLE_SECURED = 15;
	public final static byte ISD_LIFE_CYCLE_CARD_LOCKED = 63;
	public final static byte ISD_LIFE_CYCLE_TERMINATED = (byte)255;
	
	/*
	public static String getISDStatus(byte code) {
		String binaryCode = Integer.toBinaryString(code & 0xFF);
		String padding = "00000000";
		if (binaryCode.length() < 8) {
			binaryCode = padding.substring(0, 8 - binaryCode.length()) + binaryCode;
		}

		for (int i = 0; i < ISD_LIFE_CYCLE_STATUS.length; i++) {
			for (int j = 0; j < ISD_LIFE_CYCLE_STATUS[i][0].length(); j++) {
				char bit = ISD_LIFE_CYCLE_STATUS[i][0].charAt(j);
				if (bit != 'x' && bit != binaryCode.charAt(j)) {
					continue;
				}
			}
		}
		
		return null;
	}
	*/
	
	public static String getAppStatus(byte code) {
		switch(code) {
		case Const.GP_APPLICATION_LIFE_CYCLE_STATE_INSTALL:
			return "INSTALLED";
		case Const.GP_APPLICATION_LIFE_CYCLE_STATE_SELECTABLE:
			return "SELECTABLE";
		case Const.GP_APPLICATION_LIFE_CYCLE_STATE_LOCKED:
			return "LOCKED";
		case Const.GP_APPLICATION_LIFE_CYCLE_STATE_PERSONALIZED:
			return "PERSONALIZED";
		default:
			break;
		}
		return "";
	}
	
	public static String getCardStatus(byte code) {
		switch (code) {
		case Const.GP_CARD_LIFE_CYCLE_STATE_READY:
			return "OP_READY";
		case Const.GP_CARD_LIFE_CYCLE_STATE_INITIALIZED:
			return "INITIALIZED";
		case Const.GP_CARD_LIFE_CYCLE_STATE_SECURED:
			return "SECURED";
		case Const.GP_CARD_LIFE_CYCLE_STATE_LOCKED:
			return "CARD_LOCKED";
		case Const.GP_CARD_LIFE_CYCLE_STATE_TERMINAL:
			return "TERMINATED";
		default:
			return "None";
		}
	}
	
	public static String getExeLoadFileStatus(byte code) {
		return "LOADED";
	}
}
