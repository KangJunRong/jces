package com.ecp.jces.jctool.shell;


import com.ecp.jces.jctool.simulator.InstallItem;
import com.ecp.jces.jctool.simulator.SimulatorManager;
import com.ecp.jces.jctool.simulator.SimulatorProcess;
import com.ecp.jces.jctool.simulator.VirtualReader;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;

import java.io.*;
import java.util.*;

public class SimulatorCad extends Cad {

	private String name;
	private VirtualReader vReader;

	//private File f =new File("c:\\a.txt");

	private static Map<String, Integer> eventList = new HashMap<>();

	static {
		eventList.put("EVENT_CALL_CONTROL_BY_NAA", 9);
		eventList.put("EVENT_MO_SHORT_MESSAGE_CONTROL_BY_NAA", 10);
		eventList.put("EVENT_EVENT_DOWNLOAD_MT_CALL", 12);
		eventList.put("EVENT_EVENT_DOWNLOAD_CALL_DISCONNECTED", 14);
		eventList.put("EVENT_EVENT_DOWNLOAD_LOCATION_STATUS", 15);
		eventList.put("EVENT_EVENT_DOWNLOAD_USER_ACTIVITY", 16);
		eventList.put("EVENT_EVENT_DOWNLOAD_BROWSER_TERMINATION", 21);
		eventList.put("EVENT_EVENT_DOWNLOAD_DATA_AVAILABLE", 22);
		eventList.put("EVENT_EVENT_DOWNLOAD_CHANNEL_STATUS", 23);
		eventList.put("EVENT_EVENT_DOWNLOAD_LOCAL_CONNECTION", 27);
		eventList.put("EVENT_EVENT_DOWNLOAD_BROWSING_STATUS", 29);

//		eventList.put("EVENT_CALL_CONTROL_BY_NAA", 10);
//		eventList.put("EVENT_MO_SHORT_MESSAGE_CONTROL_BY_NAA", 11);
//		eventList.put("EVENT_EVENT_DOWNLOAD_MT_CALL", 13);
//		eventList.put("EVENT_EVENT_DOWNLOAD_CALL_DISCONNECTED", 15);
//		eventList.put("EVENT_EVENT_DOWNLOAD_LOCATION_STATUS", 16);
//		eventList.put("EVENT_EVENT_DOWNLOAD_USER_ACTIVITY", 17);
//		eventList.put("EVENT_EVENT_DOWNLOAD_BROWSER_TERMINATION", 22);
//		eventList.put("EVENT_EVENT_DOWNLOAD_DATA_AVAILABLE", 23);
//		eventList.put("EVENT_EVENT_DOWNLOAD_CHANNEL_STATUS", 24);
//		eventList.put("EVENT_EVENT_DOWNLOAD_LOCAL_CONNECTION", 28);
//		eventList.put("EVENT_EVENT_DOWNLOAD_BROWSING_STATUS", 30);
	}


	public SimulatorCad(String name) {
		this.name = name;
//		this.mesv = (MeSimulationView)UIUtil.findView(Const.VIEW_ID_MESIMULATIONVIEW, false);
	}

	@Override
	public void destory() {
		vReader.destory();
	}

	@Override
	public void init(JcesShellView sjcsv) {
		SimulatorProcess sp = SimulatorManager.get(name);
		if (sp != null) {
			vReader = sp.getVirtualReader();
		}

		keyInit();
//		jcsv = sjcsv;
	}

	private void setApduData(com.sun.javacard.apduio.Apdu apdu, byte[] res) {
		if (res != null && res.length >= 2) {
			if (res.length > 2) {
				byte[] dataOut = new byte[res.length - 2];
				System.arraycopy(res, 0, dataOut, 0, res.length -2);
				apdu.setDataOut(dataOut);
			}

			apdu.sw1sw2[0] = res[res.length -2];
			apdu.sw1sw2[1] = res[res.length -1];
		}
	}

