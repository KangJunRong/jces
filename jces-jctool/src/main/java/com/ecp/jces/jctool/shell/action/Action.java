package com.ecp.jces.jctool.shell.action;

import java.util.List;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.AID;
import com.ecp.jces.jctool.shell.*;
import com.ecp.jces.jctool.util.CryptoException;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;
import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apdutool.Msg;

public abstract class Action implements IAction {
	
	public JcesShellView jcsv;
	
	public JcesShellView getJcsv() {
		if (jcsv == null) {
			jcsv = JcesShellView.newInstance();
		}
		
		return jcsv;
	}
	
	public void init() {
		fillStatus();
	}
	public abstract void execute(String input) throws CmdProcessException;

	protected Cad getCurCad() throws CmdProcessException {
		Cad curCad = JcesShell.curCad;
		if (curCad == null) {
			throw new CmdProcessException("jcesshell: Not connected to a terminal");
		} 
			
		return curCad;

	}
	
	protected ScriptCommand process(byte[] cmdBytes) throws CmdProcessException {
		//签名、加密指令
		ISCP scp = getCurCad().getScp();
		byte securityLevel = 0;
		if (scp != null) {
			securityLevel = (byte)scp.getSecurityLevel();
		}
		
		if ((securityLevel > 0) && (cmdBytes[Apdu.CLA] == (byte)0x80) && (cmdBytes[Apdu.INS] != (byte)0xA4)) {
			//验证命令格式
			if (cmdBytes.length > Apdu.P3) {
				int tmpLen = cmdBytes[Apdu.P3] & 0xFF;
				System.out.println("cmd Len: " + cmdBytes[Apdu.P3] + "  " + tmpLen + "   size: " + cmdBytes.length);
				if (tmpLen + 5 > cmdBytes.length)
					throw new CmdProcessException("jcesshell: Wrong length in Lc.");
				
				//重新组装命令,消除没用的数据
				byte[] tmpCmdBytes = new byte[tmpLen + 5];
				System.arraycopy(cmdBytes, 0, tmpCmdBytes, 0, tmpLen + 5);
				cmdBytes = tmpCmdBytes;
			} else {
				byte[] tmpCmdBytes = new byte[5];
				System.arraycopy(cmdBytes, 0, tmpCmdBytes, 0, cmdBytes.length);
				cmdBytes = tmpCmdBytes;
			}
			
			try {
				if ((securityLevel & ISCP.SCP_SL_ENC) == ISCP.SCP_SL_ENC) {
					cmdBytes = scp.cmdEncryption(cmdBytes);
				} else if ((securityLevel & ISCP.SCP_SL_CMAC) == ISCP.SCP_SL_CMAC) {
					//cmdBytes = SCPCryto.scp2mac(cmdBytes, getCurCad().cmacSessionKey, getCurCad().icv);
					cmdBytes = scp.genCMAC(cmdBytes);
				}
			} catch (CryptoException ex) {
				throw new CmdProcessException(ex.getMessage());
			} catch (Exception ex) {
				throw new CmdProcessException(ex.getMessage());
			}
		}
		
		try {
			Apdu apdu = new Apdu();
			apdu.command[Apdu.CLA] = cmdBytes[Apdu.CLA];
			apdu.command[Apdu.INS] = cmdBytes[Apdu.INS];
			apdu.command[Apdu.P1] = cmdBytes[Apdu.P1];
			apdu.command[Apdu.P2] = cmdBytes[Apdu.P2];

			if (cmdBytes.length == 4) { //case 1
				apdu.command[Apdu.P3] = 0;
				apdu.setDataIn(null);
			} else {
				apdu.command[Apdu.P3] = cmdBytes[Apdu.P3];
				
				byte[] datain = getDataIn(cmdBytes);
				if (datain == null) { //case 2
					//apdu.setDataIn(null,(cmdBytes[Apdu.P3] & 0xff));
					apdu.Le = cmdBytes[Apdu.P3] & 0xff;
					apdu.setDataIn(null);
					//apdu.setDataIn(new byte[cmdBytes[Apdu.P3] & 0xff], cmdBytes[Apdu.P3] & 0xff);
				} else { //case 3
					if (cmdBytes[Apdu.P3] == (cmdBytes.length - 6)) { //case 4
						apdu.Le = cmdBytes[cmdBytes[Apdu.P3] + 5] & 0xff;
					}
					apdu.Lc = cmdBytes[Apdu.P3] & 0xff;
					apdu.setDataIn(getDataIn(cmdBytes));
				}
			}
			
			//增加逻辑通道
			byte cla = apdu.command[Apdu.CLA];
			if (((cla & 0xFF & (byte)0x03) == 0) && (Cad.getLogicChannel() != 0)) {
				apdu.command[Apdu.CLA] = (byte)(apdu.command[Apdu.CLA] | (byte)Cad.getLogicChannel());
			}
			ScriptCommand cmd = new ScriptCommand(ScriptCommand.APDU,apdu);
			process(cmd);
			return cmd;
		} catch (CmdProcessException ex) {
			throw ex;
		}	
	}
	
