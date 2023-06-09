package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.DateUtil;
import com.ecp.jces.jctool.util.HexUtil;

import java.util.Calendar;

public class GpGetCplcAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		//ScriptCommand cmd = buildCmd("80CA9F7F0000");
		ScriptCommand cmd = process("80CA9F7F00");
		
		if (cmd == null || getCurCad() == null) {
			return ;
		}
		
		int status = cmd.getStatus();
		byte[] res = cmd.getResp();
		if (status == 0x9000 && res.length >= 45) {
			int index = 3;
			index = out("  IC Fabricator                      : ", res, index, 2);
			index = out("  IC Type                            : ", res, index, 2);
			index = out("  Operating System ID                : ", res, index, 2);
			index = out("  Operating System release date      : ", res, index, 2, true);
			index = out("  Operating System release level     : ", res, index, 2);
			index = out("  IC Fabrication Date                : ", res, index, 2, true);
			index = out("  IC Serial Number                   : ", res, index, 4);
			index = out("  IC Batch Identifier                : ", res, index, 2);
			index = out("  IC Module Fabricator               : ", res, index, 2);
			index = out("  IC Module Packaging Date           : ", res, index, 2, true);
			index = out("  ICC Manufacturer                   : ", res, index, 2);
			index = out("  IC Embedding Date                  : ", res, index, 2);
			index = out("  IC Pre-Personalizer                : ", res, index, 2);
			index = out("  IC Pre-Perso. Equipment Date       : ", res, index, 2);
			index = out("  IC Pre-Perso. Equipment ID         : ", res, index, 4);
			index = out("  IC Personalizer                    : ", res, index, 2);
			index = out("  IC Personalization Date            : ", res, index, 2);
			index = out("  IC Perso. Equipment ID             : ", res, index, 4);
		}
	}
	
	private int out(String title, byte[] res, int start, int len) {
		return out(title, res, start, len,false);
	}

	private int out(String title, byte[] res, int start, int len, boolean date) {
		byte[] temp = new byte[len];
		System.arraycopy(res, start, temp, 0, len);
		
		String hex = HexUtil.byteArr2HexStr(temp);
		if (date && len == 2) {
			getJcsv().appendLine(title + hex +  " " + formDate(hex), JcesShell.CMD_STYLE_RES);
		} else {
			getJcsv().appendLine(title + hex, JcesShell.CMD_STYLE_RES);
		}
		
		return start + len;
	}
	
	public String getAction() {
		return Const.GP_CMD_GET_CPLC;
	}
	
	private static String formDate(byte[] bdate) {
		String sDate = HexUtil.byteArr2HexStr(bdate);
		if (sDate.length() == 4) {
			return formDate(sDate);
		}
		
		return "";
	}
	
	private static String formDate(String sDate) {
		if (sDate.length() == 4) {
			String year = sDate.substring(0,1);
			String day = sDate.substring(1,4);
			Calendar cld = Calendar.getInstance();
			cld.set(Calendar.YEAR, Integer.parseInt("200" + year));
			cld.set(Calendar.DAY_OF_YEAR, Integer.parseInt(day));
			return DateUtil.dateToString_7(cld.getTime());
		}
		
		return "";
	}
	
	public static void main(String[] args) {
		byte[] bt = {(byte)0x00,(byte)0x78};
		System.out.println(formDate(bt));
	}

	public void usage() {
		outUsage(Const.GP_CMD_GET_CPLC);
	}
	
	public void help() {
		outUsage(Const.GP_CMD_GET_CPLC + "\nGet Card Production life cycle data.");
	}

}
