package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.shell.StatusBytes;

import java.util.List;

public class GenericNormalStatusAction extends Action {

	private static final int NORMAL_STATUS_CASE_ADD = 1;
	private static final int NORMAL_STATUS_CASE_REMOVE = 2;
	
	private void normalStatus(String stausList, int ncase) {
		String[] nsAry = stausList.split(",");
		
		for (int i = 0; i < nsAry.length; i++) {

			switch (ncase) {
			case NORMAL_STATUS_CASE_ADD: //add
				JcesShell.addNormalStatus(nsAry[i]);
				break;
			case NORMAL_STATUS_CASE_REMOVE: //remove
				JcesShell.removeNormalStatus(nsAry[i]);
				break;
			}
		}
	}
	
	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");

		if (params.length >= 2 && params.length <= 3) {
			if ((("-a".equals(params[1]) || "-r".equals(params[1])) && params.length == 3)
					|| "-c".equals(params[1])
					|| "-l".equals(params[1])) {
				if (params.length == 3) {
					if (!params[2].matches("([0-9A-Fa-fXx]{4},)*[0-9A-Fa-fXx]{4}$")) {
						getJcsv().appendLine("jcshell: /normal-status 状态字节格式不正确。", JcesShell.CMD_STYLE_ERROR);
						return ;
					}
				}
				
				if ("-a".equals(params[1])) {
					normalStatus(params[2], NORMAL_STATUS_CASE_ADD);
				} else if ("-r".equals(params[1])) {
					normalStatus(params[2], NORMAL_STATUS_CASE_REMOVE);
				} else if ("-c".equals(params[1])) {
					JcesShell.clearNormalStatus();
				} 
				
				getJcsv().appendLine("Normal Status bytes list:", JcesShell.CMD_STYLE_RES);
				List rs = JcesShell.getNormalStatusList();
				
				int colCount = 2;
				StringBuffer ns = new StringBuffer();
				ns.append("  9000  61xx");
				StatusBytes sb = null;
				for (int i = 0; i < rs.size(); i++) {
					if (colCount > 8) {
						ns.append("\n");
						colCount = 0;
					}
					
					sb = (StatusBytes)rs.get(i);
					//ns.append("  ").append(HexUtil.byteArr2HexStr(sb.getSw1sw2()));
					ns.append("  ").append(sb.getStatus());
					colCount++;
				}
				getJcsv().appendLine(ns.toString(), JcesShell.CMD_STYLE_RES);
				return ;
			} 
		}
		
		getJcsv().appendLine("jcshell: /normal-status [-a normalStatusList] [-r normalStatusList] [-c] [-l]", JcesShell.CMD_STYLE_ERROR);
	}

	public String getAction() {
		return Const.GENERIC_CMD_NORMAL_STATUS;
	}

	/**
	 * -a 增加正常状态字节列表，每个正常状态字节用“，”分隔；
	 * -r 在正常状态字节列表移除状态字节，如果移除多个状态字节，第个正常状态字节用“，”分隔；
	 * -c 清空正常状态字节列表；
	 * -l 显示正常状态字节列表。
	 */
	public void usage() {
		outUsage("/normal-status [-a normalStatusList] [-r normalStatusList] [-c] [-l]"); 
	}
	
	public void help() {
		StringBuffer helpStr = new StringBuffer();
		helpStr.append("/normal-status [-a normalStatusList] [-r normalStatusList] [-c] [-l]").append("\n");
		helpStr.append("        -a 增加正常状态字节列表，每个正常状态字节用“，”分隔。").append("\n");
		helpStr.append("        -r 在正常状态字节列表移除状态字节，如果移除多个状态字节，第个正常状态字节用“，”分隔。").append("\n");
		helpStr.append("        -c 清空正常状态字节列表。").append("\n");
		helpStr.append("        -l 显示正常状态字节列表。").append("\n");
		helpStr.append("        normalStatusList 正常状态字节列表,第个正常状态字节用“，”分隔(例如：9F00，61xx);\n如果有不确定字节使用“”代替。").append("\n");
		outUsage(helpStr.toString());
	}

}
