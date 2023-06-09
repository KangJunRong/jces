package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;

public class AppCloseAction extends GenericAction {

	public void execute(String input) throws CmdProcessException {
		
	}

	public String getAction() {
		return Const.GENERIC_CMD_APP_CLOSE;
	}

	public void usage() {
		outUsage(Const.GENERIC_CMD_APP_CLOSE);
	}

	public void help() {
		usage();
	}

}
