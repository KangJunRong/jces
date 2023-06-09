package com.ecp.jces.jctool.shell.action;

public interface APDU {
	
	public static final int LC = 4;
	public static final int DATA = 5;
	public static final int P1 = 2;
	public static final int p2 = 3;
	public static final int INS = 1;
	public static final int CLS = 0;
	public static final int HEADER_LENGTH = 5;
	
	public static final byte INS_INIT_UPDATE = (byte)0x50;
	public static final byte INS_EXT_AUTH = (byte)0x82;
	

}
