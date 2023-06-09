package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.GPUtil;
import com.ecp.jces.jctool.util.GpLifeCycleUtil;
import com.ecp.jces.jctool.util.HexUtil;

public class GpCardInfoAction extends GpAction {
	
	public static final String LIFE_CYCLE_PADDING = "             ";
	public static final String PRIVILEGE_PADDING  = "          ";
	public String getAction() {
		return Const.GP_CMD_CARD_INFO;
	}
	
	private byte[] getStatusInfo(byte[] apdu) throws CmdProcessException {
		int macLen = 0;
		ISCP scp = getCurCad().getScp();
		if (scp != null && ((scp.getSecurityLevel() & ISCP.SCP_SL_RMAC) == ISCP.SCP_SL_RMAC)) {
			if (((scp.getSecurityLevel() & ISCP.SCP_SL_CRMAC_ENC_DEC) == ISCP.SCP_SL_CRMAC_ENC_DEC)) {
				macLen = 0;
			} else {
				macLen = 8;
			}
		}
		
		byte[] resp = null;
		ScriptCommand cmd = process(HexUtil.byteArr2HexStr(apdu));
		
		if (cmd.getStatus() ==  Const.GP_STATUS_GET_NEXT) {
			resp = cmd.getResp();
			
			if (resp.length < 8) {
				throw new CmdProcessException("响应数据格式不正确.");
			}
			byte[] preDataBytes = new byte[resp.length - macLen];
			System.arraycopy(resp, 0, preDataBytes, 0, resp.length - macLen);
			
			/*
			if (preDataBytes == null) {
				throw new CmdProcessException("没有状态信息.");
			}
			*/
			
			byte[] newDataBytes = null;
			apdu[APDU.p2] = (byte)0x01;
			
			while (cmd.getStatus() ==  Const.GP_STATUS_GET_NEXT) {
				cmd = process(HexUtil.byteArr2HexStr(apdu));
				resp = cmd.getResp();
				if (resp == null) {
					throw new CmdProcessException("没有状态信息.");
				}
				
				if (resp.length < 8) {
					throw new CmdProcessException("响应数据格式不正确.");
				}
				
				newDataBytes = new byte[preDataBytes.length + cmd.getResp().length - macLen];
				System.arraycopy(preDataBytes,0 , newDataBytes, 0, preDataBytes.length);
				System.arraycopy(cmd.getResp(),0 , newDataBytes, preDataBytes.length, cmd.getResp().length - macLen);
				
				preDataBytes = newDataBytes;
			}
			
			return newDataBytes;
			
		} else {
			byte[] respData = cmd.getResp();
			if (respData != null && respData.length > macLen && macLen > 0) {
				byte[] rs = new byte[respData.length - macLen];
				System.arraycopy(respData, 0, rs, 0, rs.length);
				return rs;
			} else {
				return respData;
			}
		}
	}

	private void analyseISDInfo(byte[] dataBytes) throws CmdProcessException {
		if (dataBytes == null) {
			return;
		}
		
		try {
			int len = dataBytes[0] & (byte)0xFF;
			if (len <= 0) {
				return;
			}
			if ((len + 2) <= dataBytes.length) {
				byte[] cardManagerAids = new byte[len];
				System.arraycopy(dataBytes, 1, cardManagerAids, 0, len);
				
				String cardManagerStatus = GpLifeCycleUtil.getCardStatus(dataBytes[len + 1]);

				getJcsv().appendLine("Card Manager AID   : " + HexUtil.byteArr2HexStr(cardManagerAids) + " (" + HexUtil.byteArr2Ascii(cardManagerAids) + ")", JcesShell.CMD_STYLE_RES);
				getJcsv().appendLine("Card Manager state : " + cardManagerStatus, JcesShell.CMD_STYLE_RES);
			}
		} catch (Exception ex) {
			//throw new CmdProcessException(ex.getMessage());
		}
	}
	
