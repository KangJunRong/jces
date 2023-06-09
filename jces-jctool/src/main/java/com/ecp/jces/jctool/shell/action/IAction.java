package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShellView;

import java.util.HashMap;
import java.util.Map;

public interface IAction {

	public final static String STATUS_PREFIX = "status_";
	public static Map processStatus = new HashMap();

	public JcesShellView getJcsv();
	public void init();
	public void execute(String input) throws CmdProcessException;
	
	public String getAction();
	
	public void usage();
	
	public void help();
}