	protected ScriptCommand process(String cmd) throws CmdProcessException {
		try {
			if (!StringUtil.isHexString(cmd)) {
				throw new CmdProcessException("jcesshell: Apdu Illegal hex digits.");
			}
			return process(HexUtil.hexStr2ByteArr(cmd));
		} catch (CmdProcessException ex) {
			throw ex;
		} catch (Exception e) {
			throw new CmdProcessException(e.getLocalizedMessage());
		}
	}
	
	private void veriRMAC(ScriptCommand cmd) throws CmdProcessException {
		Object dataObject = cmd.getData();
		if (dataObject instanceof Apdu) {
			Apdu apdu = (Apdu)dataObject;
			
			byte[] cmdData = apdu.getCommand();
			if (cmdData[APDU.INS] == APDU.INS_INIT_UPDATE || cmdData[APDU.INS] == APDU.INS_EXT_AUTH) {
				return ;
			}
			
			byte[] dataOut = apdu.getDataOut();
			if (dataOut != null && dataOut.length > 8) {
				ISCP scp = getCurCad().getScp();
				if (scp != null && ((scp.getSecurityLevel() & ISCP.SCP_SL_RMAC) == ISCP.SCP_SL_RMAC)) {
					try {
						byte[] srcData = null;
						if (scp instanceof SCP02) {
							srcData = new byte[apdu.getCommand().length + apdu.getDataIn().length + apdu.dataOut.length - 5];
							System.arraycopy(apdu.getCommand(), 0, srcData, 0, apdu.getCommand().length); //lc
							System.arraycopy(apdu.getDataIn(), 0, srcData, apdu.getCommand().length, apdu.getDataIn().length); //datain
							srcData[apdu.getCommand().length + apdu.getDataIn().length] = (byte)(apdu.dataOut.length - 8);//le
							System.arraycopy(apdu.dataOut, 0, srcData, apdu.getCommand().length + apdu.getDataIn().length + 1, apdu.dataOut.length - 8);
							System.arraycopy(apdu.getSw1Sw2(), 0, srcData, srcData.length - 2, 2);
						} else if (scp instanceof SCP03) {
							srcData = new byte[apdu.dataOut.length + 2];
							System.arraycopy(apdu.dataOut, 0, srcData, 0, apdu.dataOut.length);
							System.arraycopy(apdu.sw1sw2, 0, srcData, apdu.dataOut.length, 2);
						} else {
							return;
						}
						
						byte[] mac = new byte[8];
						System.arraycopy(dataOut, dataOut.length - 8, mac, 0, 8);
						if (!scp.veriRMAC(srcData, mac)) {
							throw new CmdProcessException("jcesshell: RMAC Verification failed");
						}
						
						//
						if ((scp.getSecurityLevel() & ISCP.SCP_SL_CRMAC_ENC_DEC) == ISCP.SCP_SL_CRMAC_ENC_DEC) {
							byte[] desData = scp.resDecryption(srcData);
							getCurCad().out(desData, Cad.OUT_FLG_PLAIN_TEXT);
							apdu.setDataOut(desData);
						}
					} catch (CryptoException e) {
						throw new CmdProcessException("jcesshell: " + e.getMessage());
					}
				}
			}
		}
		
	}
	protected void process(ScriptCommand cmd) throws CmdProcessException {
		if (cmd != null) {
			try {
				//打印输入命令
				getCurCad().process(cmd);

				//打印运行结果
				
				//显示运行状态
				veriRMAC(cmd);			
				processState(cmd.getStatus());
				
			} catch (CmdProcessException ex) {
				throw ex;
			}
		}
	}
	
