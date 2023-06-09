package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;

import java.io.File;
import java.util.List;

public class GenericSetVarAction extends GenericAction {

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		
		String key = null;
		String value = null;
		if (params.length > 1) {
			if (params.length > 3) {
				throw new CmdProcessException("Usage: set-var variable value\njcesshell: set-var : Excess arguments: " + params[3]);
			}
			
			if (params.length < 3) {
				throw new CmdProcessException("Usage: set-var variable value");
			}
		} else {
			throw new CmdProcessException("Usage: set-var variable value");
		}
				
		value = params[2];
		key = params[1];
		
		/*
		if (value.indexOf("\\") >= 0) {
			throw new CmdProcessException("jcesshell: Backslash only in quotes allowed.");
		}
		*/
		
		if (value.startsWith("\"")) {
			value = value.substring(1, value.length());
		}
		
		if (value.endsWith("\"")) {
			value = value.substring(0, value.length() - 1);
		}
		
		if (key == null)
			throw new CmdProcessException("key is Null");
		
		if (value == null) {
			JcesShell.setVar(key.toLowerCase(), "");
		} else {
			JcesShell.setVar(key.toLowerCase(), value);
		}
	}

	public String getAction() {
		return Const.GENERIC_CMD_SET_VAR;
	}

	public void usage() {
		outUsage("set-var variable value");
	}

}
