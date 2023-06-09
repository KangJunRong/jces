package com.ecp.jces.jctool;

public class Const {
	
	//命令代码
	//DISPLAY TEXT
	public static final int CMD_DISPLAY_TEXT = (byte)0x21;
	public static final String CMD_NAME_DISPLAY_TEXT = "DISPLAY TEXT";
	//GET INPUT
	public static final int CMD_GET_INPUT = (byte)0x23;
	public static final String CMD_NAME_GET_INPUT = "GET INPUT";
	//SELECT ITEM
	public static final int CMD_SELECT_ITEM = (byte)0x24;
	public static final String CMD_NAME_SELECT_ITEM = "SELECT ITEM";
	//SET UP MENU
	public static final int CMD_SET_UP_MENU = (byte)0x25;
	public static final String CMD_NAME_SET_UP_MENU = "SET UP MENU";
	//send sms
	public static final int CMD_SEND_SMS = (byte)0x13;
	public static final String CMD_NAME_SEND_SMS = "SEND SHORT MESSAGE";
	//发送通用指令
	public static final int CMD_COMM_STATEMWNT = (byte)0x00;
	public static final String CMD_NAME_COMM_STATEMENT = "send comm statement";
	
	
	
	//tag type
	public static final int TAG_COMM_DETAIL = (byte)0x01;
	public static final int TAG_ALPHA_IDENTIFIER = (byte)0x05;
	public static final int TAG_ITEM = 0x0F;
	public static final int TAG_TEXT_STRING = (byte)0x0D;
	public static final int TAG_RESPONSE_LENGTH = (byte)0x11;
	
	public static final int DCS_ACSII = (byte)0x04;
	public static final int DCS_UCS2 = (byte)0x08;
	public static final int DCS_BCD = (byte)0x01;
	
	//view id                                       com.eastcompeace.jces.eclipse.view.jcesShell
	public static final String VIEW_ID_SHELLVIEW = "com.eastcompeace.jces.eclipse.view.jcesShell";
	public static final String VIEW_ID_MESIMULATIONVIEW = "com.eastcompeace.jces.eclipse.view.MeSimulator";
	
	//perspective id
	public static final String PERSPECTIVE_ID_SIMDEBUG = "com.eastcompeace.jces.eclipse.perspectives.simdebug";
	
	public static final String JCESSHELL_CAD_TERMINAL_PREFIX = "/terminal ";
	public static final String JCESSHELL_CAD_TERMINAL_REMOTE = "Remote";
	public static final String JCESSHELL_CAD_TERMINAL_SIMULATOR = "Simulator";
	
	//launchConfig cardManagerAid
	public static final String LAUNCHCONFIGPAGE_CARD_MANAGER_AID = "cardManagerAid";
	
	//GP 指令
	public static final String GP_CMD_INIT_UPDATE = "init-update";
	public static final String GP_CMD_EXT_AUTH = "ext-auth";
	public static final String GP_CMD_UPLOAD = "upload";
	public static final String GP_CMD_INSTALL = "install";
	public static final String GP_CMD_DELETE = "delete";
	public static final String GP_CMD_CARD_INFO = "card-info";
	public static final String GP_CMD_SET_APPLET = "set-applet";
	public static final String GP_CMD_SET_STATE = "set-state";
	public static final String GP_CMD_PUT_KEY = "put-key";
	public static final String GP_CMD_PUT_KEYSET = "put-keyset";
	public static final String GP_CMD_CHANGE_PIN = "change-pin";
	public static final String GP_CMD_UNBLOCK_PIN = "unblock-pin";
	public static final String GP_CMD_SET_KEY = "set-key";
	public static final String GP_CMD_GET_CPLC = "get-cplc";
	public static final String GP_CMD_SELECT = "select";
	
	public static final String GP_CMD_AUTH = "auth";
	public static final String GP_CMD_PRINT_KEY = "print-key";
	
	//javacard 指令
	public static final String JC_CMD_POWERUP = "powerup";
	public static final String JC_CMD_POWERED_DOWN = "powered_down";
	
