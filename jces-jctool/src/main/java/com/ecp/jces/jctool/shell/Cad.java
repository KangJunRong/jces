package com.ecp.jces.jctool.shell;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.*;
import com.ecp.jces.jctool.shell.action.ISCP;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;
import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apdutool.Msg;

public abstract class Cad {

	protected JcesShellView jcsv;
	
	public static final byte OUT_FLG_CMD = 1;
	public static final byte OUT_FLG_DATA = 2;
	public static final byte OUT_FLG_RES = 3;
	public static final byte OUT_FLG_PLAIN_TEXT = 4;
	public static final int APDU_LC_MAX_LEN = 260;
	
	private static int logicChannel = 0;
	//public byte[] ATR;
	
	public static int getLogicChannel() {
		return logicChannel;
	}

	public static void setLogicChannel(int logicChannel) {
		Cad.logicChannel = logicChannel;
	}

	/*
	public byte[] hostChallenge = {(byte)0x22,(byte)0x30,(byte)0x31,(byte)0x21,(byte)0x21,(byte)0x21,(byte)0x21,(byte)0x21,};
	public byte[] cardChallenge = new byte[8];
	public byte[] hostCryptogram = new byte[8];
	public byte[] cardCryptogram = new byte[8];
	*/
	private ISCP scp;

	public ISCP getScp() {
		return scp;
	}

	public void setScp(ISCP scp) {
		this.scp = scp;
	}

	/*
	public byte keyset;
	public byte[] s_mac;
	public byte[] s_enc;
	public byte[] s_dek;

	//public boolean initState = false;
	
	public byte[] c_mac;
	public byte[] c_enc;
	public byte[] c_dek;
	public String mode = "OP_READY";

	public byte tmpChannelProtocol;
	public byte tmpkeyVersionNo;
	public byte[] tmpSeqCounter = new byte[2];
	
	public byte channelProtocol;
	public byte keyVersionNo;
	public byte[] seqCounter = new byte[2];
	
	public byte[] cmacSessionKey;
	public byte[] rmacSessionKey;
	public byte[] encSessionKey;
	public byte[] dekSessionKey;
	public byte[] icv;
	public byte securityLevel = 0;
	*/
	private List jcshPathList = new ArrayList();
	public boolean isGp = false;
	
	public AID cardManagerAid;
	public IKey encKey = new Key("255/1/DES-ECB/404142434445464748494a4b4c4d4e4f"); //静态会话key
	public IKey macKey = new Key("255/1/DES-ECB/404142434445464748494a4b4c4d4e4f"); //静态mac key
	public IKey dekKey = new Key("255/1/DES-ECB/404142434445464748494a4b4c4d4e4f"); //静态数据 key
	
	//private Map keysetMap = new HashMap();
	private List keySetList = new ArrayList();
	
/*	
	public byte[] getHostCryptogram() {
		return hostCryptogram;
	}
	
	public void setHostCryptogram(byte[] hostCryptogram) {
		this.hostCryptogram = hostCryptogram;
	}

	public byte[] getCardCryptogram() {
		return cardCryptogram;
	}

	public void setCardCryptogram(byte[] cardCryptogram) {
		this.cardCryptogram = cardCryptogram;
	}

	public byte getChannelProtocol() {
		return channelProtocol;
	}

	public void setChannelProtocol(byte channelProtocol) {
		this.channelProtocol = channelProtocol;
	}
	//private Set keySet = new HashSet();
	
	public byte[] getHostChallenge() {
		return hostChallenge;
	}

	public void setHostChallenge(byte[] hostChallenge) {
		this.hostChallenge = hostChallenge;
	}

	public byte[] getCardChallenge() {
		return cardChallenge;
	}

	public void setCardChallenge(byte[] cardChallenge) {
		this.cardChallenge = cardChallenge;
	}
*/
//	private BerTag curBerTag;
	
	protected PrintWriter log = new PrintWriter(System.out, true);
	
	public abstract void init(JcesShellView js);
	
	public abstract void process(ScriptCommand cmd) throws CmdProcessException;
	
	//public abstract void connection();
	
	//public abstract void close();
	
	public abstract void destory();
	
