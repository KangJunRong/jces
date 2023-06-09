package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.AID;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;

public class GenericCardAction extends GenericAction {

	public void execute(String input) throws CmdProcessException {
		if (JcesShell.curCad == null) {
			throw new CmdProcessException("jcesshell: Not connected to a terminal.");
		}
		
		String[] params = input.split("\\s+");
		String said = null;
		AID cardManagerAid = null;
		
		if (params.length >= 2) {
			String p1 = params[1].trim().toLowerCase();
			if ("-a".equals(params[1]) || "card-manager-aid".equals(params[1])) {
				if (params.length < 3) {
					throw new CmdProcessException("jcesshell: /card: Missing mandatory argument: AID");
				}
				
				if (params.length > 3) {
					throw new CmdProcessException("jcesshell: /card: Excess arguments: " + params[3]);
				}
				
				said = params[2];
				if (!said.startsWith("|")) {
					if (said.length() % 2 != 0) {
						throw new CmdProcessException("jcesshell: /card: Odd number of hex digits");
					}
					if (!StringUtil.isHexString(said)) {
						throw new CmdProcessException("jcesshell: /card: AID Illegal hex digits");
					}
				} else {
					if (said.length() <= 5) {
						throw new CmdProcessException("jcesshell: /card: AID invalid");
					}
				}
				
				cardManagerAid = new AID(params[2]);
			} else {
				said = params[1];
				if (params.length > 2) {
					throw new CmdProcessException("jcesshell: /card: Excess arguments: " + params[2]);
				}
				if (!said.startsWith("|")) {
					if (said.length() % 2 != 0) {
						throw new CmdProcessException("jcesshell: /card: Odd number of hex digits");
					}
					if (!StringUtil.isHexString(said)) {
						throw new CmdProcessException("jcesshell: /card: AID Illegal hex digits");
					}
				} else {
					if (said.length() <= 5) {
						throw new CmdProcessException("jcesshell: /card: AID invalid");
					}
				}
				
				cardManagerAid = new AID(params[1]);
			}
		} /*else {
			cardManagerAid = getCardManagerAid();
		}
		
		if (cardManagerAid == null) {
			jcsv.appendLine("jcesshell: 内部错误, CardManger AID丢失。", JcesShell.CMD_STYLE_ERROR);
			return ;
		}
		*/
		
		outResp("--Waiting for card...");
		//atr
		getCurCad().atrCmd();
		
		if (cardManagerAid == null) {
			cardManagerAid = getCardManagerAid();
		}
		JcesShell.curCad.cardManagerAid = cardManagerAid;

		//select default applet
		byte[] aidData = cardManagerAid.getAid();
		ScriptCommand cmd = buildCmd("00A40400" + HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData));
		process(cmd);
		
		if (isNormalStatus(cmd.getStatus()) && getJcsv() != null) {
			getCurCad().isGp = true;
			getJcsv().cardEnable(true);
		}
	}

	public String getAction() {
		return Const.GENERIC_CMD_CARD;
	}

	public void usage() {
		outUsage(" /card [-a|card-manager-aid aid]");
	}

	public void help() {
		outUsage(" /card [-a|card-manager-aid aid]\n" +
				"-a|card-manager-aid aid\n" +
				"         Desired Card Manager AID to be used.\n" +
				"    aid  Card Manager AID");
	}

}
