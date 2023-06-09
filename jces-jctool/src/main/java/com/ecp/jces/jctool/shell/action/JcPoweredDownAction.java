package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.ScriptCommand;

public class JcPoweredDownAction extends Action {

	public void execute(String input) throws CmdProcessException {
		process(new ScriptCommand(ScriptCommand.POWER_DOWN));
	}

	public String getAction() {
		return Const.JC_CMD_POWERED_DOWN;
	}

	public void usage() {
		outUsage(Const.JC_CMD_POWERED_DOWN);
	}

}
