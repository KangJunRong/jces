package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.IKey;
import com.ecp.jces.jctool.core.Key;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.CryptoException;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;

public class GpInitUpdateAction extends GpAction {

	private static final String CMD = "init-update [key-set] [static|derive]";
	public void execute(String input) throws CmdProcessException {
		if (getCurCad() == null)
			return;
		
		ISCP scp = null;
		String[] params = input.split("\\s+");
		byte keyVersionNo = 0;
		
		String keyMode = "static";
		int kv = 0;
		if (params.length >= 2) {
			if (params.length > 3) {
				throw new CmdProcessException("jcesshell: init-update: Excess arguments: " + params[3] + " ...");
			}
			
			if (params.length == 3) {
				if (!StringUtil.isNumber(params[1])) {
					throw new CmdProcessException("jcesshell:For input string: \"" + params[1] + "\"" );
				}
				
				kv = Integer.parseInt(params[1]);
				
				String para2 = params[2].trim().toLowerCase();
				if ("static".equals(para2) || "derive".equals(para2)) {
					keyMode = para2;
				} else {
					throw new CmdProcessException(CMD + "\njcesshell: unknown key mode: " + params[2]);
				}
			} else {
				String param1 = params[1].trim().toLowerCase();
				if ("static".equals(param1) || "derive".equals(param1) || StringUtil.isNumber(param1)) {
					if ("static".equals(param1) || "derive".equals(param1)) {
						if ("derive".equals(param1)) {
							keyMode = "derive";
						}
					} else {
						kv = Integer.parseInt(param1);
					}
				} else {
					throw new CmdProcessException(CMD + "\njcesshell: auth: Illegal option:" + params[1]);
				}
			}
		}
		
		if (kv > 0 && kv < 255) {
			//throw new CmdProcessException("jcesshell: Command failed: Illegal key set version (1-255): " + params[1]);
			keyVersionNo = (byte)kv;
		} else {
			keyVersionNo = 0;
		}

		getCurCad().setScp(null);
		
		//算卡随机数
		byte[] hostChallenge = SCP.genHostChallenge();
		
		//send init update
		ScriptCommand cmd = buildCmd("8050" + HexUtil.byteToHex(keyVersionNo) + "0008" + HexUtil.byteArr2HexStr(hostChallenge));
		process(cmd);
		
		if (isNormalStatus(cmd.getStatus())) {
			byte[] dataout = cmd.getResp();
			if (dataout == null || dataout.length < 28) {
				throw new CmdProcessException("jcesshell: Wrong response APDU");
			}
			
			switch (dataout[ISCP.SCP_INIT_SCPID_OFFSET]) {
			case ISCP.SCP_ID_01:
				scp = new SCP01();
				break;
			case ISCP.SCP_ID_02:
				scp = new SCP02();
				break;
			case ISCP.SCP_ID_03:
				scp = new SCP03();
				break;
			default:
				throw new CmdProcessException("jcesshell: init-update: Secure Channel Protocol identifier failed");
			}
			
			//校验 key version no
			keyVersionNo = dataout[ISCP.SCP_INIT_KEY_VERSION_OFFSET];
			byte scpId = dataout[ISCP.SCP_INIT_SCPID_OFFSET];
			String  keyType = null;
			switch (scpId) {
			case ISCP.SCP_ID_01:
			case ISCP.SCP_ID_02:
				keyType = ISCP.KEY_TYPE_DES_ECB;
				break;
			case ISCP.SCP_ID_03:
				keyType = ISCP.KEY_TYPE_AES;
				break;
			default:
				throw new CmdProcessException("jcesshell: Command failed: unknown Secure Channel Protocol identifier: " + (scpId & 0xFF));
			}
			
			IKey encKey = null;
			IKey macKey = null;
			IKey dekKey = null;
			if ("derive".equals(keyMode)) {
				IKey baseKey = (IKey)getCurCad().getKey(keyVersionNo , 1 , keyType);
				
				if (baseKey == null) {
					switch (scpId) {
					case ISCP.SCP_ID_01:
					case ISCP.SCP_ID_02:
						baseKey = new Key(Const.GP_INIT_KEY1);
						break;
					case ISCP.SCP_ID_03:
						baseKey = new Key(Const.GP_SCP03_INIT_KEY1);
						break;

					default:
						break;
					}
					
				}
				
				try {
					byte[] dkBytes = new byte[16];
					System.arraycopy(dataout, 4, dkBytes, 0, 6);
					dkBytes[6] = (byte)0xF0;
					dkBytes[7] = (byte)0x01;
					System.arraycopy(dataout, 4, dkBytes, 8, 6);
					dkBytes[14] = (byte)0x0F;
					dkBytes[15] = (byte)0x01;
					encKey = new Key(keyVersionNo, 1, keyType, scp.deriveKey(baseKey.getKeymat(), dkBytes));
					
					System.arraycopy(dataout, 4, dkBytes, 0, 6);
					dkBytes[6] = (byte)0xF0;
					dkBytes[7] = (byte)0x02;
					System.arraycopy(dataout, 4, dkBytes, 8, 6);
					dkBytes[14] = (byte)0x0F;
					dkBytes[15] = (byte)0x02;
					macKey = new Key(keyVersionNo, 2, keyType, scp.deriveKey(baseKey.getKeymat(), dkBytes));
					
					System.arraycopy(dataout, 4, dkBytes, 0, 6);
					dkBytes[6] = (byte)0xF0;
					dkBytes[7] = (byte)0x03;
					System.arraycopy(dataout, 4, dkBytes, 8, 6);
					dkBytes[14] = (byte)0x0F;
					dkBytes[15] = (byte)0x03;
					dekKey = new Key(keyVersionNo, 3, keyType, scp.deriveKey(baseKey.getKeymat(), dkBytes));
				} catch (CryptoException ex) {
					throw new CmdProcessException("jcesshell: Command failed: " + ex.getMessage());
				}
				
				if (baseKey == null) {
					throw new CmdProcessException("jcesshell: Command failed: No such key: " + (keyVersionNo & 0xFF) + "/" + 1);
				}
			} else {
				encKey = (IKey)getCurCad().getKey(keyVersionNo , 1 , keyType);
				macKey = (IKey)getCurCad().getKey(keyVersionNo , 2 , keyType);
				dekKey = (IKey)getCurCad().getKey(keyVersionNo , 3 , keyType);
			}
			
			
			if (encKey == null || macKey == null || dekKey == null) { //没有找到key 使用默认的
				switch (scpId) {
				case ISCP.SCP_ID_01:
				case ISCP.SCP_ID_02:
					encKey = new Key(Const.GP_INIT_KEY1);
					macKey = new Key(Const.GP_INIT_KEY2);
					dekKey = new Key(Const.GP_INIT_KEY3);
					break;
				case ISCP.SCP_ID_03:
					encKey = new Key(Const.GP_SCP03_INIT_KEY1);
					macKey = new Key(Const.GP_SCP03_INIT_KEY2);
					dekKey = new Key(Const.GP_SCP03_INIT_KEY3);
					break;
				}
			}
			
			if (encKey == null) {
				throw new CmdProcessException("jcesshell: Command failed: No such key: " + (keyVersionNo & 0xFF) + "/" + 1);
			}
			
			if (macKey == null) {
				throw new CmdProcessException("jcesshell: Command failed: No such key: " + (keyVersionNo & 0xFF) + "/" + 2);
			}
			
			if (dekKey == null) {
				throw new CmdProcessException("jcesshell: Command failed: No such key: " + (keyVersionNo & 0xFF) + "/" + 3);
			}

			scp.init(encKey, macKey, dekKey, dataout, hostChallenge);
			getCurCad().setScp(scp);

			try {
				scp.genSessionKey();
				if (!scp.veriCardCrypto()) {
					getCurCad().setScp(null);
					throw new CmdProcessException("jcesshell: init-update: Authentication of card cryptogram failed");
				}
			} catch (CryptoException e) {
				throw new CmdProcessException("jcesshell: " + e.getMessage());
			}

		} else {
			throw new CmdProcessException("jcesshell:  Wrong response APDU: " +  cmd.getStatusCode());
		}
	}