	private void analyseSDInfo(byte[] dataBytes) throws CmdProcessException {
		if (dataBytes == null) {
			return;
		}
		
		try {
			int len = 0;
			int index = 0;
			while (true) {
				if (index >= dataBytes.length)
					break;
					
				len = dataBytes[index] & (byte)0xFF;
				if (len == 0) {
					break;
				}
				
				byte[] appAids = new byte[len];
				index++;
				System.arraycopy(dataBytes, index, appAids, 0, len);
				
				index += len;
				String appStatus = GpLifeCycleUtil.getAppStatus(dataBytes[index]);
				index++;
				String privilege = GPUtil.getApplicationPrivilegeCode(dataBytes[index]);
				index++;
				
				if (appStatus.length() < LIFE_CYCLE_PADDING.length()) {
					int paddingLen = LIFE_CYCLE_PADDING.length() - appStatus.length();
					appStatus = LIFE_CYCLE_PADDING.substring(0,paddingLen) + appStatus;
				}
				
				getJcsv().appendLine("    Application: " + appStatus + " " + privilege + " " + HexUtil.byteArr2HexStr(appAids) + " (" + HexUtil.byteArr2Ascii(appAids) + ")", JcesShell.CMD_STYLE_RES);
			}
		} catch (Exception ex) {
			//throw new CmdProcessException(ex.getMessage());
		}
	}
	
	private void analyseAppInfo(byte[] dataBytes) throws CmdProcessException {
		if (dataBytes == null) {
			return;
		}
		
		try {
			int len = 0;
			int index = 0;
			while (true) {
				if (index >= dataBytes.length)
					break;
				
				len = dataBytes[index] & (byte)0xFF;
				if (len == 0) {
					break;
				}
				//exe load file
				byte[] loadFielAids = new byte[len];
				index++;
				System.arraycopy(dataBytes, index, loadFielAids, 0, len);
				byte status = dataBytes[index + len];
				String loadFielStatus = GpLifeCycleUtil.getExeLoadFileStatus(status);
				if (loadFielStatus.length() < LIFE_CYCLE_PADDING.length()) {
					int paddingLen = LIFE_CYCLE_PADDING.length() - loadFielStatus.length();
					loadFielStatus = LIFE_CYCLE_PADDING.substring(0,paddingLen) + loadFielStatus;
				}
				String privilege = GPUtil.getApplicationPrivilegeCode(dataBytes[index + len + 1]);
				getJcsv().appendLine("    Load File  : " + loadFielStatus + " " + privilege + " " + HexUtil.byteArr2HexStr(loadFielAids) + " (" + HexUtil.byteArr2Ascii(loadFielAids) + ")", JcesShell.CMD_STYLE_RES);
				
				//module
				int moduleSize = dataBytes[index + len + 2] & (byte)0xFF;
				index = index + len + 3;
				if (moduleSize > 0) {
					for (int k = 0; k < moduleSize; k++) {
						len = dataBytes[index]& (byte)0xFF;
						byte[] modules = new byte[len];
						System.arraycopy(dataBytes, index + 1, modules, 0, len);
						
						getJcsv().appendLine("     Module    : " + LIFE_CYCLE_PADDING + " " + PRIVILEGE_PADDING + " " + HexUtil.byteArr2HexStr(modules) + " (" + HexUtil.byteArr2Ascii(modules) + ")", JcesShell.CMD_STYLE_RES);
						index += len + 1;
					}
				}					
			}
			
		} catch (Exception ex) {
			//throw new CmdProcessException(ex.getMessage());
		}
	}
	
