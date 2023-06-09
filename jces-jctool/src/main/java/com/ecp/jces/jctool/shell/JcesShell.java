package com.ecp.jces.jctool.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.action.AppCloseAction;
import com.ecp.jces.jctool.shell.action.GenericAtrAction;
import com.ecp.jces.jctool.shell.action.GenericCardAction;
import com.ecp.jces.jctool.shell.action.GenericCloseAction;
import com.ecp.jces.jctool.shell.action.GenericDoAction;
import com.ecp.jces.jctool.shell.action.GenericListVarsAction;
import com.ecp.jces.jctool.shell.action.GenericManageChannelAction;
import com.ecp.jces.jctool.shell.action.GenericModeAction;
import com.ecp.jces.jctool.shell.action.GenericNormalStatusAction;
import com.ecp.jces.jctool.shell.action.GenericSelectAction;
import com.ecp.jces.jctool.shell.action.GenericSendAction;
import com.ecp.jces.jctool.shell.action.GenericSetChannelAction;
import com.ecp.jces.jctool.shell.action.GenericSetVarAction;
import com.ecp.jces.jctool.shell.action.GenericTerminalAction;
import com.ecp.jces.jctool.shell.action.GpAuthAction;
import com.ecp.jces.jctool.shell.action.GpCardInfoAction;
import com.ecp.jces.jctool.shell.action.GpDeleteAction;
import com.ecp.jces.jctool.shell.action.GpExtAuthAction;
import com.ecp.jces.jctool.shell.action.GpGetCplcAction;
import com.ecp.jces.jctool.shell.action.GpInitUpdateAction;
import com.ecp.jces.jctool.shell.action.GpInstallAction;
import com.ecp.jces.jctool.shell.action.GpPringKeyAction;
import com.ecp.jces.jctool.shell.action.GpPutKeyAction;
import com.ecp.jces.jctool.shell.action.GpPutKeysetAction;
import com.ecp.jces.jctool.shell.action.GpSelectAction;
import com.ecp.jces.jctool.shell.action.GpSetAppletAction;
import com.ecp.jces.jctool.shell.action.GpSetKeyAction;
import com.ecp.jces.jctool.shell.action.GpSetStateAction;
import com.ecp.jces.jctool.shell.action.GpUploadAction;
import com.ecp.jces.jctool.shell.action.IAction;
import com.ecp.jces.jctool.shell.action.JcPoweredDownAction;
import com.ecp.jces.jctool.shell.action.JcPowerupAction;
import com.ecp.jces.jctool.simulator.InstallItem;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;

public class JcesShell {

	public static final int CMD_STYLE_CMD = 1;
	public static final int CMD_STYLE_RES = 0;
	public static final int CMD_STYLE_ERROR = 2;
	public static final int CMD_STYLE_INFO = 1;
	
	public static final String PREF_NORMAL_STATUS_BYTES_KEY = "pref.normal.status.bytes";
	
	public static final String SCRIPT_PATH = "scriptPath ";
	
	public static final char[] cmdPadding = new char[]{' ', ' ', ' ',' ', ' ', ' ',' ', ' ', ' ', ' ',
		' ', ' ', ' ',' ', ' ', ' ',' ', ' ', ' ', ' ',
		' ', ' ', ' ',' ', ' ', ' ',' ', ' ', ' ', ' ',
		' ', ' ', ' ',' ', ' ', ' ',' ', ' ', ' ', ' ',
		' ', ' ', ' ',' ', ' ', ' ',' ', ' ', ' ', ' ',};
	
	//public static final Map actionRegister = new HashMap();
	private static JcesShellView jcsv;
	//private static MeSimulationView mesv = (MeSimulationView)UIUtil.findView(Const.VIEW_ID_MESIMULATIONVIEW, true);
	private static final Map cadMap = new HashMap();
	public static Cad curCad;
	public static Map varsMap = new HashMap();
	
