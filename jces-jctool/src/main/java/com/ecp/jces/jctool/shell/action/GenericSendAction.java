package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.util.StringUtil;
import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apdutool.Msg;

public class GenericSendAction extends GenericAction {

	public void execute(String input) throws CmdProcessException {
		try {
			//input = input.replaceFirst("/send ", "");
			input = input.substring(6);
			
			input = input.replaceAll(" ", "");
			if (StringUtil.isHexString(input)) {
				if (input.length() < 8) {
					getJcsv().appendLine("jcesshell: Command failed: Illegal APDU command.", JcesShell.CMD_STYLE_ERROR);
					return;
				}
				/*
				byte[] cmdBytes = hexStringToBytes(input);
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
					ScriptCommand cmd = new ScriptCommand(ScriptCommand.APDU, apdu, input);
					process(cmd);
				} else {
					throw new ParseException(Msg.getMessage("parser.4"));
				}
				*/
				process(input);
			} else {
				throw new CmdProcessException("Usage: /send apdu");
				//throw new CmdProcessException("analyse command exception!");
			}
		} catch (CmdProcessException ex) {
			throw ex;
		}
	}

	public String getAction() {
		return Const.GENERIC_CMD_SEND;
	}

	public void usage() {
		outUsage("/send apdu\nSend APDU data");
	}

}
