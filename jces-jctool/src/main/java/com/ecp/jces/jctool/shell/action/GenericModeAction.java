package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;

public class GenericModeAction extends GenericAction {

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");

		boolean trace = true;
		boolean traceFlg = false;
		if (params.length > 1) {
			if (params.length > 2)
				throw new CmdProcessException("jcesshell: /mode : Excess arguments: " + params[2] + "...");
			
			String param1 = params[1].trim().toLowerCase();
			if (param1.startsWith("trace")) {
				if (param1.equals("trace=on")) {
					trace = true;
					traceFlg = true;
				} else if (param1.equals("trace=off")) {
					trace = false;
					traceFlg = true;
				} else {
					throw new CmdProcessException("jcshell: /mode: Option trace= expects some arguments");
				}
			} else {
				throw new CmdProcessException("jcesshell: /mode : Excess arguments: " + param1 + "...");
			}
			
		}
		
//		if (traceFlg) {
//			getJcsv().handleEvent(2, new Boolean(trace));
//			JcesShell.traceApdus = trace;
//		}
		outModeInfo();
	}

	private void outModeInfo() {
		if (JcesShell.traceApdus) {
			outResp("  trace=on");
		} else {
			outResp("  trace=off");
		}
	}
	
	public String getAction() {
		return Const.GENERIC_CMD_MODE;
	}

	public void usage() {
		outUsage("/mode [trace=on|off]");
	}

	public void help() {
		outUsage("/mode [trace=on|off]\n" + 
				"trace=on|off\n" +
				"    Turn APDU tracing on/off");
	}

}
