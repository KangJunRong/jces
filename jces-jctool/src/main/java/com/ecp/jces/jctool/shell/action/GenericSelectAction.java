package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.AID;
import com.ecp.jces.jctool.shell.Cad;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;

public class GenericSelectAction extends GenericAction{

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		if (params.length >= 2) {
			if (params.length > 3) 
				throw new CmdProcessException("jcesshell: /select: Excess arguments: " + params[2]);
			
			String said = params[1];
			if (!said.startsWith("|")) {
				if (!StringUtil.isHexString(said)) {
					throw new CmdProcessException("jcesshell: /select: AID Illegal hex digits");
				}
			} else {
				if (said.length() <= 5) {
					throw new CmdProcessException("jcesshell: /select: AID invalid");
				}
			}
			AID aid = new AID(said);
			byte[] aidData = aid.getAid();
			
			int channel = 0;
			if (params.length == 3) {
				try {
					channel = Integer.parseInt(params[2]);
				} catch (Exception ex) {
					throw new CmdProcessException("jcesshell: " + Const.GENERIC_CMD_SELECT + ": <0-3>: " + ex.getMessage());
				}
			}
			
			if (channel > 3) {
				//jcshell: /select: Number `<0-3>' out of range: 5 is not in the range [0..3]
				throw new CmdProcessException("jcesshell: " + getAction() + ": Number '<0-3>' out of range: " + channel + " is not in the range [0..3]");
			}
			
			//ScriptCommand cmd = buildCmd("00A40400" + HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData));
			ScriptCommand cmd = process(HexUtil.intToHex(channel) + "A40400" + HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData));
			
			int status = cmd.getStatus();
			if (status == 0x9000) {
				Cad.setLogicChannel(channel);
			}
		} else {
			getJcsv().appendLine("jcshell: /select: Missing mandatory argument: AID", JcesShell.CMD_STYLE_ERROR);
		}
	}

	protected void fillStatus() {
		processStatus.put(STATUS_PREFIX + "6283", "Card Life Cycle State is CARD_LOCKED");
		processStatus.put(STATUS_PREFIX + "6882", "Secure messaging not supported");
		processStatus.put(STATUS_PREFIX + "6A81", "Function not supported e.g. card Life Cycle State is CARD_LOCKED");
		processStatus.put(STATUS_PREFIX + "6A82", "Selected Application/file not found");
	}
	
	public String getAction() {
		return Const.GENERIC_CMD_SELECT;
	}

	public void usage() {
		outUsage("/select AID [<0-3>]");
	}

	public void help() {
		StringBuffer helpStr = new StringBuffer();
		helpStr.append("/select AID [<0-3>] ").append("\n");
		helpStr.append("        AID  AID of applet to be selected").append("\n");
		helpStr.append("        <0-3>  Target logical channel (current channel is default).");
		/*
		helpStr.append("        <0-3>  Target logical channel (current channel is default).").append("\n").append("\n");
		helpStr.append("Selects an applet either on the current logical channel or optionally").append("\n");
		helpStr.append("on another, possibly new, logical channel. If the target channel is").append("\n");
		helpStr.append("already open the assigned applet plugin, if any, is reused.").append("\n");
		helpStr.append("If the select FCI returned by the applet includes a remote object").append("\n");
		helpStr.append("reference and there is no applet plugin selected or the current").append("\n");
		helpStr.append("applet plugin is the Card Manager plugin, then it is automatically").append("\n");
		helpStr.append("switched to the JCRMI plugin.");
		*/
		outUsage(helpStr.toString());        
	}

}
