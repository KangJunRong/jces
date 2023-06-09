package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.AID;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;

public class GpSetAppletAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		if (params.length >= 2) {
			if (params.length > 3) 
				throw new CmdProcessException("jcesshell: /set-applet: Excess arguments: " + params[3]);
			
			if (params.length < 3) 
				throw new CmdProcessException("jcesshell: /set-applet: Missing mandatory argument: installed|selectable|personalized|blocked|locked");
			
			String said = params[1];
			if (!said.startsWith("|")) {
				if (!StringUtil.isHexString(said)) {
					throw new CmdProcessException("jcesshell: /set-applet: AID Illegal hex digits");
				}
			}
			AID aid = new AID(said);
			
			String state = params[2].trim().toLowerCase();
			byte stateCode = 0;
			if ("installed".equals(state)) {
				stateCode = Const.GP_APPLICATION_LIFE_CYCLE_STATE_INSTALL;
			} else if ("selectable".equals(state)) {
				stateCode = Const.GP_APPLICATION_LIFE_CYCLE_STATE_SELECTABLE;
			} else if ("personalized".equals(state)) {
				stateCode = Const.GP_APPLICATION_LIFE_CYCLE_STATE_PERSONALIZED;
			} else if ("blocked".equals(state)) {
				stateCode = Const.GP_APPLICATION_LIFE_CYCLE_STATE_UNLOCKED;
			} else if ("locked".equals(state)) {
				stateCode = Const.GP_APPLICATION_LIFE_CYCLE_STATE_LOCKED;
			} else {
				throw new CmdProcessException("jcesshell: /set-applet: Unknown life cycle state: " + params[2]);
			}
			
			byte[] aidData = aid.getAid();
			//ScriptCommand cmd = buildCmd("80F040" + HexUtil.byteToHex(stateCode) + HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData));
			process("80F040" + HexUtil.byteToHex(stateCode) + HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData));
		} else {
			getJcsv().appendLine("jcshell: set-applet: Missing mandatory argument: AID", JcesShell.CMD_STYLE_ERROR);
		}
	}

	public String getAction() {
		return Const.GP_CMD_SET_APPLET;
	}

	public void usage() {
		outUsage("set-applet AID installed|selectable|personalized|blocked|locked");
	}

	public void help() {
		outUsage("set-applet AID installed|selectable|personalized|blocked|locked\n" +
				"installed|selectable|personalized|blocked|locked\n" +
	            "        Set application life cycle state.\n\n" +
				"Set the life cycle state of a given application.");
	}
}
