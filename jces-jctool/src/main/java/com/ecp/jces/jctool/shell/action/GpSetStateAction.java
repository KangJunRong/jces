package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.AID;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.util.HexUtil;

public class GpSetStateAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		AID cardManagerAid = getCardManagerAid();
		
		String[] params = input.split("\\s+");
		if (params.length >= 2) {
			if (params.length > 2) 
				throw new CmdProcessException("jcesshell: set-state: Excess arguments: " + params[2]);
			
			String state = params[1].trim().toLowerCase();
			byte stateCode = 0;
			if ("ready".equals(state)) {
				stateCode = Const.GP_CARD_LIFE_CYCLE_STATE_READY;
			} else if ("initialized".equals(state)) {
				stateCode = Const.GP_CARD_LIFE_CYCLE_STATE_INITIALIZED;
			} else if ("secured".equals(state)) {
				stateCode = Const.GP_CARD_LIFE_CYCLE_STATE_SECURED;
			} else if ("locked".equals(state)) {
				stateCode = Const.GP_CARD_LIFE_CYCLE_STATE_LOCKED;
			} else if ("terminated".equals(state)) {
				stateCode = Const.GP_CARD_LIFE_CYCLE_STATE_TERMINAL;
			} else {
				throw new CmdProcessException("jcesshell: /set-state: Unknown life cycle state: " + params[1]);
			}
			
			byte[] aidData = cardManagerAid.getAid();
			
			//ScriptCommand cmd = buildCmd("80F080" + HexUtil.byteToHex(stateCode) + HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData));
			process("80F080" + HexUtil.byteToHex(stateCode) + HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData));
		} else {
			getJcsv().appendLine("jcesshell: set-state: Missing mandatory argument: ready|initialized|secured|locked|terminated", JcesShell.CMD_STYLE_ERROR);
		}
	}

	public String getAction() {
		return Const.GP_CMD_SET_STATE;
	}

	public void usage() {
		outUsage("set-state ready|initialized|secured|locked|terminated");
	}

	public void help() {
		outUsage("set-state ready|initialized|secured|locked|terminated\n" +
				"ready|initialized|secured|locked|terminated\n" +
				"        Life cycle state.\n\n" + 
				"Set the Card Manager life cycle state.");
	}
}
