package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.CryptoException;
import com.ecp.jces.jctool.util.StringUtil;

import java.awt.color.CMMException;

public class GpExtAuthAction extends GpAction {

	private static final String CMD = "ext-auth [plain|mac|enc|rmac|crmac|crmacenc|crmacencdec]";
	public void execute(String input) throws CmdProcessException {
		ISCP scp = getCurCad().getScp();
		if (scp == null) {
			throw new CmdProcessException("jcesshell: Command failed: No previous initialize update.");
		}
		
		String[] params = input.split("\\s+");

		byte securityLevel = 0;
		if (params.length > 1) {
			if (params.length > 2)
				throw new CmdProcessException(CMD + "\njcesshell: ext-auth : Excess arguments: " + params[2]);
			
			String param1 = params[1].trim().toLowerCase();
			if ("mac".equals(param1) 
					|| "enc".equals(param1)
					|| "rmac".equals(param1)
					|| "crmac".equals(param1)
					|| "crmacenc".equals(param1)
					|| "crmacencdec".equals(param1)
					|| "plain".equals(param1)) {
				
				if ("enc".equals(param1)) {
					securityLevel = ISCP.SCP_SL_ENC;
				} else if ("mac".equals(param1)) {
					securityLevel = ISCP.SCP_SL_CMAC;
				} else if ("rmac".equals(param1)) {
					securityLevel = ISCP.SCP_SL_RMAC;
				} else if ("crmac".equals(param1)) {
					securityLevel = ISCP.SCP_SL_CRMAC;
				} else if ("crmacenc".equals(param1)) {
					securityLevel = ISCP.SCP_SL_CRMAC_ENC;
				} else if ("crmacencdec".equals(param1)) {
					securityLevel = ISCP.SCP_SL_CRMAC_ENC_DEC;
				} else {
					securityLevel = 0;
				}
			} else {
				throw new CmdProcessException("ext-auth [plain|mac|enc]\njcesshell: unknown security level: " + params[1]);
			}
		} 
		
		//生成主机密码
		/*
		byte[] sessionkey = genSessionKey(Const.GP_SCP_ENC_CONSTANT, getCurCad().tmpSeqCounter, getCurCad().encKey);
		
		byte[] padding = {(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		byte[] hostCrypData = new byte[24];
		System.arraycopy(getCurCad().cardChallenge, 0, hostCrypData, 0, 8);
		System.arraycopy(getCurCad().hostChallenge, 0, hostCrypData, 8, 8);
		System.arraycopy(padding, 0, hostCrypData, 16, 8);
		byte[] genHostCryp = Cryto.tripleDes(sessionkey, hostCrypData);
		System.arraycopy(genHostCryp, genHostCryp.length - 8, getCurCad().hostCryptogram, 0, 8);
		*/
		//生成c-mac
		try {
			byte[] hostCrypto = scp.genHostCrypto();
			
			byte[] cmacData = new byte[13];
			cmacData[0] = (byte)0x80;
			cmacData[1] = (byte)0x82;
			cmacData[2] = securityLevel;
			cmacData[3] = (byte)0x00;
			cmacData[4] = (byte)0x08;
			System.arraycopy(hostCrypto, 0, cmacData, 5, 8);
			
			System.out.println("gen cmac");
			byte[] apdu = scp.genCMAC(cmacData);
			ScriptCommand authCmd = buildCmd(StringUtil.dumpBytes(apdu));
			process(authCmd);
			
			if (isNormalStatus(authCmd.getStatus())) { //签权成功，初始化card信息
				scp.setSecurityLevel(securityLevel);
				if (scp instanceof SCP03) {
					SCP03 scp03 = (SCP03) scp;
					scp03.resetIcvCounter();
				}
			}
		} catch (CryptoException e) {
			throw new CMMException(e.getMessage());
		}

		/*
		byte[] cmac = Cryto.fullTripleDes(mackey, Cryto.padding(cmacData));
		byte[] dataIn = new byte[16];
		System.arraycopy(getCurCad().hostCryptogram, 0, dataIn, 0, 8);
		System.arraycopy(cmac, 0, dataIn, 8, 8);
		
		Apdu authApdu = new Apdu();
		authApdu.command[Apdu.CLA] = (byte)0x84;
		authApdu.command[Apdu.INS] = (byte)0x82;
		authApdu.command[Apdu.P1] = securityLevel;
		authApdu.command[Apdu.P2] = (byte)0x00;
		authApdu.command[Apdu.P3] = (byte)0x10;
		authApdu.setDataIn(dataIn);
		
		//jcsv.appendLine("cmd> ext-auth plain", JcesShell.CMD_STYLE_CMD);
		//ScriptCommand authCmd = new ScriptCommand(ScriptCommand.APDU, authApdu);
		ScriptCommand authCmd = buildCmd(StringUtil.dumpBytes(apdu));
		process(authCmd);
		
		if (authCmd.getStatus() == Const.GP_STATUS_NO_ERROR) { //签权成功，初始化card信息
			getCurCad().keyVersionNo = getCurCad().tmpkeyVersionNo;
			getCurCad().channelProtocol = getCurCad().tmpChannelProtocol;
			getCurCad().seqCounter = getCurCad().tmpSeqCounter;
			
			getCurCad().icv = cmac;
			
			getCurCad().securityLevel = securityLevel;
			genSessionKey(getCurCad().seqCounter);
		}
		*/
	}

	public String getAction() {
		return Const.GP_CMD_EXT_AUTH;
	}

	public void usage() {
		outUsage("ext-auth [plain|mac|enc|rmac|crmac|crmacenc|crmacencdec]");
	}
	
	public void help() {
		/*
usage: ext-auth [plain|mac|enc|rmac|crmac|crmacenc] 
plain|mac|enc|rmac|crmac|crmacenc
            Desired security level for subsequent APDUs:
              plain   : no secure messaging (default)
              mac     : Command MAC (C-MAC) generation
              enc     : Command data encryption (C-ENC) and C-MAC generation
              rmac    : Response MAC (R-MAC) generation
              crmac   : C-MAC and R-MAC generation
              crmacenc: C-MAC, R-MAC and C-ENC
              crmacencdec: C-MAC, R-MAC, C-ENC and R-DEC
            

Global Platform EXTERNAL AUTHENTICATE command.
		 */
		
		StringBuffer helpStr = new StringBuffer();
		helpStr.append("ext-auth [plain|mac|enc|rmac|crmac|crmacenc]").append("\n");
		helpStr.append("plain|mac|enc|rmac|crmac|crmacenc").append("\n");
		helpStr.append("            Desired security level for subsequent APDUs:").append("\n");
		helpStr.append("              plain      : no secure messaging (default)").append("\n");
		helpStr.append("              mac        : Command MAC (C-MAC) generation").append("\n");;
		helpStr.append("              enc        : Command data encryption (C-ENC) and C-MAC generation").append("\n");;
		helpStr.append("              rmac       : Response MAC (R-MAC) generation").append("\n");;
		helpStr.append("              crmac      : C-MAC and R-MAC generation").append("\n");;
		helpStr.append("              crmacenc   : C-MAC, R-MAC and C-ENC").append("\n");
		helpStr.append("              crmacencdec: C-MAC, R-MAC, C-ENC and R-DEC").append("\n").append("\n");
		helpStr.append("Global Platform EXTERNAL AUTHENTICATE command.");
		outUsage(helpStr.toString());  
	}

}
