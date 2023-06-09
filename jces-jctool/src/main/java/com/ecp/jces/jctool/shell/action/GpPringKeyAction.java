package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.IKey;
import com.ecp.jces.jctool.core.IKeySet;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;

import java.util.List;

public class GpPringKeyAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		if (getCurCad() == null)
			return ;
		
		List keysetList = getCurCad().getKeySetList();
		List keyList = null;
		for (int i = 0; i < keysetList.size(); i++) {
			IKeySet keySet = (IKeySet)keysetList.get(i);
			keyList = keySet.getKeyList();
			
			for (int k = 0; k < keyList.size(); k++) {
				IKey key = (IKey)keyList.get(k);
				getJcsv().appendLine(key.getString(), JcesShell.CMD_STYLE_RES);
			}
			getJcsv().appendLine("", JcesShell.CMD_STYLE_RES);
		}
		/*
		while (keyset.hasNext()) {
			
			Collections.sort(new ArrayList());
			
			String keyid = (String)keyset.next();
			Key key = (Key)getCurCad().getKey(keyid);
			if (key != null) {
				jcsv.appendLine(key.getString(), JcesShell.CMD_STYLE_RES);
			}
		}
		*/
	}

	public String getAction() {
		return Const.GP_CMD_PRINT_KEY;
	}

	public void usage() {
		outUsage(Const.GP_CMD_PRINT_KEY);
	}

}