	private static List actions = new ArrayList();
	private static List gpActions = new ArrayList();
	public static boolean traceApdus = true;

	private static InstallItem installItem;
	
	private static List normalStatusList = new ArrayList();
	
	public static List getNormalStatusList() {
		return normalStatusList;
	}
	
	public static void addNormalStatus(String status) {
		StatusBytes sb = new StatusBytes(status);
		
		if ("9000".equals(sb.getStatus()) || "61xx".equals(sb.getStatus())) {
			return;
		}
		
		int index = normalStatusList.indexOf(sb);
		if (index == -1) {
			normalStatusList.add(sb);
		}
		
	}
	
	public static void removeNormalStatus(String status) {
		StatusBytes sb = new StatusBytes(status);
		
		normalStatusList.remove(sb);
	}
	
	public static void clearNormalStatus() {
		normalStatusList.clear();
	}
	
	public static boolean lineInput(String input) {
		if (StringUtil.isEmpty(input))
			return true;

		IAction action = null;
		List matchActions = null;
		try {
			
			/*
			ScriptCommand cmd = curCad.analyse(input); //分析命令
			
			//处理命令请求
			curCad.process(cmd);
			*/
			String cmd = input;
			String[] params = input.split("\\s+");
			
			if (input != null && input.indexOf(" ") > -1) {
				cmd = input.substring(0, input.indexOf(" "));
			}
			
			if ("?".equals(cmd)) {
				matchActions = new ArrayList();
				
				if (params.length > 1) {
					cmd = params[1].trim().toLowerCase();
					action = getMatchAction(matchActions, cmd);
					if (action != null) {
						action.usage();
						return true;
					}
				} else {
					getJcsv().appendLine("List of currently active commands: ", CMD_STYLE_RES);
					
					IAction mAction = null;
					for (int i = 0; i < actions.size(); i++) {
						mAction = (IAction)actions.get(i);
						if (!mAction.getAction().equals(Const.GENERIC_CMD_DOACTION)) {
							matchActions.add(actions.get(i));
						}
					}
					
					if (JcesShell.curCad != null && JcesShell.curCad.isGp) {
						matchActions.addAll(gpActions);
					}
					
					listMatchActions(matchActions);
					return true;
				}
				
				if (matchActions.size() == 1) {
					action = (IAction)matchActions.get(0);
					action.usage();
					return true;
				} else if (matchActions.size() > 1) {
					getJcsv().appendLine("jcesshell: " + cmd + " is ambigious - possible completions:", CMD_STYLE_RES);
					listMatchActions(matchActions);
				} 
				
				getJcsv().appendLine("jcesshell: No such command: " + cmd, CMD_STYLE_ERROR);
				return true;
			} else if ("help".equals(cmd.toLowerCase())) {
				matchActions = new ArrayList();
				
				if (params.length > 1) {
					cmd = params[1].trim().toLowerCase();
					action = getMatchAction(matchActions, cmd);
					if (action != null) {
						action.help();
						return true;
					}
				} else {
					getJcsv().appendLine("List of currently active commands: ", CMD_STYLE_RES);
					//matchActions.addAll(actions);
					IAction mAction = null;
					for (int i = 0; i < actions.size(); i++) {
						mAction = (IAction)actions.get(i);
						if (!mAction.getAction().equals(Const.GENERIC_CMD_DOACTION)) {
							matchActions.add(actions.get(i));
						}
					}
					
					if (JcesShell.curCad != null && JcesShell.curCad.isGp) {
						matchActions.addAll(gpActions);
					}
					
					listMatchActions(matchActions);
					return true;
				}
				
				if (matchActions.size() == 1) {
					action = (IAction)matchActions.get(0);
					action.help();
					return true;
				} else if (matchActions.size() > 1) {
					getJcsv().appendLine("jcesshell: " + cmd + " is ambigious - possible completions:", CMD_STYLE_RES);
					listMatchActions(matchActions);
				} 
				
				getJcsv().appendLine("jcesshell: No such command: " + cmd, CMD_STYLE_ERROR);
				return true;
			}
			
			/*
			boolean processFlg = false;
			for (int i = 0; i < actions.size(); i++) {
				IAction action = (IAction)actions.get(i);
				if (cmd.equals(action.getAction())) {
					action.execute(input);
					processFlg = true;
					break;
				}
			}
			*/
			
			boolean processFlg = false;
			matchActions = new ArrayList();
			/*
			for (int i = 0; i < actions.size(); i++) { //一般命令
				IAction action = (IAction)actions.get(i);
				
				if (action.getAction().equals(cmd)) {
					action.execute(input);
					processFlg = true;
					return true;
				} else if (action.getAction().startsWith(cmd)) {
					matchActions.add(action);
				}
			}
			
			if (curCad != null && curCad.isGp) {
				for (int i = 0; i < gpActions.size(); i++) { //GP命令
					IAction action = (IAction)gpActions.get(i);
					
					if (action.getAction().equals(cmd)) {
						action.execute(input);
						processFlg = true;
						return true;
					} else if (action.getAction().startsWith(cmd)) {
						matchActions.add(action);
					}
				}
			}
			*/
			
			action = getMatchAction(matchActions, cmd);
			if (action != null) {
				action.execute(input);
				return true;
			}
			
			
			if (!processFlg) {
				if (matchActions.size() == 1) {
					action = (IAction)matchActions.get(0);
					action.execute(input);
					processFlg = true;
					return true;
				} else {
					if (matchActions.size() > 1) {
						getJcsv().appendLine("jcesshell: " + cmd + " is ambigious - possible completions:", CMD_STYLE_RES);
						listMatchActions(matchActions);
						/*
						jcsv.appendLine("jcesshell: " + cmd + " is ambigious - possible completions:", CMD_STYLE_ERROR);
						StringBuffer out = new StringBuffer();
						for  (int i = 0; i < matchActions.size(); i++) {
							action = (IAction)matchActions.get(i);
							if (i == 0)
								out.append("  ");
							
							out.append(action.getAction()).append("\t");
								
								if ((i + 1) % 6 == 0)
									out.append("\n  ");
						}
						jcsv.appendLine(out.toString(), CMD_STYLE_ERROR);
						*/
					}
				}
			} 
			
			File jcshPath = null;
			if (!processFlg && curCad != null) { //运行脚本文件
				List jcshPaths = curCad.getJcshPathList();
				
				//set-var path 目录
				String path = getVar("path");
				if (!StringUtil.isEmpty(path)) {
					File pathFolder = new File(path);
					if (pathFolder.isDirectory()) {
						jcshPaths.add(pathFolder);
					}
				}
				
				
				for (int i = 0; i < jcshPaths.size(); i++) {
					jcshPath = (File)jcshPaths.get(i);
					
					if (jcshPath != null && jcshPath.exists() && jcshPath.isDirectory()) {
						processFlg = runJcsh(jcshPath, input);
						if (processFlg)
							return true;
					}
				}
				/*
				if (jcsv.getCurrentConfig() != null && file != null && file.exists()) {//会话 shell
					runJcsh(file);
					processFlg = true;
				} else { //全局 shell
					String path = getVar("path");
					if (StringUtil.isEmpty(path))
						return true;
					
					file = new File(path);
					if (file.exists() && file.isDirectory()) {
						File[] files = file.listFiles();
						
						if (files == null || files.length < 1)
							return true;
						
						String fileName = "/" + input.toLowerCase() + ".jcsh";
						String tmpFileName = null;
						for (int i = 0; i < files.length; i++) {
							tmpFileName = files[i].toString().trim().toLowerCase();
							tmpFileName = tmpFileName.replaceAll("\\\\", "/");
							if (tmpFileName.endsWith(fileName) && files[i].isFile()) {
								runJcsh(files[i]);
								processFlg = true;
								break;
							}
						}
						
					}
				}
				*/
			}
			
			if (!processFlg) {
				String path = getVar("path");
				if (!StringUtil.isEmpty(path)) {	
					jcshPath = new File(path);
					processFlg = runJcsh(jcshPath, input);
				}
			}
			
			if (!processFlg)
				getJcsv().appendLine("jcesshell: No such command: " + cmd, CMD_STYLE_ERROR);
			return true;
//		} catch (CmdProcessException ex) {
//			if (ex.getMessage() != null) {
//				getJcsv().appendLine(ex.getMessage(), CMD_STYLE_ERROR);
//			} else {
//				getJcsv().appendLine("Error:" + ex.getClass().toString(), CMD_STYLE_ERROR);
//			}
//			return false;
		} catch (Exception ex) {
			if (ex.getLocalizedMessage() != null) {
				getJcsv().appendLine(ex.getLocalizedMessage(), CMD_STYLE_ERROR);
			} else {
				getJcsv().appendLine("Error:" + ex.getClass().toString(), CMD_STYLE_ERROR);
			}
			return false;
		}
		//return false;
	}
	
