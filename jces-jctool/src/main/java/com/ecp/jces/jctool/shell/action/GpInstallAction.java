package com.ecp.jces.jctool.shell.action;


import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.AID;
import com.ecp.jces.jctool.shell.*;
import com.ecp.jces.jctool.simulator.InstallItem;
import com.ecp.jces.jctool.util.*;

import java.util.List;

public class GpInstallAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		AID pkgAid = null;
		AID appAid = null;
		AID instAid = null;
		byte[] paramField = {(byte)0xC9,(byte)0x00};
		byte p1 = (byte)0x0C; //0x04 install-only
		byte privilegeFlg = (byte)0x0;
		
		try {
			if (params.length >= 2) {
				String param1 = params[1].trim().toLowerCase();
				if (param1.startsWith("-")) { //有选项信息
					String param;
					int index = 1;
					while (true) {
						if (index >= params.length) {
							break;
						}
						
						param = params[index].trim().toLowerCase();
						if (!param.startsWith("-"))
							break;
						
						if ("-q".equals(param) || "--install-param".equals(param) ||
								"-i".equals(param) || "--instance-aid".equals(param)) {
														
							if ("-i".equals(param) || "--instance-aid".equals(param)) {
								if (params.length <= index + 1)
									throw new CmdProcessException("jcesshell: install: Option --instance-aid expects some arguments");
								
								//instance aid
								if (!params[index + 1].startsWith("|")) {
									if (params[index + 1].length() % 2 != 0) {
										throw new CmdProcessException("jcesshell: install: Odd number of hex digits");
									}
									if (!StringUtil.isHexString(params[index + 1])) {
										throw new CmdProcessException("jcesshell: install: instance-aid Illegal hex digits");
									}
								} else {
									if (params[index + 1].length() <= 5) {
										throw new CmdProcessException("jcesshell: install: instance-aid invalid");
									}
								}
								instAid = new AID(params[index + 1]);
							}
							
							//install-param
							if ("-q".equals(params[index]) || "--install-param".equals(params[index])) {
								if (params.length <= index + 1)
									throw new CmdProcessException("jcesshell: install: Option --install-param expects some arguments");
								//instance aid
								String instPara = params[index + 1];
								if (!params[index + 1].startsWith("|")) {
									if (instPara.length() > 4 && instPara.charAt(2) == '#' && instPara.charAt(3) == '(' && instPara.endsWith(")")) {
										String tag = instPara.substring(0,2);
										if (!StringUtil.isHexString(tag))
											throw new CmdProcessException("jcesshell: install: instance-aid Illegal hex digits");
										
										if (instPara.length() == 5) {
											instPara = tag + "00";
										} else {
											String subPara = instPara.substring(4,instPara.length() - 1);
											if (!StringUtil.isHexString(subPara))
												throw new CmdProcessException("jcesshell: install: instance-aid Illegal hex digits");
											
											instPara = tag + HexUtil.intToHex(subPara.length() / 2) + subPara;
										}
									} else {
										if (params[index + 1].length() % 2 != 0) {
											throw new CmdProcessException(
													"jcesshell: install: Odd number of hex digits");
										}
										if (!StringUtil
												.isHexString(params[index + 1])) {
											throw new CmdProcessException(
													"jcesshell: install: instance-aid Illegal hex digits");
										}
									}
								}
								paramField = Hex.toByteArray(instPara, new Pointer());
							}
							index += 2;
						} else if ("-e".equals(param) || "--delegation".equals(param) ||
								"-l".equals(param) || "--cm-lock".equals(param) ||
								"-t".equals(param) || "--terminate".equals(param) ||
								"-d".equals(param) || "--default".equals(param) ||
								"-p".equals(param) || "--pin-change".equals(param) ||
								"-s".equals(param) || "--security-domain".equals(param) ||
								"-b".equals(param) || "--sd-dap".equals(param) ||
								"-m".equals(param) || "--mandated-dap".equals(param) ||
								"-o".equals(param) || "--install-only".equals(param)) {
							
							if ("-e".equals(param) || "--delegation".equals(param)) {
								privilegeFlg = (byte)(privilegeFlg | (byte)0x20);
							} else if ("-l".equals(param) || "--cm-lock".equals(param)) {
								privilegeFlg = (byte)(privilegeFlg |(byte)0x10);
							} else if ("-t".equals(param) || "--terminate".equals(param)) {
								privilegeFlg = (byte)(privilegeFlg |(byte)0x08);
							} else if ("-d".equals(param) || "--default".equals(param)) {
								privilegeFlg = (byte)(privilegeFlg |(byte)0x04);
							} else if ("-p".equals(param) || "--pin-change".equals(param)) {
								privilegeFlg = (byte)(privilegeFlg |(byte)0x02);
							} else if ("-s".equals(param) || "--security-domain".equals(param)) {
								privilegeFlg = (byte)(privilegeFlg |(byte)0x80);
							} else if ("-b".equals(param) || "--sd-dap".equals(param)) {
								privilegeFlg = (byte)(privilegeFlg |(byte)0x40);
							} else if ("-m".equals(param) || "--mandated-dap".equals(param)) {
								privilegeFlg = (byte)(privilegeFlg |(byte)0x01);
							} else if ("-o".equals(param) || "--install-only".equals(param)) {
								p1 = (byte)0x04;
							} 
							index++;
						} else {
							throw new CmdProcessException("jcesshell: install: Illegal short option: " + params[index]);
						}

					}
					
					//pkg aid, app aid
					validateAid(params,index);
					pkgAid = new AID(params[index]);
					appAid = new AID(params[index + 1]);
				} else { //没有选项信息
					validateAid(params, 1);
					pkgAid = new AID(params[1]);
					appAid = new AID(params[2]);
				}
				
				//组装apdu
				if (pkgAid == null || appAid == null)
					throw new CmdProcessException("install: Missing mandatory argument: pkgAID appAID");
				if (instAid == null)
					instAid = appAid;
				
				String apduData = printAid(pkgAid) + printAid(appAid) + printAid(instAid) + "01" + HexUtil.byteToHex(privilegeFlg) 
								+ HexUtil.intToHex(paramField.length) + HexUtil.byteArr2HexStr(paramField)  + "00";
				String apdu = "80E6" + HexUtil.byteToHex(p1) + "00" + HexUtil.intToHex(apduData.length() / 2) + apduData;
				//ScriptCommand cmd = buildCmd(apdu);
				
				Cad cad = getCurCad();
				SimulatorCad simCad = null;
				if (cad instanceof SimulatorCad) {
					simCad = (SimulatorCad) cad;
				}
				
				if (simCad != null) {
					simCad.resetVmInfo();
				}
				JcesShell.setInstallItem(null);
				
				ScriptCommand cmd = process(apdu);
				
				if (simCad != null && cmd.getStatus() == 0x9000) {
					InstallItem item = simCad.getVmInfo();
					item.setInstanceAid(HexUtil.byteArr2HexStr(instAid.getAid()));
					item.setPackageAid(HexUtil.byteArr2HexStr(pkgAid.getAid()));
					item.setAppletAid(HexUtil.byteArr2HexStr(appAid.getAid()));

					List<String> eventList = simCad.getEventInfo(instAid.getAid());
					item.setEventList(eventList);

					JcesShell.setInstallItem(item);
				}
				
			} else {
				getJcsv().appendLine("jcshell: install: Missing mandatory argument: [-e|--delegation][-l|--cm-lock][-t|--terminate][-d|--default][-p|--pin-change][-s|--security-domain][-b|--sd-dap][-m|--mandated-dap][-q|--install-param params][-i|--instance-aid AID][-o|--install-only]", JcesShell.CMD_STYLE_ERROR);
				return ;
			}
//		} catch (HexParseException ex) {
//			throw new CmdProcessException(ex.getMessage());
		} catch (CmdProcessException ex) {
			throw ex;
		} catch (Exception ex) {
			if (ex.getMessage() != null) {
				throw new CmdProcessException(ex.getMessage());
			} else {
				throw new CmdProcessException("未知错误");
			}
		}
	}

	private String printAid(AID aid) {
		byte[] aidData = aid.getAid();
		return HexUtil.intToHex(aidData.length) + HexUtil.byteArr2HexStr(aidData);
	}
	
	private void validateAid(String[] params, int index) throws CmdProcessException {
		//验证参数
		if (params.length > index + 2) {
			throw new CmdProcessException("jcesshell: install: Excess arguments: " + params[index + 2]);
		} else if (params.length < index + 1) {
			throw new CmdProcessException("install: Missing mandatory argument: pkgAID");
		} else if (params.length < index + 2) {
			throw new CmdProcessException("install: Missing mandatory argument: appAID");
		}
		
		//pkg aid
		if (!params[index].startsWith("|")) {
			if (params[index].length() % 2 != 0) {
				throw new CmdProcessException("jcesshell: install: Odd number of hex digits");
			}
			if (!StringUtil.isHexString(params[index])) {
				throw new CmdProcessException("jcesshell: install: pkgAID Illegal hex digits");
			}
		} else {
			if (params[index].length() <= 5) {
				throw new CmdProcessException("jcesshell: install: pkgAID invalid");
			}
		}
		
		//app aid
		if (!params[index + 1].startsWith("|")) {
			if (params[index + 1].length() % 2 != 0) {
				throw new CmdProcessException("jcesshell: install: Odd number of hex digits");
			}
			if (!StringUtil.isHexString(params[index + 1])) {
				throw new CmdProcessException("jcesshell: install: appAID Illegal hex digits");
			}
		} else {
			if (params[index + 1].length() <= 5) {
				throw new CmdProcessException("jcesshell: install: appAID invalid");
			}
		}
	}
	public String getAction() {
		return Const.GP_CMD_INSTALL;
	}

	protected void fillStatus() {
		processStatus.put(STATUS_PREFIX + "6A88", "Referenced data not found");
		processStatus.put(STATUS_PREFIX + "6A80", "Incorrect parameters in data field");
		processStatus.put(STATUS_PREFIX + "6581", "Memory failure");
		processStatus.put(STATUS_PREFIX + "6A84", "Not enough memory space");
	}

	public void usage() {
		outUsage("install [-e|--delegation][-l|--cm-lock][-t|--terminate][-d|--default][-p|--pin-change][-s|--security-domain][-b|--sd-dap][-m|--mandated-dap][-q|--install-param params][-i|--instance-aid AID][-o|--install-only] pkgAID appAID");
	}
	
	public void help() {
		outUsage("install [-e|--delegation][-l|--cm-lock][-t|--terminate][-d|--default][-p|--pin-change][-s|--security-domain][-b|--sd-dap][-m|--mandated-dap][-q|--install-param params][-i|--instance-aid AID][-o|--install-only] pkgAID appAID" + 
				"pkgAID  AID of the package (Load File) to install from.\n" +
				"appAID  AID of the applet (Executable Module within the Load File) to be installed.\n\n" + 
				"Install an applet (via the Card Manager) with certain privileges and, if desired, make it selectable.");
	}
}