	public void execute(String input) throws CmdProcessException {
		//80F28000024F00
		byte[] cmd = new byte[7];
		cmd[APDU.CLS] = (byte)0x80;
		cmd[APDU.INS] = (byte)0xF2;
		cmd[APDU.P1] = (byte)0x80;
		cmd[APDU.p2] = (byte)0x00;
		cmd[APDU.LC] = (byte)0x02;
		cmd[APDU.DATA] = (byte)0x4F;
		
		byte[] isdBytes = getStatusInfo(cmd);
		
		//80F24000024F00
		cmd[APDU.P1] = (byte)0x40;
		cmd[APDU.p2] = (byte)0x00;
		byte[] sdBytes = getStatusInfo(cmd);
		
		//80F21000024F00
		cmd[APDU.P1] = (byte)0x10;
		cmd[APDU.p2] = (byte)0x00;
		byte[] appBytes = getStatusInfo(cmd);
		
		analyseISDInfo(isdBytes);
		analyseSDInfo(sdBytes);
		analyseAppInfo(appBytes);
		
		
		
		
		/*
		//获取 isd info 80F28000024F0000
		//ScriptCommand isdCmd = buildCmd("80F28000024F0000");
		//process(isdCmd);
		ScriptCommand isdCmd = process("80F28000024F00");
		//ScriptCommand isdCmd1 = process("80F28000024F00");
		
		//Applications and Security Domains 80F24000024F0000
		//ScriptCommand sdCmd = buildCmd("80F24000024F0000");
		//process(sdCmd);
		ScriptCommand sdCmd = process("80F24000024F00");
		
		// Executable Load Files and their Executable Modules 80F21000024F0000
		//ScriptCommand appCmd = buildCmd("80F21000024F0000");
		//process(appCmd);
		ScriptCommand appCmd = process("80F21000024F00");
		ScriptCommand appCmd1 = process("80F21001024F00");
		
		ScriptCommand moduCmd = null;
		if (appCmd.getStatus() != Const.GP_STATUS_NO_ERROR) {
			moduCmd = process("80F22000024F00");
		}
		
		jcsv.appendLine("", JcesShell.CMD_STYLE_RES);
		//输出分析结果
		//card
		byte[] isdCmds = isdCmd.getResp();
		if (isdCmds != null && isdCmd.getStatus() == Const.GP_STATUS_NO_ERROR) {
			try {
				int len = isdCmds[0] & (byte)0xFF;
				if ((len + 2) <= isdCmd.getRespLength()) {
					byte[] cardManagerAids = new byte[len];
					System.arraycopy(isdCmds, 1, cardManagerAids, 0, len);
					
					String cardManagerStatus = GpLifeCycleUtil.getCardStatus(isdCmds[len + 1]);

					jcsv.appendLine("Card Manager AID   : " + HexUtil.byteArr2HexStr(cardManagerAids) + " (" + HexUtil.byteArr2Ascii(cardManagerAids) + ")", JcesShell.CMD_STYLE_RES);
					jcsv.appendLine("Card Manager state : " + cardManagerStatus, JcesShell.CMD_STYLE_RES);
				}
			} catch (Exception ex) {
				throw new CmdProcessException(ex.getMessage());
			}
		}
		
		//application
		byte[] sdCmds = sdCmd.getResp();
		if (sdCmds != null && sdCmd.getStatus() == Const.GP_STATUS_NO_ERROR) {
			try {
				int len = 0;
				int index = 0;
				while (true) {
					if (index >= sdCmd.getRespLength())
						break;
						
					len = sdCmds[index] & (byte)0xFF;
					byte[] appAids = new byte[len];
					index++;
					System.arraycopy(sdCmds, index, appAids, 0, len);
					
					index += len;
					String appStatus = GpLifeCycleUtil.getAppStatus(sdCmds[index]);
					index++;
					String privilege = GPUtil.getApplicationPrivilegeCode(sdCmds[index]);
					index++;
					
					if (appStatus.length() < LIFE_CYCLE_PADDING.length()) {
						int paddingLen = LIFE_CYCLE_PADDING.length() - appStatus.length();
						appStatus = LIFE_CYCLE_PADDING.substring(0,paddingLen) + appStatus;
					}
					
					jcsv.appendLine("    Application: " + appStatus + " " + privilege + " " + HexUtil.byteArr2HexStr(appAids) + " (" + HexUtil.byteArr2Ascii(appAids) + ")", JcesShell.CMD_STYLE_RES);
				}
			} catch (Exception ex) {
				throw new CmdProcessException(ex.getMessage());
			}
		}
		
		//exe load file
		if (appCmd != null && appCmd.getResp() != null &&  appCmd.getStatus() == Const.GP_STATUS_NO_ERROR) {
			byte[] appCmds = appCmd.getResp();
			
			try {
				int len = 0;
				int index = 0;
				while (true) {
					if (index >= appCmd.getRespLength())
						break;
					
					len = appCmds[index] & (byte)0xFF;
					
					//exe load file
					byte[] loadFielAids = new byte[len];
					index++;
					System.arraycopy(appCmds, index, loadFielAids, 0, len);
					byte status = appCmds[index + len];
					String loadFielStatus = GpLifeCycleUtil.getExeLoadFileStatus(status);
					if (loadFielStatus.length() < LIFE_CYCLE_PADDING.length()) {
						int paddingLen = LIFE_CYCLE_PADDING.length() - loadFielStatus.length();
						loadFielStatus = LIFE_CYCLE_PADDING.substring(0,paddingLen) + loadFielStatus;
					}
					String privilege = GPUtil.getApplicationPrivilegeCode(appCmds[index + len + 1]);
					jcsv.appendLine("    Load File  : " + loadFielStatus + " " + privilege + " " + HexUtil.byteArr2HexStr(loadFielAids) + " (" + HexUtil.byteArr2Ascii(loadFielAids) + ")", JcesShell.CMD_STYLE_RES);
					
					//module
					int moduleSize = appCmds[index + len + 2] & (byte)0xFF;
					index = index + len + 3;
					if (moduleSize > 0) {
						for (int k = 0; k < moduleSize; k++) {
							len = appCmds[index]& (byte)0xFF;
							byte[] modules = new byte[len];
							System.arraycopy(appCmds, index + 1, modules, 0, len);
							
							jcsv.appendLine("     Module    : " + LIFE_CYCLE_PADDING + " " + PRIVILEGE_PADDING + " " + HexUtil.byteArr2HexStr(modules) + " (" + HexUtil.byteArr2Ascii(modules) + ")", JcesShell.CMD_STYLE_RES);
							index += len + 1;
						}
					}					
				}
				
			} catch (Exception ex) {
				throw new CmdProcessException(ex.getMessage());
			}
		}
			
		//module file
		if (moduCmd != null && moduCmd.getResp() != null &&  moduCmd.getStatus() == Const.GP_STATUS_NO_ERROR) {
			byte[] moduCmds = moduCmd.getResp();
			
			try {
				int len = 0;
				int index = 0;
				while (true) {
					if (index >= moduCmd.getRespLength())
						break;
					
					len = moduCmds[index] & (byte)0xFF;
					
					//exe module file
					byte[] moduleFielAids = new byte[len];
					index++;
					System.arraycopy(moduCmds, index, moduleFielAids, 0, len);
					byte status = moduCmds[index + len];
					String moduleFielStatus = GpLifeCycleUtil.getExeLoadFileStatus(status);
					if (moduleFielStatus.length() < LIFE_CYCLE_PADDING.length()) {
						int paddingLen = LIFE_CYCLE_PADDING.length() - moduleFielStatus.length();
						moduleFielStatus = LIFE_CYCLE_PADDING.substring(0,paddingLen) + moduleFielStatus;
					}
					String privilege = GPUtil.getApplicationPrivilegeCode(moduCmds[index + len + 1]);
					index = index + len + 2;
					jcsv.appendLine("    Load File  : " + moduleFielStatus + " " + privilege + " " + HexUtil.byteArr2HexStr(moduleFielAids) + " (" + HexUtil.byteArr2Ascii(moduleFielAids) + ")", JcesShell.CMD_STYLE_RES);			
				}
				
			} catch (Exception ex) {
				throw new CmdProcessException(ex.getMessage());
			}
		}
		*/
	}
	
	protected void fillStatus() {
		processStatus.put(STATUS_PREFIX + "6A88", "Referenced data not found");
		processStatus.put(STATUS_PREFIX + "6A80", "Incorrect values in command data");
	}

	public void usage() {
		outUsage(Const.GP_CMD_CARD_INFO);
	}
	
	public void help() {
		outUsage(Const.GP_CMD_CARD_INFO + 
				"Displays card information (Card Manager state/AID and registry info).\n" +
				"The privileges are coded as follows:\n" +
				"  S - Security Domain\n" +
				"  V - Security Domain with DAP verification\n" +
				"  E - Security Domain with delegated management\n" +
				"  L - Card Manager lock privilege\n" +
				"  T - Card Manager terminate privilege\n" +
				"  D - Implicit selectable (default applet)\n" +
				"  P - PIN change privilege\n" +
				"  M - Security Domain with mandated DAP verification\n");
	}
	
}