	public String getAction() {
		return Const.GP_CMD_INIT_UPDATE;
	}

	public void usage() {
		outUsage("init-update [key-set] [static|derive]");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.eastcompeace.jces.eclipse.internal.ui.views.jcesshell.action.Action#help()
	 */
	public void help() {
/*
usage: init-update [key-set [scp]] 
   key-set  Key set version to be used. Zero is default and means
            that the card dictates, which key set to be used.
       scp  Global Platform 2.1.1 Secure Channel Protocol (SCP) to be used.
            Valid values are: SCP_UNDEFINED, SCP_01_05, SCP_01_15, SCP_02_04,
            SCP_02_05, SCP_02_0A, SCP_02_0B, SCP_02_14, SCP_02_15, SCP_02_1A,
            SCP_02_1B.
            SCP_UNDEFINED is the default value and means that SCP_02_15
            will be used if the card indicates that it supports SCP 02.
            Otherwise SCP_01_05 will be used (that's the Global (Open) Platform
            2.0.1' compatible protocol.

Global Platform INITIALIZE-UPDATE command.
 */
		StringBuffer helpStr = new StringBuffer();
		helpStr.append("init-update [key-set] [static|derive]").append("\n");
		helpStr.append("   key-set  Key set version to be used. Zero is default and means").append("\n");
		helpStr.append("            that the card dictates, which key set to be used.").append("\n").append("\n");
		helpStr.append("   static|derive").append("\n");
		helpStr.append("            Desired key mode for secure channel.").append("\n").append("\n");
		helpStr.append("Global Platform INITIALIZE-UPDATE command.");
		outUsage(helpStr.toString());  
	}

}
