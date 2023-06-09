package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;

public class GpAuthAction extends GpAction {
	private static final String CMD = "auth [plain|mac|enc|rmac|crmac|crmacenc|crmacencdec] [static|derive]";
	
	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		
		String securityLevel = "plain";
		String keyMode = "static";
		
		if (params.length > 1) {
			if (params.length > 3)
				throw new CmdProcessException("usage: " + CMD);
			
			if (params.length == 2) {
				String para1 = params[1].trim().toLowerCase();
				if ("enc".equals(para1) 
						|| "mac".equals(para1) 
						|| "rmac".equals(para1) 
						|| "crmac".equals(para1) 
						|| "crmacenc".equals(para1) 
						|| "crmacencdec".equals(para1) 
						|| "plain".equals(para1)
						|| "static".equals(para1)
						|| "derive".equals(para1)) {
					if ("enc".equals(para1) 
							|| "mac".equals(para1) 
							|| "rmac".equals(para1) 
							|| "crmac".equals(para1) 
							|| "crmacenc".equals(para1) 
							|| "crmacencdec".equals(para1) 
							|| "plain".equals(para1)) {
						securityLevel = para1;
					} else {
						keyMode = para1;
					}
				} else {
					throw new CmdProcessException(CMD + "\njcesshell: auth: Illegal option:" + params[1]);
				}
			} else if (params.length == 3) {
				String para1 = params[1].trim().toLowerCase();
				if ("enc".equals(para1) 
						|| "mac".equals(para1) 
						|| "rmac".equals(para1) 
						|| "crmac".equals(para1) 
						|| "crmacenc".equals(para1) 
						|| "crmacencdec".equals(para1) 
						|| "plain".equals(para1)) {
					securityLevel = para1;
				} else {
					throw new CmdProcessException(CMD + "\njcesshell: unknown security level: " + params[1]);
				}
				
				String para2 = params[2].trim().toLowerCase();
				if ("static".equals(para2) || "derive".equals(para2)) {
					keyMode = para2;
				} else {
					throw new CmdProcessException(CMD + "\njcesshell: unknown key mode: " + params[2]);
				}
			}

		}

		
        boolean flg = JcesShell.lineInput("init-update 255 " + keyMode);
        if (flg) {
        	JcesShell.lineInput("ext-auth " + securityLevel);
        } else {
        	outError("Unexpected error; aborting execution.");
        }
	}

	public String getAction() {
		return Const.GP_CMD_AUTH;
	}

	public void usage() {
		outUsage(CMD);		
	}
	
	
	public void help() {
		StringBuffer helpStr = new StringBuffer();
		helpStr.append("auth [plain|mac|enc|rmac|crmac|crmacenc] [static|derive]").append("\n");
		helpStr.append("plain|mac|enc|rmac|crmac|crmacenc").append("\n");
		helpStr.append("            Desired security level for subsequent APDUs:").append("\n");
		helpStr.append("              plain      : no secure messaging (default)").append("\n");
		helpStr.append("              mac        : Command MAC (C-MAC) generation").append("\n");;
		helpStr.append("              enc        : Command data encryption (C-ENC) and C-MAC generation").append("\n");;
		helpStr.append("              rmac       : Response MAC (R-MAC) generation").append("\n");;
		helpStr.append("              crmac      : C-MAC and R-MAC generation").append("\n");;
		helpStr.append("              crmacenc   : C-MAC, R-MAC and C-ENC").append("\n");
		helpStr.append("              crmacencdec: C-MAC, R-MAC, C-ENC and R-DEC").append("\n");
		
		helpStr.append("static|derive").append("\n");
		helpStr.append("              Desired key mode for secure channel.").append("\n").append("\n");
		outUsage(helpStr.toString());  
	}

}
