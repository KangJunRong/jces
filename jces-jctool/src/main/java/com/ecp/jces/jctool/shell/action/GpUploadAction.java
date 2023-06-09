package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.capfiledownload.CAP;
import com.ecp.jces.jctool.capfiledownload.CAPScriptGopFormat;
import com.ecp.jces.jctool.core.AID;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.HexUtil;
import com.ibm.jc.CapFile;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;


public class GpUploadAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		
		String path;
		if (params.length < 2) {
			throw new CmdProcessException("upload: Missing mandatory argument: CAP-file");
		} else {
			if (params.length == 2) {
				path = params[1];
			} else {
				int index = input.indexOf(' ');
				if (index < 0)
					throw new CmdProcessException("upload: Missing mandatory argument: CAP-file");
				
				String tmpInput = input.substring(index).trim();
				StringTokenizer st = new StringTokenizer(tmpInput, "\"");
				if (st.countTokens() >= 1 && tmpInput.startsWith("\"")) {
					int startIndex = tmpInput.indexOf("\"");
					int endIndex = tmpInput.indexOf("\"", startIndex);
					
					if (!tmpInput.endsWith("\"") || st.countTokens() > 2) {
						throw new CmdProcessException("jcesshell: upload: Excess arguments: " + input.substring(endIndex) + " ...");
					}
					path = tmpInput;
				} else {
					if (params.length > 3) {
						throw new CmdProcessException("jcesshell: upload: Excess arguments: " + params[2] + " ...");
					}
					
					path = params[1];
				}
			}				
		}
		
		
		if (path.startsWith("\"")) {
			path = path.substring(1, path.length());
		}
		
		if (path.endsWith("\"")) {
			path = path.substring(0, path.length() - 1);
		}
		
		List cmdList = new ArrayList();
		PrintWriter logWriter = null;
        PrintWriter scriptWriter = null;
        
        CAP cap = null;
        CapFile capInfo = null;
		try {
			ResourceBundle msg = PropertyResourceBundle.getBundle("com/ecp/jces/jctool/capfiledownload/MessagesBundle");
	        logWriter = new PrintWriter(System.err, true);
	        scriptWriter = new PrintWriter(System.out, true);

	        
	        AID cmAid = getCardManagerAid();
	        cap = new CAPScriptGopFormat(cmdList, path, logWriter, msg, null, cmAid.getAid());
	        
	        if (cap.verifyCAP() == 0) {
	            cap.genScript(scriptWriter, false);
	        }
	        
	        if (cmdList.size() < 1)
	        	throw new CmdProcessException("分析cap文件出错.");
	        
	        String cmd = null;
	        ScriptCommand scriptCmd = null;
	        int loadBytes = 0;
	        for (int i = 0; i < cmdList.size(); i++) {
	        	cmd = (String)cmdList.get(i);
	        	if (i != 0) {
	        		try {
	        			loadBytes += Integer.parseInt(cmd.split("\\s")[4], 16);
	        		} catch (Exception ex) {
	        			
	        		}
	        	}
	        	scriptCmd = process(HexUtil.hexStr2ByteArr(cmd.replaceAll(" ", "")));
	        	
	        	if (scriptCmd == null || !isNormalStatus(scriptCmd.getStatus())) {
	        		return;
	        	}
	        }

	        try {
				capInfo = new CapFile(path, null);

				outResp("Load report:");
				//outResp("  " + loadBytes + " bytes loaded");

				outResp("  effective code size on card:");
				outResp("      + Package AID       " + capInfo.s_pkgAID);
				outResp("      + Applet AIDs       " + capInfo.s_applets);
				outResp("      + Classes           " + capInfo.s_classes);
				outResp("      + Methods           " + capInfo.s_methods);
				outResp("      + Statics           " + capInfo.s_statics);
				outResp("      + Exports           " + capInfo.s_exports);
				outResp("     ------------------------------");
				outResp("        overall           " + capInfo.effCodeSize + "  bytes");
			} catch (Exception ex) {

			}
		} catch (Exception ex) {
			throw new CmdProcessException("jcesshell:Cannot read <" + path + ">: " + ex.getMessage() + ">");
		} finally {
			if (logWriter != null) {
				logWriter.close();
			}
			
			if (scriptWriter != null) {
				scriptWriter.close();
			}
			
			if (cap != null) {
				cap.close();
			}
			
			capInfo = null;
		}
		
	}
	
	public String getAction() {
		return Const.GP_CMD_UPLOAD;
	}

	public void usage() {
		//outUsage("upload [-p|--progress][-c|--components][-r|--random][-l|--package package-name][-s|--sd SD-AID][-m|--params parameters][-b|--block_length length][-a|--auto][-d|--load-debug] CAP-file ");
		outUsage("upload CAP-file ");
	}
	
	public void help() {
		outUsage("upload CAP-file \nLoad a package onto the card via the Card Manager.");
	}

}