	protected void fillStatus() {
		processStatus.put(STATUS_PREFIX + "6400", "No specific diagnosis");
		processStatus.put(STATUS_PREFIX + "6700", "Wrong length in Lc");
		processStatus.put(STATUS_PREFIX + "6881", "Logical channel not supported or is not active");
		processStatus.put(STATUS_PREFIX + "6982", "Security status not satisfied");
		processStatus.put(STATUS_PREFIX + "6985", "Conditions of use not satisfied");
		processStatus.put(STATUS_PREFIX + "6A86", "Conditions of use not satisfied");
		processStatus.put(STATUS_PREFIX + "6D00", "Invalid instruction");
		processStatus.put(STATUS_PREFIX + "6E00", "Invalid class ");
		
		
		//PUT KEY Error Conditions
		processStatus.put(STATUS_PREFIX + "6581", "Memory failure ");
		processStatus.put(STATUS_PREFIX + "6A84", "Not enough memory space");
		processStatus.put(STATUS_PREFIX + "6A88", "Referenced data not found ");
		processStatus.put(STATUS_PREFIX + "9484", "Algorithm not supported");
		processStatus.put(STATUS_PREFIX + "9485", "Invalid key check value ");
		
	}
	
	protected void processState(int state) {
		if (state == Const.GP_STATUS_GET_NEXT || isNormalStatus(state) || (state >= 37120 && state < 37376)) {
			getJcsv().appendLine("Status: No Error", JcesShell.CMD_STYLE_RES);
		} else {
			String processState = (String)processStatus.get(STATUS_PREFIX + toHex(state));
			if (!StringUtil.isEmpty(processState)) {
				getJcsv().appendLine("Status: " + processState, JcesShell.CMD_STYLE_ERROR);
			}
			
			if (state != 0) {
				outError("jcesshell: Error code: " +  Integer.toHexString(state).toUpperCase());
				//throw new CmdProcessException("jcesshell: Error code: " +  Integer.toHexString(state).toUpperCase());
			}
		}
	}
	
	private String toHex(int src) {
		return Integer.toHexString(src).toUpperCase();
	}
	
	protected ScriptCommand buildCmd(String apduInput) throws CmdProcessException {
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
					return new ScriptCommand(ScriptCommand.APDU,apdu);
				} else {
					throw new CmdProcessException(Msg.getMessage("parser.4"));
				}
			} else {
				throw new CmdProcessException("jcesshell: analyse command exception.");
			}
		} catch (CmdProcessException ex) {
			throw ex;
		}	
	}
	
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
				throw new CmdProcessException("Wrong length in Lc.");
			}
		}
		return null;
	}
	
	protected byte[] hexStringToBytes(String hex) {
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
	
	protected void outError(String error) {
		getJcsv().appendLine(error, JcesShell.CMD_STYLE_ERROR);
	}
	
	protected void outResp(String resp) {
		getJcsv().appendLine(resp, JcesShell.CMD_STYLE_RES);
	}
	
	protected void outCmd(String cmd) {
		getJcsv().appendLine(cmd, JcesShell.CMD_STYLE_CMD);
	}
	
	protected void outUsage(String out) {
		getJcsv().appendLine("Usage: " + out, JcesShell.CMD_STYLE_RES);
	}
	
	protected AID getCardManagerAid() {
		AID aid = null;
//		try {
//			if (getCurCad().cardManagerAid != null) {
//				aid =  getCurCad().cardManagerAid;
//			}
//
//			if (aid == null && getJcsv() != null && getJcsv().getCurrentConfig() != null) {
//				aid  = LauncherUtil.getCardManagerAid(getJcsv().getCurrentConfig());
//			}
//		} catch (CmdProcessException ex) {
//
//		}
		
		if (aid != null) {
			return aid;
		} else {
			return new AID(Const.CARD_MANAGER_DEFAULT_AID);
		}
	}
	
	private boolean isCmac(byte securityLevel) {
		return isTrue(securityLevel, 0);
	}
	
	private boolean isEnc(byte securityLevel) {
		return isTrue(securityLevel, 1);
	}
	
	private boolean isRmac(byte securityLevel) {
		return isTrue(securityLevel, 4);
	}
	
	private boolean isTrue(byte securityLevel, int bitIndex) {
		if (bitIndex > 8) 
			return false;
		
		byte bit = (byte)0x01;
		bit = (byte)(bit << bitIndex);
		
		if ((securityLevel & bit) > 0) {
			return true;
		}
		return false;
	}
	
	public void help() {
		usage();
	}
	
	protected boolean isNormalStatus(int status) {
		List statusList = JcesShell.getNormalStatusList();
		
		if (status == Const.GP_STATUS_NO_ERROR || (status >= 0x6100 && status <= 0x61FF)) {
			return true;
		}
		
		StatusBytes sbyte = null;
		for (int i = 0; i < statusList.size(); i++) {
			sbyte = (StatusBytes)statusList.get(i);
			
			if (status >= sbyte.getMinCode() && status <= sbyte.getMaxCode()) {
				return true;
			}
		}
		return false;
	}
}