	private static void listMatchActions(List matchActions) {
		try {
			int actionSize = matchActions.size();

			if (actionSize > 1) {
				StringBuffer out = new StringBuffer();
				IAction action = null;

				int row = 0;
				if (actionSize % 6 == 0) {
					row = actionSize / 6;
				} else {
					row = actionSize / 6 + 1;
				}

				int index = 0;
				String[][] actionAry = new String[row][6];
				int[] colMaxLen = new int[6];

				for (int col = 0; col < 6; col++) {

					for (int tmpRow = 0; tmpRow < row; tmpRow++) {
						index = 6 * tmpRow + col;

						if (index < actionSize) {
							action = (IAction) matchActions.get(index);
							actionAry[tmpRow][col] = action.getAction();

							if (action.getAction().length() > colMaxLen[col]) {
								colMaxLen[col] = action.getAction().length();
							}
						}
					}
				}

				for (int tmpRow = 0; tmpRow < row; tmpRow++) {

					for (int col = 0; col < 6; col++) {
						if (col == 0)
							if (tmpRow == 0)
								out.append("  ");
							else
								out.append("\n  ");

						if (!StringUtil.isEmpty(actionAry[tmpRow][col])) {
							out.append(actionAry[tmpRow][col]);
							out.append(cmdPadding, 0, colMaxLen[col] - actionAry[tmpRow][col].length() + 2);
						}
					}
				}
				getJcsv().appendLine(out.toString(), CMD_STYLE_RES);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static IAction getMatchAction(List matchActions, String cmd) {
		for (int i = 0; i < actions.size(); i++) { //一般命令
			IAction action = (IAction)actions.get(i);
			
			if (action.getAction().equals(Const.GENERIC_CMD_DOACTION)) {
				continue;
			}
		

			if (action.getAction().equals(cmd.toLowerCase())) {
				return action;
			} else if (action.getAction().startsWith(cmd)) {
				matchActions.add(action);
			}
		}
		
		if (curCad != null && curCad.isGp) {
			for (int i = 0; i < gpActions.size(); i++) { //GP命令
				IAction action = (IAction)gpActions.get(i);
				
				if (action.getAction().equals(cmd)) {
					return action;
				} else if (action.getAction().startsWith(cmd)) {
					matchActions.add(action);
				}
			}
		}
		
		return null;
	}
	
	public static void setVar(String key, String var) {
		if (StringUtil.isEmpty(key))
			return ;
		
		varsMap.put(key.toLowerCase(), var);
	}
	
	public static String getVar(String key) {
		if (StringUtil.isEmpty(key))
			return null;
		
		return (String)varsMap.get(key.toLowerCase());
	}
	
	public static boolean runJcsh(File filePath, String cmd) {


		if (!StringUtil.isEmpty(cmd) && filePath != null && filePath.exists() && filePath.isDirectory()) {
			File[] files = filePath.listFiles();
			
			if (files == null || files.length < 1)
				return false;
			
			String fileName = "/" + cmd.toLowerCase() + ".jcsh";
			String tmpFileName = null;
			for (int i = 0; i < files.length; i++) {
				tmpFileName = files[i].toString().trim().toLowerCase();
				tmpFileName = tmpFileName.replaceAll("\\\\", "/");
				if (tmpFileName.endsWith(fileName) && files[i].isFile()) {
					runJcshCmd(files[i], cmd);
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	public static void runJcshCmd(File file , String fileName) {
		if (file == null || !file.exists())
			return;

		FileReader fReader = null;
		BufferedReader bReader = null;
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String lineData = null;
			boolean runFlg = true;
			while ((lineData = bReader.readLine()) != null) {
				lineData = lineData.trim();
				if(!(StringUtil.isEmpty(lineData) 
						|| lineData.startsWith("#")
						|| lineData.startsWith("//"))) {
					
        			int leftBracketOff = lineData.indexOf("//");
        			if (leftBracketOff > 0) {
        				lineData = lineData.substring(0, leftBracketOff);			
        			}
        			
        			leftBracketOff = lineData.indexOf("#");//{//)
        			if (leftBracketOff > 0 && (lineData.charAt(leftBracketOff-2) != 'C' && lineData.charAt(leftBracketOff-1) != '9')) {
        				lineData = lineData.substring(0, leftBracketOff);			
        			}
        			
        			if (lineData.startsWith("-")) {
        				runFlg = JcesShell.lineInput(lineData.substring(1));
        				if (!runFlg) {
        					getJcsv().appendLine("Unexpected error; aborting execution!", CMD_STYLE_ERROR);
        					break;
        				}
        			} else {
        				getJcsv().appendLine("jcsh-> " + fileName + "-> "+ lineData , CMD_STYLE_CMD);
            			JcesShell.lineInput(lineData);
        			}
        		}
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			
			if (bReader != null)
				try {
					bReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			if (fReader != null)
				try {
					fReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	private static void doActionOut(String cmd) {
		getJcsv().appendLine("cmd> " + cmd, JcesShell.CMD_STYLE_CMD);
	}
	
//	public static boolean doAction(Cmd cmd) {
//		try {
//			String cmdReq = null;
//			if (cmd instanceof SetUpMenuCmd) { //set up menu
//				SetUpMenuCmd tmp = (SetUpMenuCmd)cmd;
//				return doEnvelope("D307820201819001" + HexUtil.intToHex(tmp.getItemTag().getIdentifier()));
//			} else if (cmd instanceof SelectItemCmd) { //select item
//				SelectItemCmd tmp = (SelectItemCmd)cmd;
//				return doResponse("8103012400820282818301009001" + HexUtil.intToHex(tmp.getItemTag().getIdentifier()));
//			} else if (cmd instanceof GetInputCmd) { //get input
//				GetInputCmd tmp = (GetInputCmd)cmd;
//				String input = tmp.getInput();
//				int dcs = tmp.getDcs();
//
//				byte[] inputByte = null;
//				try {
//					switch (dcs) {
//					case Const.DCS_ACSII:
//						inputByte = input.getBytes("ascii");
//						break;
//
//					case Const.DCS_UCS2:
//						inputByte = input.getBytes("UTF-16BE");
//						break;
//
//					default:
//						inputByte = input.getBytes();
//						break;
//					}
//				} catch (UnsupportedEncodingException ex) {
//					ex.printStackTrace();
//					return false;
//				}
//
//				String inpuStr = HexUtil.intToHex(dcs) + HexUtil.byteArr2HexStr(inputByte);
//				return doResponse("8103012304820201818301008D " + HexUtil.intToHex(inpuStr.length() / 2) + inpuStr);
//			} else if (cmd instanceof BackCmd) { //back
//				BackCmd tmp = (BackCmd)cmd;
//				cmdReq = "/action A01400000C810301" + HexUtil.intToHex(tmp.getCmd()) + "0082028281830111";
//				doActionOut(cmdReq);
//				return lineInput(cmdReq);
//			} else if (cmd instanceof SendSmsCmd) { //send sms
//				SendSmsCmd tmp = (SendSmsCmd)cmd;
//				return doResponse("810301130082028281830100");
//			} else if (cmd instanceof DisplayCmd) { //display text
//				DisplayCmd tmp = (DisplayCmd)cmd;
//				return doResponse("810301130082028281830100");
//			} else if (cmd instanceof CommStatementCmd){
//				CommStatementCmd commCmd = (CommStatementCmd)cmd;
//				cmdReq = "/action " + commCmd.getStatement();
//				doActionOut(cmdReq);
//				return lineInput(cmdReq);
//			} else {
//				throw new CmdException("nonsupport script command.");
//			}
//		} catch (CmdException ex) {
//			getJcsv().appendLine(ex.getMessage(), CMD_STYLE_ERROR);
//			return false;
//		} catch (PCSCException ex) {
//			//ex.printStackTrace();
//			String errorMsg = ex.getMessage();
//			getJcsv().appendLine(errorMsg, CMD_STYLE_ERROR);
//			return false;
//		} catch (Exception ex) {
//			getJcsv().appendLine("未知错误.", CMD_STYLE_ERROR);
//			return false;
//		}
//	}
	
	public static boolean doEnvelope(String data) {
		String cmd = "/action A0C20000" + HexUtil.intToHex(data.length() / 2) + data;
		doActionOut(cmd);
		return lineInput(cmd);
	}
	
	public static boolean doResponse(String data) {
		String cmd = "/action A0140000" + HexUtil.intToHex(data.length() / 2) + data;
		doActionOut(cmd);
		return lineInput(cmd);
	}
	
	public static boolean doProfile() {
		String cmd = "/action A0100000143FFFFFFFFF7F00FFFF03FE1FFF05148603030000";
		doActionOut(cmd);
		return lineInput(cmd);
	}
	
//	public static String getPrompt() {
//		if (curCad != null) {
//			return ((ApduTool)curCad).getPrompt();
//		} else {
//			return "";
//		}
//
//		//return "-";
//	}
	
	public static Cad getCad(String key) {
		return (Cad)cadMap.get(key);
	}
	
	public static void putCad(String key, Cad cad) {
		cadMap.put(key, cad);
	}
	
	public static void removeCad(String key) {
		cadMap.remove(key);
	}
	
	static {
		/*
		//jc
		IAction jcPowerupAction = new JcPowerupAction();
		jcPowerupAction.init();
		actions.add(jcPowerupAction);
		
		IAction jcPoweredDownAction = new JcPoweredDownAction();
		jcPoweredDownAction.init();
		actions.add(jcPoweredDownAction);
		*/
		//generic
		IAction genericAtrAction = new GenericAtrAction();
		genericAtrAction.init();
		actions.add(genericAtrAction);
		
		IAction genericTerminalAction = new GenericTerminalAction();
		genericTerminalAction.init();
		actions.add(genericTerminalAction);
		
		IAction genericSendAction = new GenericSendAction();
		genericSendAction.init();
		actions.add(genericSendAction);
		
		IAction genericDoAction = new GenericDoAction();
		genericDoAction.init();
		actions.add(genericDoAction);
		
		IAction genericCardAction = new GenericCardAction();
		genericCardAction.init();
		actions.add(genericCardAction);
		
		IAction genericSelectAction = new GenericSelectAction();
		genericSelectAction.init();
		actions.add(genericSelectAction);
		
		IAction genericCloseAction = new GenericCloseAction();
		genericCloseAction.init();
		actions.add(genericCloseAction);
		
		IAction genericSetVarAction = new GenericSetVarAction();
		genericSetVarAction.init();
		actions.add(genericSetVarAction);
		
		IAction genericListVarsAction = new GenericListVarsAction();
		genericListVarsAction.init();
		actions.add(genericListVarsAction);
		
		IAction genericModeAction = new GenericModeAction();
		genericModeAction.init();
		actions.add(genericModeAction);
		
		IAction genericSetChannelAction = new GenericSetChannelAction();
		genericSetChannelAction.init();
		actions.add(genericSetChannelAction);
		
		IAction genericManageChannelAction = new GenericManageChannelAction();
		genericManageChannelAction.init();
		actions.add(genericManageChannelAction);
		
		IAction appCloseAction = new AppCloseAction();
		appCloseAction.init();
		actions.add(appCloseAction);
		
		IAction normalStatusAction = new GenericNormalStatusAction();
		normalStatusAction.init();
		actions.add(normalStatusAction);
		
		//gp
		IAction gpCardInfoAction = new GpCardInfoAction();
		gpCardInfoAction.init();
		gpActions.add(gpCardInfoAction);
		
		IAction gpInitUpdateAction = new GpInitUpdateAction();
		gpInitUpdateAction.init();
		gpActions.add(gpInitUpdateAction);

		IAction gpUploadAction = new GpUploadAction();
		gpUploadAction.init();
		gpActions.add(gpUploadAction);
		
		IAction gpInstallAction = new GpInstallAction();
		gpInstallAction.init();
		gpActions.add(gpInstallAction);

		IAction gpDeleteAction = new GpDeleteAction();
		gpDeleteAction.init();
		gpActions.add(gpDeleteAction);
		
		IAction gpExtAuthAction = new GpExtAuthAction();
		gpExtAuthAction.init();
		gpActions.add(gpExtAuthAction);
		
		IAction gpAuthAction = new GpAuthAction();
		gpAuthAction.init();
		gpActions.add(gpAuthAction);
		
		IAction gpSetAppletAction = new GpSetAppletAction();
		gpSetAppletAction.init();
		gpActions.add(gpSetAppletAction);
		
		IAction gpSetStateAction = new GpSetStateAction();
		gpSetStateAction.init();
		gpActions.add(gpSetStateAction);
		
		IAction gpSetKeyAction = new GpSetKeyAction();
		gpSetKeyAction.init();
		gpActions.add(gpSetKeyAction);
		
		IAction gpPringKeyAction = new GpPringKeyAction();
		gpPringKeyAction.init();
		gpActions.add(gpPringKeyAction);
		
		IAction gpGetCplcAction = new GpGetCplcAction();
		gpGetCplcAction.init();
		gpActions.add(gpGetCplcAction);
		
		IAction gpPutKeyAction = new GpPutKeyAction();
		gpPutKeyAction.init();
		gpActions.add(gpPutKeyAction);
		
		IAction gpPutKeysetAction = new GpPutKeysetAction();
		gpPutKeysetAction.init();
		gpActions.add(gpPutKeysetAction);
		
		IAction gpSelectAction = new GpSelectAction();
		gpSelectAction.init();
		gpActions.add(gpSelectAction);
		
	}
	
	public static JcesShellView getJcsv() {
		if (jcsv == null) {
			jcsv = JcesShellView.newInstance();
		}
		
		return jcsv;
	}
	
	public static void setJcsvPrint(boolean isPrint) {
		JcesShellView jv = getJcsv();
		if (jv != null) {
			jv.setPrint(isPrint);
		}
	}

	public static void setInstallItem(InstallItem _item) {
		JcesShell.installItem = _item;
	}

	public static InstallItem getInstallItem() {
		return JcesShell.installItem;
	}
}
