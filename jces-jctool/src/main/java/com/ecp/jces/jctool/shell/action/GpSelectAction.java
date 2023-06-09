package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.AID;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.util.HexUtil;

public class GpSelectAction extends GpAction{

	public void execute(String input) throws CmdProcessException {
		AID aid = getCardManagerAid();
		byte[] aidData = aid.getAid();
		process("00A40400" + HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData));
	}

	protected void fillStatus() {
		processStatus.put(STATUS_PREFIX + "6283", "Card Life Cycle State is CARD_LOCKED");
		processStatus.put(STATUS_PREFIX + "6882", "Secure messaging not supported");
		processStatus.put(STATUS_PREFIX + "6A81", "Function not supported e.g. card Life Cycle State is CARD_LOCKED");
		processStatus.put(STATUS_PREFIX + "6A82", "Selected Application/file not found");
	}
	
	public String getAction() {
		return Const.GP_CMD_SELECT;
	}

	public void usage() {
		outUsage("select");
	}
	
	public void help() {
		StringBuffer helpStr = new StringBuffer();
		helpStr.append("select").append("\n");
		helpStr.append("Send select command APDU to card.");
		outUsage(helpStr.toString());     
	}

}