	public static byte[] hexStringToBytes(String hex) {
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
	
	private ScriptCommand buildSendCmd(String input) throws CmdProcessException {
		try {
			input = input.replaceFirst("/send ", "");
			
			input = input.replaceAll(" ", "");
			if (StringUtil.isHexString(input)) {
				byte[] cmdBytes = hexStringToBytes(input);
				if (cmdBytes != null && cmdBytes.length >= 4) {
					Apdu apdu = new Apdu();
					apdu.command[Apdu.CLA] = cmdBytes[Apdu.CLA];
					apdu.command[Apdu.INS] = cmdBytes[Apdu.INS];
					apdu.command[Apdu.P1] = cmdBytes[Apdu.P1];
					apdu.command[Apdu.P2] = cmdBytes[Apdu.P2];

					if (cmdBytes.length == 4) {
						apdu.command[Apdu.P3] = 0;
						apdu.setDataIn(null);
					} else {
						apdu.command[Apdu.P3] = cmdBytes[Apdu.P3];
						
						byte[] datain = getDataIn(cmdBytes);
						if (datain == null) {
							//apdu.setDataIn(null,(cmdBytes[Apdu.P3] & 0xff));
							apdu.Le = cmdBytes[Apdu.P3] & 0xff;
							apdu.setDataIn(null);
							//apdu.setDataIn(new byte[cmdBytes[Apdu.P3] & 0xff], cmdBytes[Apdu.P3] & 0xff);
						} else {
							apdu.setDataIn(getDataIn(cmdBytes));
						}
					}
					return new ScriptCommand(ScriptCommand.APDU, apdu, input);
				} else {
					throw new CmdProcessException(Msg.getMessage("parser.4"));
				}
			} else {
				throw new CmdProcessException("analyse command exception!");
			}
		} catch (CmdProcessException ex) {
			throw ex;
		}
	}
	
	private ScriptCommand buildActionCmd(String input) throws CmdProcessException {
		try {
			input = input.replaceFirst("/action ", "");
			
			input = input.replaceAll(" ", "");
			if (StringUtil.isHexString(input)) {
				byte[] cmdBytes = hexStringToBytes(input);
				if (cmdBytes != null && cmdBytes.length >= 4) {
					Apdu apdu = new Apdu();
					apdu.command[Apdu.CLA] = cmdBytes[Apdu.CLA];
					apdu.command[Apdu.INS] = cmdBytes[Apdu.INS];
					apdu.command[Apdu.P1] = cmdBytes[Apdu.P1];
					apdu.command[Apdu.P2] = cmdBytes[Apdu.P2];

					if (cmdBytes.length == 4) {
						apdu.command[Apdu.P3] = 0;
						apdu.setDataIn(null);
					} else {
						apdu.command[Apdu.P3] = cmdBytes[Apdu.P3];
						
						byte[] datain = getDataIn(cmdBytes);
						if (datain == null) {
							//apdu.setDataIn(null,(cmdBytes[Apdu.P3] & 0xff));
							apdu.Le = cmdBytes[Apdu.P3] & 0xff;
							apdu.setDataIn(null);
							//apdu.setDataIn(new byte[cmdBytes[Apdu.P3] & 0xff], cmdBytes[Apdu.P3] & 0xff);
						} else {
							apdu.setDataIn(getDataIn(cmdBytes));
						}
					}
					return new ScriptCommand(ScriptCommand.APDU_ACTION,apdu ,input);
				} else {
					throw new CmdProcessException(Msg.getMessage("parser.4"));
				}
			} else {
				throw new CmdProcessException("analyse command exception!");
			}
		} catch (CmdProcessException ex) {
			throw ex;
		}	
	}
	
	private ScriptCommand buildCmdByApdu(String cmd, String apduInput) throws CmdProcessException {
		if (StringUtil.isEmpty(apduInput)) {
			throw new CmdProcessException(Msg.getMessage("parser.4"));
		}
		
		try {
			if (StringUtil.isHexString(apduInput)) {
				byte[] cmdBytes = hexStringToBytes(apduInput);
				if (cmdBytes != null && cmdBytes.length >= 4) {
					Apdu apdu = new Apdu();
					apdu.command[Apdu.CLA] = cmdBytes[Apdu.CLA];
					apdu.command[Apdu.INS] = cmdBytes[Apdu.INS];
					apdu.command[Apdu.P1] = cmdBytes[Apdu.P1];
					apdu.command[Apdu.P2] = cmdBytes[Apdu.P2];
	
					if (cmdBytes.length == 4) {
						apdu.command[Apdu.P3] = 0;
						apdu.setDataIn(null);
					} else {
						apdu.command[Apdu.P3] = cmdBytes[Apdu.P3];
						
						byte[] datain = getDataIn(cmdBytes);
						if (datain == null) {
							//apdu.setDataIn(null,(cmdBytes[Apdu.P3] & 0xff));
							apdu.Le = cmdBytes[Apdu.P3] & 0xff;
							apdu.setDataIn(null);
							//apdu.setDataIn(new byte[cmdBytes[Apdu.P3] & 0xff], cmdBytes[Apdu.P3] & 0xff);
						} else {
							apdu.setDataIn(getDataIn(cmdBytes));
						}
					}
					return new ScriptCommand(ScriptCommand.APDU,apdu ,cmd);
				} else {
					throw new CmdProcessException(Msg.getMessage("parser.4"));
				}
			} else {
				throw new CmdProcessException("analyse command exception!");
			}
		} catch (CmdProcessException ex) {
			throw ex;
		}	
	}
	
	/*
	private ScriptCommand buildSetKeyCmd(String input) throws CmdProcessException {
		if (input.startsWith(Const.GP_CMD_SET_KEY + " ")) {
			String[] inputAry = input.split("\\s+");
			if (inputAry.length >= 2) {
				for (int i = 1; i < inputAry.length; i++) {
					Key key = new Key(inputAry[i]);
					keysetMap.put(key.getKeyset() + "/" + key.getId() , key);
				}
				return new ScriptCommand(ScriptCommand.NOT);
			}
		}
		throw new CmdProcessException(Msg.getMessage("parser.4"));
	}
	
	private ScriptCommand buildPrintKeyCmd() throws CmdProcessException {
		Iterator keyset = keysetMap.keySet().iterator();
		while (keyset.hasNext()) {
			String keyid = (String)keyset.next();
			Key key = (Key)keysetMap.get(keyid);
			if (key != null) {
				jcsv.appendLine(key.getString(), JcesShell.CMD_STYLE_RES);
			}
		}
		return new ScriptCommand(ScriptCommand.NOT);
	}
	*/
	/*
	private ScriptCommand buildInitUpdateCmd(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		keyVersionNo = 0;
		
		if (params.length >= 2) {
			if (!StringUtil.isNumber(params[1])) {
				throw new CmdProcessException("jcesshell:For input string: \"" + params[1] + "\"" );
			}
			
			int kv = Integer.parseInt(params[1]);
			if (kv > 0 && kv < 255) {
				keyVersionNo = (byte)kv;
			}
			
			if (params.length >= 4) {
				throw new CmdProcessException("jcesshell: init-update: Excess arguments: " + params[3] + " ...");
			}
			
			if (params.length >= 3) {
				throw new CmdProcessException("jcesshell:unknown secure channel protocol: " + params[2]);
			}
		} 
		return buildCmdByApdu(input, "8050" + HexUtil.byteToHex(keyVersionNo) + "0008" + HexUtil.byteArr2HexStr(hostChallenge));
	}
	
	
	private ScriptCommand buildUploadCmd(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		if (params.length >= 2) {
			
			if (params.length > 3) {
				throw new CmdProcessException("jcesshell: upload: Excess arguments: " + params[2] + " ...");
			}
			
			String path = params[1].trim();
			if (path.startsWith("\"")) {
				path = path.substring(1, path.length());
			}
			
			if (path.endsWith("\"")) {
				path = path.substring(0, path.length() - 1);
			}
			
			try {
				ResourceBundle msg = PropertyResourceBundle.getBundle("com/eastcompeace/jces/eclipse/internal/capfiledownload/MessagesBundle");
		        PrintWriter logWriter = new PrintWriter(System.err, true);
		        PrintWriter scriptWriter = new PrintWriter(System.out, true);

		        CAP cap = null;
		        cap = new CAPScriptGopFormat(jcsv, path, logWriter, msg, null);
		        
		        // verify the CAP file has the right version number of valid components
		        if (cap.verifyCAP() == 0) {
		            cap.genScript(scriptWriter, false);
		        }
			} catch (Exception ex) {
				throw new CmdProcessException("jcesshell:Cannot read <" + path + ">: " + ex.getMessage() + ">");
			}
		} 
		
		return new ScriptCommand(ScriptCommand.NOT);
	}
	*/
	private ScriptCommand buildInstallCmd(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		
		if (params.length < 2) {
			throw new CmdProcessException("jcesshell: install: Missing mandatory argument: |PackageAID  |AppletAID");
		} else if (params.length < 3) {
			throw new CmdProcessException("jcesshell: install: Missing mandatory argument: |AppletAID");
		} else if (params.length > 4) {
			throw new CmdProcessException("jcesshell: install: Excess arguments: " + params[4] + " ...");
		}
		
		String spkgAid =  params[1];
		String sappAid = params[2];
		AID pkgAid = null;
		AID appAid = null;
		
		if (!spkgAid.startsWith("|")) {
			if (!StringUtil.isHexString(spkgAid)) {
				throw new CmdProcessException("jcesshell: install: PackageAID Illegal hex digits");
			}
		}
		pkgAid = new AID(spkgAid);
		
		if (!sappAid.startsWith("|")) {
			if (!StringUtil.isHexString(sappAid)) {
				throw new CmdProcessException("jcesshell: install: AppletAID Illegal hex digits");
			}
		}
		appAid = new AID(sappAid);
		
		//生成install cmd
		buildCmdByApdu(input, "");
		
		
		return new ScriptCommand(ScriptCommand.NOT);
	}
	
	private ScriptCommand gpCardInfoCmd(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		
		if (params.length < 2) {
			throw new CmdProcessException("jcesshell: install: Missing mandatory argument: |PackageAID  |AppletAID");
		} else if (params.length < 3) {
			throw new CmdProcessException("jcesshell: install: Missing mandatory argument: |AppletAID");
		} else if (params.length > 4) {
			throw new CmdProcessException("jcesshell: install: Excess arguments: " + params[4] + " ...");
		}
		
		String spkgAid =  params[1];
		String sappAid = params[2];
		AID pkgAid = null;
		AID appAid = null;
		
		if (!spkgAid.startsWith("|")) {
			if (!StringUtil.isHexString(spkgAid)) {
				throw new CmdProcessException("jcesshell: install: PackageAID Illegal hex digits");
			}
		}
		pkgAid = new AID(spkgAid);
		
		if (!sappAid.startsWith("|")) {
			if (!StringUtil.isHexString(sappAid)) {
				throw new CmdProcessException("jcesshell: install: AppletAID Illegal hex digits");
			}
		}
		appAid = new AID(sappAid);
		
		//生成install cmd
		buildCmdByApdu(input, "");
		
		
		return new ScriptCommand(ScriptCommand.NOT);
	}
	/*
	protected ScriptCommand analyse(String input) throws CmdProcessException {
		if (input == null || "".equals(input.trim())) {
			throw new CmdProcessException(Msg.getMessage("parser.4"));
		}
		String tmpInput = input.toLowerCase();
		
		input = input.toLowerCase().trim();
		
		//GP指令
		if (input.startsWith(Const.GP_CMD_INIT_UPDATE)) {
			return buildInitUpdateCmd(input);
		} else if (input.startsWith(Const.GP_CMD_EXT_AUTH)) {
			
		} else if (input.startsWith(Const.GP_CMD_UPLOAD)) {
			//return buildUploadCmd(input);
		} else if (input.startsWith(Const.GP_CMD_INSTALL)) {
			
		} else if (input.startsWith(Const.GP_CMD_DELETE)) {
			
		} else if (input.startsWith(Const.GP_CMD_CARD_INFO)) {
			
		} else if (input.startsWith(Const.GP_CMD_SET_APPLET)) {
			
		} else if (input.startsWith(Const.GP_CMD_SET_STATE)) {
			
		} else if (input.startsWith(Const.GP_CMD_PUT_KEY)) {
			
		} else if (input.startsWith(Const.GP_CMD_PUT_KEYSET)) {
			
		} else if (input.startsWith(Const.GP_CMD_CHANGE_PIN)) {
			
		} else if (input.startsWith(Const.GP_CMD_UNBLOCK_PIN)) {
			
		} else if (input.startsWith(Const.GP_CMD_SET_KEY)) {
			return buildSetKeyCmd(input);
		} else if (input.startsWith(Const.GP_CMD_GET_CPLC)) {
			
		} else if (input.startsWith(Const.GP_CMD_PRINT_KEY)) {
			return buildPrintKeyCmd();
		}
		
		//一般指令
		if (input.startsWith(Const.GENERIC_CMD_ATR)) {
			return atrCmd();
		} else if (input.startsWith(Const.GENERIC_CMD_CAP_INFO)) {
			
		} else if (input.startsWith(Const.GENERIC_CMD_CARD)) {
			
		} else if (input.startsWith(Const.GENERIC_CMD_CLOSE)) {
			return new ScriptCommand(ScriptCommand.CLOSE, input);
		} else if (input.startsWith(Const.GENERIC_CMD_LIST_VARS)) {
			
		} else if (input.startsWith(Const.GENERIC_CMD_SET_VAR)) {
			
		} else if (input.startsWith(Const.GENERIC_CMD_MODE)) {
			
		} else if (input.startsWith(Const.GENERIC_CMD_SELECT)) {
			
		} else if (input.startsWith(Const.GENERIC_CMD_SEND)) {
			return buildSendCmd(input);
		} else if (input.startsWith(Const.GENERIC_CMD_TERMINAL)) {
			
		} else if (input.startsWith(Const.GENERIC_CMD_HELP)) {
			
		} else if (input.startsWith(Const.GENERIC_CMD_QUIT)) {
			
		} 
		
		
		//java card 一般指令
		if (input.startsWith(Const.JCESSHELL_CAD_TERMINAL_PREFIX)) {
			return new ScriptCommand(ScriptCommand.OPEN, input);
		}
		
		if ("close".equals(input)) {
			return new ScriptCommand(ScriptCommand.CLOSE, input);
		}
		
		if ("powerdown".equals(input)) 
			return new ScriptCommand(ScriptCommand.POWER_DOWN);
		
		if ("powerup".equals(input))
			return new ScriptCommand(ScriptCommand.POWER_UP);
		
		if (input.startsWith("echo "))
			return new ScriptCommand(ScriptCommand.ECHO);
		
		if (input.startsWith("output ")){
			if (input.endsWith(" on")){
				return new ScriptCommand(ScriptCommand.OUTPUTON);
			}
			
			if (input.endsWith(" off")){
				return new ScriptCommand(ScriptCommand.OUTPUTOFF);
			}
		}
		
		if (input.startsWith("delay")) {
			String[] delayAry = input.split(" ");
			if (delayAry.length >= 2 && StringUtil.isNumber(delayAry[delayAry.length -1])) {
				return new ScriptCommand(ScriptCommand.DELAY, new Integer(delayAry[delayAry.length -1]));
			}
		}
		
		if (tmpInput.startsWith("/action ")) {
			return buildActionCmd(input);
		}

		throw new CmdProcessException(Msg.getMessage("parser.4"));
	}
*/
//	public BerTag analyseData(byte[] data) {
//		int tag = 0;
//		int index = 0;
//		int length = 0;
//		boolean flg = false;
//
//		if (data[0] == (byte)0xD0) {
//			if (data[1] == (byte)0x81) {
//				index = index + 3;
//			} else {
//				index = index + 2;
//			}
//
//			tag = data[index++] & 0x7F;
//			if (tag != Const.TAG_COMM_DETAIL)
//				return null;
//
//			length = data[index++];
//			int number = data[index++];
//			int commType = data[index++];
//			int qualifier = data[index++];
//
//
//			BerTag berTag = null;
//
//			//创建BerTag对象
//			switch (commType) {
//			case Const.CMD_DISPLAY_TEXT:
//				berTag = new DisplayTextTag();
//				break;
//
//			case Const.CMD_GET_INPUT:
//				berTag = new GetInputTag();
//				break;
//
//			case Const.CMD_SELECT_ITEM:
//				berTag = new SelectItemTag();
//				break;
//
//			case Const.CMD_SET_UP_MENU:
//				berTag = new SetUpMenuTag();
//				break;
//
//			case Const.CMD_SEND_SMS:
//				berTag = new SendSmsTag();
//				break;
//
//			default:
//				berTag = new BerTag(tag);
//				break;
//			}
//
//			//命令详细标记
//			CommDetailTag commDetail = new CommDetailTag();
//			commDetail.setNumber(number);
//			commDetail.setQualifier(qualifier);
//			berTag.addTag(commDetail);
//
//			//创建子
//			while (true) {
//				if (index > data.length -2)
//					break;
//				tag = data[index++] & (byte)0x7F;
//				length = data[index++];
//
//				switch (tag) {
//				case Const.TAG_ALPHA_IDENTIFIER:
//					AlphaIdentifierTag alphaTag = new AlphaIdentifierTag();
//
//					flg = false;
//					if (data[index] == (byte)0x80) {
//						flg = true;
//					}
//
//					String alphaIdentifier = null;
//					if (flg) {
//						alphaIdentifier = HexUtil.byte2Ucs2(data, index + 1, length - 1);
//					} else {
//						alphaIdentifier = HexUtil.byteArr2Ascii(data, index, length - 1);
//					}
//
//					alphaTag.setAlphaIdentifier(alphaIdentifier);
//					alphaTag.setValue(alphaIdentifier);
//					berTag.addTag(alphaTag);
//					index += length;
//					break;
//
//				case Const.TAG_ITEM:
//					ItemTag itemTag = new ItemTag();
//					int id = data[index++];
//
//					flg = false;
//					if (data[index] == (byte)0x80) {
//						flg = true;
//					}
//					String textString = null;
//					if (flg) {
//						textString = HexUtil.byte2Ucs2(data, index + 1, length - 2);
//					} else {
//						textString = HexUtil.byteArr2Ascii(data, index, length -1);
//					}
//
//					itemTag.setIdentifier(id);
//					itemTag.setTextString(textString);
//					itemTag.setValue(byte2HexString(data, index - 2, length));
//					berTag.addTag(itemTag);
//					index += length -1;
//					break;
//
//				case Const.TAG_TEXT_STRING:
//					TextStringTag textStringTag = new TextStringTag();
//					int dcs = data[index++];
//
//					String textStr = null;
//					if (dcs == 4) {
//						textStr = HexUtil.byteArr2Ascii(data, index, length - 1);
//					} else {
//						textStr = HexUtil.byte2Ucs2(data, index, length - 1);
//					}
//
//					textStringTag.setDcs(dcs);
//					textStringTag.setTextString(textStr);
//					textStringTag.setValue(byte2HexString(data, index - 1, length));
//					berTag.addTag(textStringTag);
//					index += length -1;
//					break;
//
//				case Const.TAG_RESPONSE_LENGTH:
//					ResponseLengthTag resLengthTag = new ResponseLengthTag();
//					int minLength = data[index++];
//					int maxLength = data[index++];
//					resLengthTag.setMinLength(minLength);
//					resLengthTag.setMaxLength(maxLength);
//					berTag.addTag(resLengthTag);
//					break;
//
//				default:
//					SimpleTag simpleTag = new SimpleTag(tag);
//					simpleTag.setValue(byte2HexString(data, index, length));
//					berTag.addTag(simpleTag);
//					index += length;
//					break;
//				}
//			}
//			return berTag;
//		}
//
//		return null;
//	}
	
	
	protected byte[] getDataIn(byte[] in) throws CmdProcessException {
		if (in.length > 5) {
			int p3  = in[Apdu.P3] & 0xFF;
			if ( p3 + 5 == in.length || p3 + 6 == in.length) {
				byte[] entityAry = new byte[p3];
				for (int i = 0;i < p3; i++) {
					entityAry[i] = in[Apdu.P3 + i + 1];
				}
				return entityAry;
			} else {
				throw new CmdProcessException("Wrong length!");
			}
		}
		return null;
	}
	
	protected String byte2HexString(byte[] data, int off, int length) {
		if (data == null || off + length > data.length)
			return null;
		
		byte[] rs = new byte[length];
		
		for (int i = 0; i < length; i++) {
			rs[i] = data[off + i];
		}
		
		return HexUtil.byteArr2HexStr(rs);
	}

//	public BerTag getCurBerTag() {
//		return curBerTag;
//	}

//	public void setCurBerTag(BerTag curBerTag) {
//		this.curBerTag = curBerTag;
//	}
	
	/**
	 * 格式化GP指令响应结果，并输出到shell
	 * @param cmd
	 */
	/*
	protected void formatDisplay1(ScriptCommand cmd) {
		String inputCmd = cmd.getInput();
		if (StringUtil.isEmpty(inputCmd))
			return;
		
		if (cmd.getData() instanceof Apdu) {
			Apdu apdu = (Apdu) cmd.getData();
			//byte[] rs = apdu.getSw1Sw2();
			
			if (inputCmd.startsWith(Const.GP_CMD_INIT_UPDATE)) {
				if (apdu.getSw1Sw2() != null && apdu.getSw1Sw2()[0] == (byte)0x90) {
					processInitUpdateRs(apdu);
				}
			} else if (inputCmd.startsWith(Const.GP_CMD_CARD_INFO)) {
				processCardInfoRs(cmd);
			}
			
		}
	}
	*/
	private void processCardInfoRs(ScriptCommand cmd) {
		Apdu apdu = (Apdu) cmd.getData();
		
		if (apdu.getSw1Sw2() != null && apdu.getSw1Sw2()[0] == (byte)0x90) {
			
		} else {
			
		}
	}
	/*
	private void processInitUpdateRs(Apdu apdu) {
		byte[] dataout = apdu.getDataOut();
		if (dataout == null || dataout.length != 28)
			return ;
		
		keyVersionNo = dataout[10];
		channelProtocol = dataout[11];
		seqCounter[0] = dataout[12];
		seqCounter[1] = dataout[13];
		
		System.arraycopy(dataout, 14 - 2, cardChallenge, 0, 8);
		System.arraycopy(dataout, 20, cardCryptogram, 0, 8);
		
		byte[] sessionkey = genSessionKey(Const.GP_SCP_ENC_CONSTANT, seqCounter, encKey);
		
		//验证卡密码
		byte[] cardCrypData = new byte[24];
		System.arraycopy(hostChallenge, 0, cardCrypData, 0, 8);
		System.arraycopy(cardChallenge, 0, cardCrypData, 8, 8);
		byte[] padding = {(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		System.arraycopy(padding, 0, cardCrypData, 16, 8);
		byte[] genCardCryp = Cryto.tripleDes(sessionkey, cardCrypData);
		byte[] tmpGenCardCryp = new byte[8];
		System.arraycopy(genCardCryp, genCardCryp.length - 8, tmpGenCardCryp, 0, 8);
		System.out.println("生成：" + HexUtil.byteArr2HexStr(genCardCryp) + "   " + HexUtil.byteArr2HexStr(cardCryptogram));
		
		if (compareByte(tmpGenCardCryp, cardCryptogram)) { //密码验证通过
			
			//生成主机密码
			byte[] hostCrypData = new byte[24];
			System.arraycopy(cardChallenge, 0, hostCrypData, 0, 8);
			System.arraycopy(hostChallenge, 0, hostCrypData, 8, 8);
			System.arraycopy(padding, 0, hostCrypData, 16, 8);
			byte[] genHostCryp = Cryto.tripleDes(sessionkey, hostCrypData);
			System.arraycopy(genHostCryp, genHostCryp.length - 8, hostCryptogram, 0, 8);

			//生成c-mac
			byte[] mackey = genSessionKey(Const.GP_SCP_C_MAC_CONSTANT, seqCounter, macKey);
			byte[] cmacData = new byte[13];
			cmacData[0] = (byte)0x84;
			cmacData[1] = (byte)0x82;
			cmacData[2] = (byte)0x00;
			cmacData[3] = (byte)0x00;
			cmacData[4] = (byte)0x10;
			System.arraycopy(hostCryptogram, 0, cmacData, 5, 8);
			
			byte[] cmac = Cryto.fullTripleDes(mackey, Cryto.padding(cmacData));
			byte[] dataIn = new byte[16];
			System.arraycopy(hostCryptogram, 0, dataIn, 0, 8);
			System.arraycopy(cmac, 0, dataIn, 8, 8);
			
			Apdu authApdu = new Apdu();
			authApdu.command[Apdu.CLA] = (byte)0x84;
			authApdu.command[Apdu.INS] = (byte)0x82;
			authApdu.command[Apdu.P1] = (byte)0x00;
			authApdu.command[Apdu.P2] = (byte)0x00;
			authApdu.command[Apdu.P3] = (byte)0x10;
			authApdu.setDataIn(dataIn);
			
			jcsv.appendLine("cmd> ext-auth plain", JcesShell.CMD_STYLE_CMD);
			ScriptCommand authCmd = new ScriptCommand(ScriptCommand.APDU, authApdu);
			try {
				process(authCmd);
			} catch (CmdProcessException e) {
				e.printStackTrace();
			}
		} else {
			jcsv.appendLine("验证卡片密码失败！", JcesShell.CMD_STYLE_RES);
		}
		
		
	}
	*/
	private byte[] copyByteArray(byte[] in, int begin, int len) {
		if (in != null && in.length > begin + len) {
			byte[] rs = new byte[len];
			for (int i = 0; i < len; i++) {
				rs[i] = in[begin + i];
			}
		}
		return null;
	}
	
	/**
	 * 生成会话key
	 * @return
	 */
	/*
	private byte[] genSessionKey(byte[] seqCounter, IKey s_encKey) {
		if (seqCounter == null || seqCounter.length != 2 || encKey == null)
			return null;
		
		byte[] data = {Const.GP_SCP_ENC_CONSTANT[0], Const.GP_SCP_ENC_CONSTANT[1],
				seqCounter[0], seqCounter[1],
				0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] key = s_encKey.getKeymat();
		
		//3des cbc 加密
		return Cryto.tripleDes(key, data);
	}
	
	private byte[] genMacKey(byte[] seqCounter, IKey s_macKey) {
		if (seqCounter == null || seqCounter.length != 2 || encKey == null)
			return null;
		
		byte[] data = {Const.GP_SCP_C_MAC_CONSTANT[0], Const.GP_SCP_C_MAC_CONSTANT[1],
				seqCounter[0], seqCounter[1],
				0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] key = s_macKey.getKeymat();
		
		//3des cbc 加密
		return Cryto.tripleDes(key, data);
	}
	
	
	private byte[] genSessionKey(byte[] constant, byte[] seqCounter, IKey key) {
		if (constant == null || constant.length != 2 || 
				seqCounter == null || seqCounter.length != 2 || encKey == null) 
			return null;
		
		byte[] data = new byte[16];
		System.arraycopy(constant, 0, data, 0, 2);
		System.arraycopy(seqCounter, 0, data, 2, 2);
		
		return Cryto.tripleDes(key.getKeymat(), data);
	}
	*/
	
	public abstract ScriptCommand atrCmd() ;
	
	private boolean compareByte(byte[] b1, byte[] b2) {
		if (b1 == null || b2 == null || b1.length != b2.length)
			return false;
		
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}
	
	public void reset() {
		/*
		cardChallenge = new byte[8];
		hostCryptogram = new byte[8];
		cardCryptogram = new byte[8];
		
		channelProtocol = 0;
		keyVersionNo = 0;
		seqCounter = new byte[2];

		tmpChannelProtocol = 0;
		tmpkeyVersionNo = 0;
		tmpSeqCounter = new byte[2];
		
		cmacSessionKey = null;
		rmacSessionKey = null;
		encSessionKey = null;
		dekSessionKey = null;
		icv = null;
		
		//keysetMap.clear();
		securityLevel = 0;
		//initState = false;
		 */
		
		setLogicChannel(0);
		this.scp = null;
		isGp = false;
		JcesShell.curCad.cardManagerAid = null;
	}
	
	public void out(byte[] data, byte outFlg) {
		if (!JcesShell.traceApdus)
			return ;
		
		if (data == null)
			return ;
		
		out(data, data.length, outFlg);
	}
	
	public void out(byte[] data, int dataLen, byte outFlg) {
		if (!JcesShell.traceApdus)
			return ;
		
		if (data == null)
			return ;
		
		int seg = dataLen / 16;
		byte[] tmpData = null;
		StringBuffer outLine = null;
		for (int i = 0; i <= seg; i++) {
			if (i * 16 >= dataLen)
				break;
			
			outLine = new StringBuffer();
			
			String padding = "     ";
			if (i == 0) {
				if (outFlg == OUT_FLG_RES) {
					padding = "  <= ";
				} else if (outFlg == OUT_FLG_CMD) {
					padding = "  => ";
				} else if (outFlg == OUT_FLG_PLAIN_TEXT) {
					padding = "DEC: ";
				} else {
					padding = "     ";
				}
			} 
			outLine.append(padding);
			
			
			if ((i * 16 + 16) > dataLen) {
				tmpData = new byte[dataLen - (i * 16)];
				System.arraycopy(data, i * 16, tmpData, 0, dataLen - (i * 16));			
				
				outLine.append(HexUtil.byteArr2HexStr(tmpData, " ").toUpperCase());
				//outLine.append(PB.substring(0, (16 -(data.length - (i * 16))) * 3));
				for (int p = 0; p < (16 -(dataLen - (i * 16))); p++) {
					outLine.append("   ");
				}
				//outLine.append("    ");
				outLine.append("\t");
				outLine.append(HexUtil.byteArr2Ascii(tmpData));
			} else {
				tmpData = new byte[16];
				System.arraycopy(data, i * 16, tmpData, 0, 16);
				
				outLine.append(HexUtil.byteArr2HexStr(tmpData, " ").toUpperCase());
				outLine.append("\t");
				outLine.append(HexUtil.byteArr2Ascii(tmpData));
			}
			
			//System.out.println("*****" + HexUtil.byteArr2HexStr(tmpData, " ").toUpperCase() + "***" + HexUtil.byteArr2Ascii(tmpData));
			//jcsv.appendLine("*****" + HexUtil.byteArr2HexStr(tmpData), JcesShell.CMD_STYLE_RES);
			
			
			
			getJcsv().appendLine(outLine.toString() , JcesShell.CMD_STYLE_RES);
			
		}
		
		
	}
	
	public void addJcshPath(String path) {
		if (StringUtil.isEmpty(path))
			return ;
		
		File filePath = new File(path);
		if (filePath.exists() && filePath.isDirectory()) {
			this.jcshPathList.add(filePath);
		}
		
	}
	
	public void addJcshPath(File path) {
		if (path != null && path.exists() && path.isDirectory()) {
			this.jcshPathList.add(path);
		}
	}
	
	public List getJcshPathList() {
		return this.jcshPathList;
	}
	
	public abstract void close();
	
	public void setKey(IKey key) {
		if (key == null) {
			return;
		}
		
		IKeySet keySet = new KeySet(key.getKeyset(), key.getType());
		int index = this.keySetList.indexOf(keySet);
		if (index >= 0) {
			keySet = (IKeySet)this.keySetList.get(index);
		} else {
			this.keySetList.add(keySet);
		}
		
		keySet.addKey(key);
		//this.keysetMap.put(key.getKeyset() + "/" + key.getId() + "/" + key.getType(), key);
	}
	
	public IKey getKey(int keyset, int id, String keyType) {
		IKeySet keySet = new KeySet(keyset, keyType.toUpperCase());
		int index = this.keySetList.indexOf(keySet);
		
		if (index >= 0) {
			keySet = (IKeySet)this.keySetList.get(index);
			
			return keySet.getKey(id);
		}
		
		return null;
		//return (IKey)this.keysetMap.get((keyset & 0xFF) + "/" + id + "/" + keyType);
	}
	
	public List getKeySetList() {
		Collections.sort(keySetList, new Comparator() {
			
			public int compare(Object arg0, Object arg1) {
				KeySet keySet0 = (KeySet)arg0;
				KeySet keySet1 = (KeySet)arg1;
				
				if (keySet0.getKeySet() == keySet1.getKeySet()) {
					return keySet0.getKeyType().hashCode() - keySet1.getKeyType().hashCode();
				}
				return keySet0.getKeySet() - keySet1.getKeySet();
			}
		});
		return keySetList;
	}
	
	/*
	public IKey getKey(String key) {
		return (IKey)this.keysetMap.get(key);
	}
	*/
	public void keyInit() {
		IKey key = new Key(Const.GP_INIT_KEY1);
		setKey(key);
		key = new Key(Const.GP_INIT_KEY2);
		setKey(key);
		key = new Key(Const.GP_INIT_KEY3);
		setKey(key);
		
		key = new Key(Const.GP_SCP03_INIT_KEY1);
		setKey(key);
		key = new Key(Const.GP_SCP03_INIT_KEY2);
		setKey(key);
		key = new Key(Const.GP_SCP03_INIT_KEY3);
		setKey(key);
	}
	
	public JcesShellView getJcsv() {
		
		if (jcsv == null) {
			jcsv = JcesShellView.newInstance();
		}
		
		return jcsv;
	}
}