	@Override
	public void process(ScriptCommand cmd) throws CmdProcessException {
		try {

			//执行指令
			boolean output = true;

			if (cmd == null)
				throw new CmdProcessException("Ignoring expected error");

			byte[] res = null;

			String inputCmd = null;

			switch (cmd.getType()) {
				case ScriptCommand.CLOSE:
					close();
					break;
				case ScriptCommand.OPEN:
					String reader = null;
					String add = (String)cmd.getData();
					String[] addAry = add.split("\\|");
					if (addAry.length == 2 && !StringUtil.isEmpty(addAry[1])) {
						reader = addAry[1];

						try {
							//prompt = "Opening terminal...";
							getJcsv().appendLine("Opening terminal...", JcesShell.CMD_STYLE_RES);
							connection(reader);
//						if (mesv != null) {
//							new ShellDisplayJob(mesv, getCurBerTag(), "Me Simulator").schedule();
//						}
						} catch (Exception ex) {
							ex.printStackTrace();
							//prompt = ex.getMessage();
							getJcsv().appendLine(ex.getMessage(), JcesShell.CMD_STYLE_ERROR);
							getJcsv().appendLine("Open terminal error.", JcesShell.CMD_STYLE_ERROR);
						}
					}
					break;
				case ScriptCommand.POWER_UP:

					break;
				case ScriptCommand.POWER_DOWN:

					break;
				case ScriptCommand.OUTPUTON:

					break;
				case ScriptCommand.OUTPUTOFF:

					break;
				case ScriptCommand.DELAY:
					try {
						Thread.sleep(((Integer) cmd.getData()).intValue() * 1000);
					} catch (InterruptedException e) {
					}
					break;
				case ScriptCommand.ECHO:
					//prompt = cmd.getData().toString();
					getJcsv().appendLine(cmd.getData().toString(), JcesShell.CMD_STYLE_INFO);
					if (log != null && output)
						log.println(cmd.getData());
					break;
				case ScriptCommand.APDU:
					if (this.vReader == null) {
						throw new CmdProcessException("Not connected to a card, reset card first!");
					}

//				apdu = new Apdu(Cad.APDU_LC_MAX_LEN);
					com.sun.javacard.apduio.Apdu capdu = (com.sun.javacard.apduio.Apdu)cmd.getData();
					if (capdu == null)
						break;

					inputCmd = cmd.getApduCmd();
					if (log != null) {
						log.println(inputCmd);
					}

					inputCmd = inputCmd.replaceAll("\\s", "");

 					res = vReader.transmit(HexUtil.hexStr2ByteArr(inputCmd));
					setApduData(capdu, res);

					/*if(inputCmd.startsWith("84E6")){
						writeToTxtByOutputStream(f,inputCmd);
						writeToTxtByOutputStream(f,HexUtil.byteArr2HexStr(res));
					}*/

					if (res != null) {
						out(HexUtil.hexStr2ByteArr(inputCmd.replaceAll(" ", "")), OUT_FLG_CMD);
						out(res, OUT_FLG_RES);
					}

					if (res != null && res.length == 2 && res[0] == (byte)0x91) {
						String fetchCmd = fetchApdu(res[1]);
						fetchCmd = fetchCmd.replaceAll("\\s", "");

						res = vReader.transmit(HexUtil.hexStr2ByteArr(fetchCmd));
						setApduData(capdu, res);
						out(HexUtil.hexStr2ByteArr(fetchCmd.replaceAll(" ", "")), OUT_FLG_CMD);

						if (res != null && res.length > 2) {
							byte[] dataOut = getDataOut(res);
							byte[] rs = new byte[2];
							rs[0] = res[res.length - 2];
							rs[0] = res[res.length - 1];
							out(dataOut, OUT_FLG_CMD);
							out(rs, OUT_FLG_RES);
						}
					}

					if (res != null && res.length == 2 && res[0] == (byte)0x61) {

						String fetchCmd = Apduc0(res[1]);
						fetchCmd = fetchCmd.replaceAll("\\s", "");

						res = vReader.transmit(HexUtil.hexStr2ByteArr(fetchCmd));
						setApduData(capdu, res);
						out(HexUtil.hexStr2ByteArr(fetchCmd.replaceAll(" ", "")), OUT_FLG_CMD);

						if (res != null && res.length > 2) {
							out(res, OUT_FLG_RES);
						}
					}

					break;

				case ScriptCommand.APDU_ACTION:
					if (this.vReader == null) {
						throw new CmdProcessException("Not connected to a card, reset card first!");
					}
					//jcsv.appendLine("cmd> " + cmd.getInput(), JcesShell.CMD_STYLE_CMD);
					com.sun.javacard.apduio.Apdu acapdu = (com.sun.javacard.apduio.Apdu)cmd.getData();
					if (acapdu == null)
						break;

//				apdu = new Apdu(Cad.APDU_LC_MAX_LEN);
					inputCmd = cmd.getApduCmd();
					if (log != null && output) {
						log.println(inputCmd);
					}

					inputCmd = inputCmd.replaceAll("\\s", "");
					res = vReader.transmit(HexUtil.hexStr2ByteArr(inputCmd));
					setApduData(acapdu, res);



					if (res != null) {
						out(HexUtil.hexStr2ByteArr(inputCmd.replaceAll(" ", "")), OUT_FLG_CMD);
						out(res, OUT_FLG_RES);

					}

					if (res != null && res.length == 2) {
						if (res[0] == (byte)0x91) { //有回响信息
							String fetchCmd = fetchApdu(res[1]);
							fetchCmd = fetchCmd.replaceAll("\\s", "");
                            res = vReader.transmit(HexUtil.hexStr2ByteArr(inputCmd));
							setApduData(acapdu, res);

							//jcsv.appendLine("  => " + fetchCmd, JcesShell.CMD_STYLE_RES);
							out(HexUtil.hexStr2ByteArr(fetchCmd.replaceAll(" ", "")), OUT_FLG_CMD);
							if (res != null && res.length > 2) {
								byte[] dataOut = getDataOut(res);
								//jcsv.appendLine("     " + HexUtil.byteArr2HexStr(dataOut, " "), JcesShell.CMD_STYLE_RES);
								//jcsv.appendLine("  <= " + HexUtil.byteToHex(res[res.length - 2]) + " " + HexUtil.byteToHex(res[res.length - 1]), JcesShell.CMD_STYLE_RES);
								byte[] rs = new byte[2];
								rs[0] = res[res.length - 2];
								rs[0] = res[res.length - 1];
								out(dataOut, OUT_FLG_DATA);
								out(rs, OUT_FLG_RES);

//							BerTag berTag = analyseData(res);
//							if (berTag != null) {
//								setCurBerTag(berTag);
//								if (mesv != null) {
//									new ShellDisplayJob(mesv, berTag, "Me Simulator").schedule();
//								}
//				            }
							}

						} else if(res[0] == (byte)0x61)
						{
							//有回响信息
							//jcsv.append("  => " + inputCmd + "\n", JcesShell.CMD_STYLE_RES);
							//jcsv.append("  <= " + HexUtil.byteArr2HexStr(res) + "\n", JcesShell.CMD_STYLE_RES);

							String fetchCmd = fetchApdu(res[1]);
							fetchCmd = fetchCmd.replaceAll("\\s", "");
//						apdu = new Apdu(Cad.APDU_LC_MAX_LEN);
//						apdu.set(fetchCmd);

//						res = card.Transmit(apdu);
							res = vReader.transmit(HexUtil.hexStr2ByteArr(inputCmd));
							setApduData(acapdu, res);

							//jcsv.appendLine("  => " + fetchCmd, JcesShell.CMD_STYLE_RES);
							out(HexUtil.hexStr2ByteArr(fetchCmd.replaceAll(" ", "")), OUT_FLG_CMD);
							if (res != null && res.length > 2) {
								//byte[] dataOut = getDataOut(res);
								//jcsv.appendLine("     " + HexUtil.byteArr2HexStr(dataOut, " "), JcesShell.CMD_STYLE_RES);
								//jcsv.appendLine("  <= " + HexUtil.byteToHex(res[res.length - 2]) + " " + HexUtil.byteToHex(res[res.length - 1]), JcesShell.CMD_STYLE_RES);
								//byte[] rs = new byte[2];
								//rs[0] = res[res.length - 2];
								//rs[0] = res[res.length - 1];
								//out(dataOut, OUT_FLG_DATA);
								out(res, OUT_FLG_RES);

//							BerTag berTag = analyseData(res);
//							if (berTag != null) {
//								setCurBerTag(berTag);
//								if (mesv != null) {
//									new ShellDisplayJob(mesv, berTag, "Me Simulator").schedule();
//								}
//				            }
							}



						}else { //没有回响信息，重新发profile指令
							//JcesShell.doProfile();
							//jcsv.append("  => " + inputCmd + "\n", JcesShell.CMD_STYLE_RES);
							//jcsv.append("  <= " + HexUtil.byteArr2HexStr(res) + "\n", JcesShell.CMD_STYLE_RES);

							com.sun.javacard.apduio.Apdu apduData = (com.sun.javacard.apduio.Apdu)cmd.getData();
							if (apduData != null
									&& apduData.command[com.sun.javacard.apduio.Apdu.CLA] == (byte)0xA0
									&& apduData.command[com.sun.javacard.apduio.Apdu.INS] == (byte)0x14) {
								byte[] dataIn = apduData.getDataIn();
								if (dataIn != null && dataIn.length >= 4 && dataIn[3] == (byte)0x25) { //不是 setupmenu 重做doprofile
								} else {
									JcesShell.doProfile();
								}
							}

						}

					}
					break;

				//default:
				//throw new ParseException(Msg.getMessage("parser.4")); // "Unknown
				// Script
				// command"
			}
		} catch (CmdProcessException ex) {
			throw ex;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				throw new CmdProcessException(e.getMessage());
			} else {
				throw new CmdProcessException("内部错误.");
			}
		}
	}

	private String fetchApdu(int le) {
		StringBuffer sb = new StringBuffer();

		sb.append("A0").append(" ");
		sb.append("12").append(" ");
		sb.append("00").append(" ");
		sb.append("00").append(" ");
		sb.append(HexUtil.intToHex(le));

		return sb.toString();
	}

	private String Apduc0(int le) {
		StringBuffer sb = new StringBuffer();

		sb.append("00").append(" ");
		sb.append("c0").append(" ");
		sb.append("00").append(" ");
		sb.append("00").append(" ");
		sb.append(HexUtil.intToHex(le));

		return sb.toString();
	}

	public void connection(String readerName) {

	}

	private byte[] getDataOut(byte[] dataIn) {
		if (dataIn != null && dataIn.length > 2){
			byte[] dataOut = new byte[dataIn.length -2];
			for (int i = 0; i < dataOut.length; i++) {
				dataOut[i] = dataIn[i];
			}
			return dataOut;
		}

		return null;
	}

	@Override
	public void close() {
	}

	@Override
	public ScriptCommand atrCmd() {

		try {

			byte[] atr = vReader.atr(false);

			getJcsv().appendLine("ATR=" + HexUtil.byteArr2HexStr(atr), JcesShell.CMD_STYLE_RES);

			reset();
		} catch (Exception ex) {
			//throw new ReaderException("未知错误!", ex);
			getJcsv().appendLine("未知错误!", JcesShell.CMD_STYLE_ERROR);
		}

		return new ScriptCommand(ScriptCommand.NOT);
	}

	public void resetVmInfo() {
		try {

			byte[] vmInfo = vReader.resetVmInfo();

//			getJcsv().appendLine("vm-info=" + HexUtil.byteArr2HexStr(vmInfo), JcesShell.CMD_STYLE_RES);

		} catch (Exception ex) {
			getJcsv().appendLine("未知错误!", JcesShell.CMD_STYLE_ERROR);
		}

	}

	public InstallItem getVmInfo() {
		try {

			byte[] vmInfo = vReader.getVmInfo();

			int offset = 0;

//			getJcsv().appendLine("object count:" + HexUtil.bytes2Int(vmInfo, offset), JcesShell.CMD_STYLE_RES);
//			getJcsv().appendLine("object space:" + HexUtil.bytes2Int(vmInfo, offset + 4), JcesShell.CMD_STYLE_RES);
//			getJcsv().appendLine("array count:" + HexUtil.bytes2Int(vmInfo, offset + 8), JcesShell.CMD_STYLE_RES);
//			getJcsv().appendLine("array space:" + HexUtil.bytes2Int(vmInfo, offset + 12), JcesShell.CMD_STYLE_RES);
//			getJcsv().appendLine("Dtr:" + HexUtil.bytes2Int(vmInfo, offset + 16), JcesShell.CMD_STYLE_RES);
//			getJcsv().appendLine("rtr:" + HexUtil.bytes2Int(vmInfo, offset + 20), JcesShell.CMD_STYLE_RES);
//			getJcsv().appendLine("nvm:" + HexUtil.bytes2Int(vmInfo, offset + 24), JcesShell.CMD_STYLE_RES);

			InstallItem item = new InstallItem();

			item.setNewObjectCount(HexUtil.bytes2Int(vmInfo, offset));
			item.setNewObjectSpace(HexUtil.bytes2Int(vmInfo, offset + 4));

			item.setNewArrayCount(HexUtil.bytes2Int(vmInfo, offset + 8));
			item.setNewArraySpace(HexUtil.bytes2Int(vmInfo, offset + 12));

			item.setRtrSpace(HexUtil.bytes2Int(vmInfo, offset + 20));
			item.setDtrSpace(HexUtil.bytes2Int(vmInfo, offset + 16));
			item.setNvmSpace(HexUtil.bytes2Int(vmInfo, offset + 24));


//			getJcsv().appendLine("vm-info=" + HexUtil.byteArr2HexStr(vmInfo), JcesShell.CMD_STYLE_RES);

			return item;
		} catch (Exception ex) {
			getJcsv().appendLine("未知错误!", JcesShell.CMD_STYLE_ERROR);
		}

		return null;

	}

	private boolean isDisableEvent(Integer event, byte[] eventList) {

		int seg = event / 8;
		int bit = event % 8;

		if ((eventList[seg] & (byte)((1 << bit) & 0xFF)) > 0) {
			return true;
		}

		return false;
	}

	public List<String> getEventInfo(byte[] aid) {

		List<String> disableEventList = new ArrayList<>();

		try {
			byte[] eventInfo = vReader.getVmEventInfo(aid);

			if (eventInfo == null || eventInfo.length <= 0) {
				throw new CmdProcessException("获取 toolkit 无响应！");
			}

			if (eventInfo.length < 1 || eventInfo.length < (eventInfo[0] + 2)) {
				throw new CmdProcessException("toolkit 响应格式不正确！");
			}

			int aidLen = eventInfo[0];
			int eventLen = eventInfo[aidLen + 1];

			if (eventLen < 7) {
				throw new CmdProcessException("toolkit 响应格式不正确！");
			}

			byte[] _eventList = new byte[7];
			System.arraycopy(eventInfo, aidLen + 2, _eventList, 0, 7);
			Set<String> keySet = eventList.keySet();

			for (String key : keySet) {
				if (isDisableEvent(eventList.get(key), _eventList)) {
					disableEventList.add(key);
				}
			}

			return disableEventList;
		} catch (Exception ex) {
			getJcsv().appendLine("未知错误!", JcesShell.CMD_STYLE_ERROR);
		}

		return null;
	}

	public static void writeToTxtByOutputStream(File file, String content){

		BufferedOutputStream bufferedOutputStream = null;

		try {

			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));

			bufferedOutputStream.write((content+"\n").getBytes());

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch(IOException e ){

			e.printStackTrace();

		}finally{

			try {

				bufferedOutputStream.close();

			} catch (IOException e) {

				e.printStackTrace();

			}
		}
	}

}
