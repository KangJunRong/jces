package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.Cad;
import com.ecp.jces.jctool.shell.CmdProcessException;

public class GenericSetChannelAction extends GenericAction{

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		
		int channel = 0;
		if (params.length >= 2) {
			if (params.length > 2) 
				throw new CmdProcessException("jcesshell: " + Const.GENERIC_CMD_SET_CHANGE + ": Excess arguments: " + params[2]);
			
			try {
				channel = Integer.parseInt(params[1]);
			} catch (Exception ex) {
				throw new CmdProcessException("jcesshell: " + Const.GENERIC_CMD_SET_CHANGE + ": <0-3>: " + ex.getMessage());
			}
			
			if (channel > 3) {
				//jcshell: /select: Number `<0-3>' out of range: 5 is not in the range [0..3]
				throw new CmdProcessException("jcesshell: " + getAction() + ": Number '<0-3>' out of range: " + channel + " is not in the range [0..3]");
			}
		}
		
		Cad.setLogicChannel(channel);
	}
	
	public String getAction() {
		return Const.GENERIC_CMD_SET_CHANGE;
	}

	public void usage() {
		outUsage(Const.GENERIC_CMD_SET_CHANGE + " [<0-3>]");
	}

	public void help() {
		StringBuffer helpStr = new StringBuffer();
		helpStr.append(Const.GENERIC_CMD_SET_CHANGE + " [<0-3>] ").append("\n");
		helpStr.append("     <0-3>  Desired logical channel.").append("\n\n");
		helpStr.append("Sets the logical channel number used in any subsequent APDU communication.").append("\n");
		helpStr.append("The CLA byte of any APDU header sent to the card is automatically modified").append("\n");
		helpStr.append("to reflect the logical channel number as defined in ISO 7816-4. Modification").append("\n");
		helpStr.append("only takes place if the original CLA byte indicates channel zero, otherwise").append("\n");
		helpStr.append("the given channel info is maintained.").append("\n");
		helpStr.append("By default the logical channel number is zero.").append("\n");
		helpStr.append("Note: Normally the current channel number is implicitely modified by the").append("\n");
		helpStr.append("commands \"/manage-channel\" and thus it is not necessary").append("\n");
		helpStr.append("to use this command under normal circumstances.");
		outUsage(helpStr.toString());        
	}

}
