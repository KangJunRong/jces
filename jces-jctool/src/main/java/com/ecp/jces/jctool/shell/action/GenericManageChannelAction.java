package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.Cad;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.HexUtil;
import com.sun.javacard.apduio.Apdu;

public class GenericManageChannelAction extends GenericAction{

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		if (params.length >= 2) {
			if (params.length > 2) 
				throw new CmdProcessException("jcesshell: " + Const.GENERIC_CMD_MANAGE_CHANGE + ": Excess arguments: " + params[2]);
			
			String mode = params[1].toLowerCase();
			if (!"open".equals(mode) && !"close".equals(mode)) {
				throw new CmdProcessException("jcesshell: Invalid operation mode: " + mode);
			} 
			
			ScriptCommand cmd = null;
			if ("open".equals(mode)) {
				Apdu apdu = new Apdu();
				apdu.command[Apdu.CLA] = (byte)0x00;
				apdu.command[Apdu.INS] = (byte)0x70;
				apdu.command[Apdu.P1] = (byte)00;
				apdu.command[Apdu.P2] = (byte)0x00;
				apdu.Lc = 0;
				apdu.Le = 1;
				
				cmd = new ScriptCommand(ScriptCommand.APDU, apdu);
				//cmd = process("0070000001");
				process(cmd);

				byte[] resp = cmd.getResp();
				if (resp != null && resp.length == 1) {
					Cad.setLogicChannel(resp[0]);
				} else {
					getJcsv().appendLine("jcshell: Error.", JcesShell.CMD_STYLE_ERROR);
				}
			} else {
				cmd = process("007080" + HexUtil.intToHex(Cad.getLogicChannel()));
			}
		} else {
			getJcsv().appendLine("jcshell: " + Const.GENERIC_CMD_MANAGE_CHANGE + ": Missing mandatory argument: open|close", JcesShell.CMD_STYLE_ERROR);
		}
	}
	
	public String getAction() {
		return Const.GENERIC_CMD_MANAGE_CHANGE;
	}

	public void usage() {
		outUsage(Const.GENERIC_CMD_MANAGE_CHANGE + " open|close");
	}

	public void help() {
		StringBuffer helpStr = new StringBuffer();
		helpStr.append(Const.GENERIC_CMD_MANAGE_CHANGE + " open|close").append("\n");
		helpStr.append("open|close  Channel management operation.").append("\n\n");
		helpStr.append("ISO 7816-4/Global Platform MANAGE CHANNEL command.").append("\n");
		helpStr.append("Either opens the next available supplementary logical channel or closes the").append("\n");
		helpStr.append(" current logical channel.").append("\n");
		helpStr.append("Upon opening a channel the new channel becomes the current channel.").append("\n");
		helpStr.append("Upon closing a supplementary channel the default channel (zero) becomes").append("\n");
		helpStr.append("the current channel.");
		outUsage(helpStr.toString());        
	}

}
