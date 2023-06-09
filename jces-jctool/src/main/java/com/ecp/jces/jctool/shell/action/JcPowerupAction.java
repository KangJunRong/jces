package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.ScriptCommand;

public class JcPowerupAction extends Action {

	public void execute(String input) throws CmdProcessException {
		process(new ScriptCommand(ScriptCommand.POWER_UP));
	}

	public String getAction() {
		return Const.JC_CMD_POWERUP;
	}

	public void usage() {
		outUsage(Const.JC_CMD_POWERUP);
	}

}
