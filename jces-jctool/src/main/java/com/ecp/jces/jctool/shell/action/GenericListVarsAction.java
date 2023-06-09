package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;

import java.util.Iterator;

public class GenericListVarsAction extends GenericAction {

	public void execute(String input) throws CmdProcessException {
		
		Iterator keySet = JcesShell.varsMap.keySet().iterator();
		
		String key = null;
		while (keySet.hasNext()) {
			 key = (String)keySet.next();
			 
			 outResp("   " + key + " = " + (String)JcesShell.varsMap.get(key));
		}
	}

	public String getAction() {
		return Const.GENERIC_CMD_LIST_VARS;
	}

	public void usage() {
		outUsage(Const.GENERIC_CMD_LIST_VARS);
	}

	public void help() {
		/*
		StringBuffer helpStr = new StringBuffer();
		helpStr.append("/list-vars [[pattern|!pattern]..] ").append("\n");
		helpStr.append("[pattern|!pattern]..").append("\n");
		helpStr.append("             List only variables matching the listed patterns.").append("\n");
		helpStr.append("             A leading !means the pattern must not match the variable name.").append("\n");
		helpStr.append("List matching/all variables.");
		outUsage(helpStr.toString());
		*/
		
		outUsage("/list-vars\nList all variables.");
	}

}
