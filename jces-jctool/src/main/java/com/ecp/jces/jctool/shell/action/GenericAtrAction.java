package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;

public class GenericAtrAction extends GenericAction {

	@Override
	public void execute(String input) throws CmdProcessException {
		getCurCad().atrCmd();
	}

	public String getAction() {
		return Const.GENERIC_CMD_ATR;
	}

	public void usage() {
		outUsage(Const.GENERIC_CMD_ATR);
	}

	@Override
	public void help() {
		outUsage(Const.GENERIC_CMD_ATR);
	}

}
