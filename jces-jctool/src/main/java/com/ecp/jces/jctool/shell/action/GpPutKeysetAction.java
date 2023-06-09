package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.IKey;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.util.HexUtil;

public class GpPutKeysetAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		
		if (params.length >= 2) {
			
			if (params.length > 3)
				throw new CmdProcessException("jcesshell: put-keyset : Excess arguments: " + params[2]);
			
			StringBuffer keysetData = new StringBuffer();
			ISCP scp = getCurCad().getScp();
			
			try {
				int keyset = Integer.parseInt(params[1]);
				
				for (int i = 1; i < 4; i++) {
					IKey key = (IKey)getCurCad().getKey(keyset, i, scp.getKeyType());
					if (key == null)
						throw new CmdProcessException("jcesshell: Command failed: No such key: " + keyset + "/" + i);
					
					keysetData.append(keyset).append("/");
					keysetData.append(i).append("/");
					keysetData.append(scp.getKeyType()).append("/");
					keysetData.append(HexUtil.byteArr2HexStr(key.getKeymat())).append(" ");
					/*
					keysetData.append(80); //默认0x80
					byte[] keymat = key.getKeymat();
					keysetData.append(HexUtil.intToHex(keymat.length));
					keysetData.append(HexUtil.byteArr2HexStr(scp.keyEncryption(Cryto.padding(keymat))));
					
					byte[] encData = scp.keyCheckValue(keymat);
					keysetData.append(HexUtil.intToHex(encData.length));
					keysetData.append(HexUtil.byteArr2HexStr(encData));
					*/
				}
				
				JcesShell.lineInput("put-key " + keysetData.toString());
				/*
				StringBuffer apdu1 = new StringBuffer();
				apdu1.append("80D800");
				apdu1.append("81");
				apdu1.append(HexUtil.intToHex(keysetData.length() / 2 + 1));
				apdu1.append(HexUtil.intToHex(keyset));
				apdu1.append(keysetData);
				
				ScriptCommand cmd = process(apdu1.toString());
				if (cmd.getStatus() != Const.GP_STATUS_NO_ERROR) {
					jcsv.appendLine("Add new key set didn't work, try modify ...",JcesShell.CMD_STYLE_ERROR);
					StringBuffer apdu2 = new StringBuffer();
					apdu2.append("80D8");
					apdu2.append(HexUtil.intToHex(keyset));
					apdu2.append("81");
					apdu2.append(HexUtil.intToHex(keysetData.length() / 2 + 1));
					apdu2.append("00");
					apdu2.append(keysetData);
					cmd = process(apdu2.toString());
				}
				*/
			} catch (CmdProcessException ex) {	
				throw ex;
			} catch (Exception ex) {
				getJcsv().appendLine("jcesshell: " + ex.getMessage(), JcesShell.CMD_STYLE_ERROR);
			}	
		} else {
			getJcsv().appendLine("jcesshell: put-keyset: Missing mandatory argument: keyset.", JcesShell.CMD_STYLE_ERROR);
			
		}
	}

	public String getAction() {
		return Const.GP_CMD_PUT_KEYSET;
	}

	public void usage() {
		outUsage("put-keyset keyset");
	}

}