	//一般指令
	public static final String GENERIC_CMD_ATR = "/atr";
	public static final String GENERIC_CMD_CAP_INFO = "/cap-info";
	public static final String GENERIC_CMD_CARD = "/card";
	public static final String GENERIC_CMD_CLOSE = "/close";
	public static final String GENERIC_CMD_APP_CLOSE = "close";
	public static final String GENERIC_CMD_LIST_VARS = "/list-vars";
	public static final String GENERIC_CMD_SET_VAR = "/set-var";
	public static final String GENERIC_CMD_MODE = "/mode";
	public static final String GENERIC_CMD_SELECT = "/select";
	public static final String GENERIC_CMD_SEND = "/send";
	public static final String GENERIC_CMD_TERMINAL = "/terminal";
	public static final String GENERIC_CMD_HELP = "/help";
	public static final String GENERIC_CMD_QUIT = "quit";
	public static final String GENERIC_CMD_DOACTION = "/action";
	public static final String GENERIC_CMD_MANAGE_CHANGE = "/manage-channel";
	public static final String GENERIC_CMD_SET_CHANGE = "/set-channel";
	public static final String GENERIC_CMD_NORMAL_STATUS = "/normal-status";

	
	public static final byte[] GP_SCP_C_MAC_CONSTANT = {(byte)0x01, (byte)0x01} ;
	public static final byte[] GP_SCP_R_MAC_CONSTANT = {(byte)0x01, (byte)0x02} ;
	public static final byte[] GP_SCP_ENC_CONSTANT = {(byte)0x01, (byte)0x82} ;
	public static final byte[] GP_SCP_DEK_CONSTANT = {(byte)0x01, (byte)0x81} ;
	
	public static final int GP_STATUS_NO_ERROR = 0x9000;
	public static final int GP_STATUS_GET_NEXT = 0x6310;
	
	//GP Application 生命周期
	public static final byte GP_APPLICATION_LIFE_CYCLE_STATE_INSTALL = (byte)0x03;
	public static final byte GP_APPLICATION_LIFE_CYCLE_STATE_SELECTABLE = (byte)0x07;
	public static final byte GP_APPLICATION_LIFE_CYCLE_STATE_PERSONALIZED = (byte)0x0F;
	public static final byte GP_APPLICATION_LIFE_CYCLE_STATE_LOCKED = (byte)0xFF;
	public static final byte GP_APPLICATION_LIFE_CYCLE_STATE_UNLOCKED = (byte)0x7F;
	
	//card 生命周期
	public static final byte GP_CARD_LIFE_CYCLE_STATE_READY = (byte)0x01;
	public static final byte GP_CARD_LIFE_CYCLE_STATE_INITIALIZED = (byte)0x07;
	public static final byte GP_CARD_LIFE_CYCLE_STATE_SECURED = (byte)0x0F;
	public static final byte GP_CARD_LIFE_CYCLE_STATE_LOCKED = (byte)0x7F;
	public static final byte GP_CARD_LIFE_CYCLE_STATE_TERMINAL = (byte)0xFF;
	
	public static final String CARD_MANAGER_DEFAULT_AID = "D1560001010001600000000100000000";
	
	public static final byte[] GP_KEY_VALUE_DATA = new byte[8];
	
	public static final String GP_INIT_KEY1 = "255/1/DES-ECB/404142434445464748494a4b4c4d4e4f";
	public static final String GP_INIT_KEY2 = "255/2/DES-ECB/404142434445464748494a4b4c4d4e4f";
	public static final String GP_INIT_KEY3 = "255/3/DES-ECB/404142434445464748494a4b4c4d4e4f";
	
	public static final String GP_SCP03_INIT_KEY1 = "255/1/AES/404142434445464748494a4b4c4d4e4f404142434445464748494a4b4c4d4e4f";
	public static final String GP_SCP03_INIT_KEY2 = "255/2/AES/404142434445464748494a4b4c4d4e4f404142434445464748494a4b4c4d4e4f";
	public static final String GP_SCP03_INIT_KEY3 = "255/3/AES/404142434445464748494a4b4c4d4e4f404142434445464748494a4b4c4d4e4f";
}