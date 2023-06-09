package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.*;

public class GenericTerminalAction extends GenericAction {

//	private static MeSimulationView mesv = (MeSimulationView)UIUtil.findView(Const.VIEW_ID_MESIMULATIONVIEW, false);
	
	public void execute(String input) throws CmdProcessException {
		
		//取cad 对象引用
		if (input.startsWith(Const.JCESSHELL_CAD_TERMINAL_PREFIX)) {
			String key = input.replaceFirst(Const.JCESSHELL_CAD_TERMINAL_PREFIX, "");
			Cad tmpCad = (Cad) JcesShell.getCad(key);
			//Cad tmpCad = null;
			if (tmpCad == null) { 
				
				//创建新的cad
				if (key.startsWith(Const.JCESSHELL_CAD_TERMINAL_SIMULATOR)) {
					String name = key.replaceFirst(Const.JCESSHELL_CAD_TERMINAL_SIMULATOR + "\\|", "");
					tmpCad = new SimulatorCad(name);
				}
				
				if (tmpCad == null) {
					outError("jcesshell: Not connected to a terminal");
					return ;
				}
				
				tmpCad.init(getJcsv());
				JcesShell.putCad(key, tmpCad);
				JcesShell.curCad = tmpCad;
				
			} else {
				JcesShell.curCad = tmpCad;
			}
		}
		
		ScriptCommand cmd = new ScriptCommand(ScriptCommand.OPEN, input);
		process(cmd);
		
//		InstallAnalysisView view = (InstallAnalysisView)UIUtil.findView("com.eastcompeace.jces.eclipse.internal.installAnalysis", false);
//		if (view != null) {
//			view.clear();
//
//			view.asyncExec();
//		}
	}

	public String getAction() {
		return Const.GENERIC_CMD_TERMINAL;
	}

	public void usage() {
		outUsage("/terminal name");
	}

}
