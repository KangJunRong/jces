package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;

public class GenericCloseAction extends GenericAction {

	public void execute(String input) throws CmdProcessException {
		getCurCad().close();

	}

	public String getAction() {
		return Const.GENERIC_CMD_CLOSE;
	}

	public void usage() {
		outUsage(Const.GENERIC_CMD_CLOSE);
	}

	public void help() {
		usage();
	}

}
