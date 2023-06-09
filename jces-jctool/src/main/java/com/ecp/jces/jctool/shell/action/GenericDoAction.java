package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.StringUtil;
import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apdutool.Msg;

public class GenericDoAction extends GenericSendAction {

	
	public void execute(String input) throws CmdProcessException {
		try {
			String tmpInput = input.replaceFirst("/action ", "");
			
			tmpInput = tmpInput.replaceAll(" ", "");
			if (StringUtil.isHexString(tmpInput)) {
				byte[] cmdBytes = hexStringToBytes(tmpInput);
				if (cmdBytes != null && cmdBytes.length >= 4) {
					Apdu apdu = new Apdu();
					apdu.command[Apdu.CLA] = cmdBytes[Apdu.CLA];
					apdu.command[Apdu.INS] = cmdBytes[Apdu.INS];
					apdu.command[Apdu.P1] = cmdBytes[Apdu.P1];
					apdu.command[Apdu.P2] = cmdBytes[Apdu.P2];

					if (cmdBytes.length == 4) {
						apdu.command[Apdu.P3] = 0;
						apdu.setDataIn(null);
					} else {
						apdu.command[Apdu.P3] = cmdBytes[Apdu.P3];
						
						byte[] datain = getDataIn(cmdBytes);
						if (datain == null) {
							//apdu.setDataIn(null,(cmdBytes[Apdu.P3] & 0xff));
							apdu.Le = cmdBytes[Apdu.P3] & 0xff;
							apdu.setDataIn(null);
							//apdu.setDataIn(new byte[cmdBytes[Apdu.P3] & 0xff], cmdBytes[Apdu.P3] & 0xff);
						} else {
							apdu.setDataIn(getDataIn(cmdBytes));
						}
					}
					ScriptCommand cmd = new ScriptCommand(ScriptCommand.APDU_ACTION, apdu, input);
					process(cmd);
				} else {
					throw new CmdProcessException(Msg.getMessage("parser.4"));
				}
			} else {
				throw new CmdProcessException("analyse command exception!");
			}
		} catch (CmdProcessException ex) {
			throw ex;
		}	
	}
	public String getAction() {
		return Const.GENERIC_CMD_DOACTION;
	}
}
