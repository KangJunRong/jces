package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.AID;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;

public class GpDeleteAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		String said = null;
		
		String deleteTag = "00";
		if (params.length >= 2) {
			String p1 = params[1].trim().toLowerCase();
			if ("-r".equals(params[1]) || "--delete-related".equals(p1)) {
				said = params[2];
				if (params.length > 3) {
					throw new CmdProcessException("jcesshell: delete: Excess arguments: " + params[3]);
				}
				
				if (!said.startsWith("|")) {
					if (said.length() % 2 != 0) {
						throw new CmdProcessException("jcesshell: delete: Odd number of hex digits");
					}
					if (!StringUtil.isHexString(said)) {
						throw new CmdProcessException("jcesshell: delete: AID Illegal hex digits");
					}
				} else {
					if (said.length() <= 5) {
						throw new CmdProcessException("jcesshell: delete: AID invalid");
					}
				}
				
				deleteTag = "80";
			} else {
				said = params[1];
				if (params.length > 2) {
					throw new CmdProcessException("jcesshell: delete: Excess arguments: " + params[2]);
				}
				if (!said.startsWith("|")) {
					if (said.length() % 2 != 0) {
						throw new CmdProcessException("jcesshell: delete: Odd number of hex digits");
					}
					if (!StringUtil.isHexString(said)) {
						throw new CmdProcessException("jcesshell: delete: AID Illegal hex digits");
					}
				} else {
					if (said.length() <= 5) {
						throw new CmdProcessException("jcesshell: delete: AID invalid");
					}
				}
			}

			AID aid = new AID(said);
			byte[] aidData = aid.getAid();
			String fieldData = "4F" + HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData);
			//ScriptCommand cmd = buildCmd("80E400" + deleteTag + HexUtil.intToHex(fieldData.length() / 2) + fieldData);
			process("80E400" + deleteTag + HexUtil.intToHex(fieldData.length() / 2) + fieldData);
		} else {
			//jcsv.appendLine("jcshell: delete: Missing mandatory argument: AID", JcesShell.CMD_STYLE_ERROR);
			getJcsv().appendLine("jcshell: delete: Missing mandatory argument: -r|--delete-related", JcesShell.CMD_STYLE_ERROR);
		}
	}

	public String getAction() {
		return Const.GP_CMD_DELETE;
	}

	public void usage() {
		outUsage("delete [-r] AID");
	}
	
	public void help() {
		outUsage("delete [-r] AID\n" + 
				"-r\n" +
				"     Also delete related objects (related instances of a package).\n" +
				"AID  AID of object to be deleted.\n\n" +
				"Global 	Platform DELETE command. Delete a uniquely identifiable\n" +
				"object such as an Executable Load File, an Application or an\n" +
				"Executable Load File and its related Applications."); 
	}

}
