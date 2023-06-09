package com.ecp.jces.jctool.capscript;

import com.ecp.jces.jctool.util.HexUtil;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CAPScript extends CAP {
	
	public final static byte INS_LOAD = (byte)0xE8;
	
	public final static byte P1_LOAD_BLOCK = (byte)0x00;
	
	public final static byte P1_LAST_BLOCK = (byte)0x80;
	
	public byte p2_block_number = 0;
	
	public byte p1_last_flag = P1_LOAD_BLOCK;
	private List cmdList;
	private byte[] cmaid;
	
	public CAPScript(List cmdList, String capPath, PrintWriter log, ResourceBundle msg, String pkgName, byte[] cmaid) throws IOException {
		super(capPath, log, msg, pkgName);
		this.cmdList = cmdList;
		this.cmaid = cmaid;
	}

	public int genScript() {
		if (zipFile == null) {
			return FAILURE;
		}

		if (!capVerified) {
			if (verifyCAP() != SUCCESS) {
				return FAILURE;
			}
		}
		
		//genBeginCAP(""); // the installer "Begin CAP" command
		genInstallForLoad(); // the installer "Begin CAP" command
		// index i is the order
		for (int i = 1; i < Download.DOWNLOAD_ORDER.length; i++) {
			// skip descriptor component
			if (i == Download.DOWNLOAD_ORDER[Download.COMPONENT_DESCRIPTOR]) {
				continue;
			}
			if (components[i] == null) { // error!
				if (i == Download.DOWNLOAD_ORDER[Download.COMPONENT_APPLET]
						&& (f_flags & 0x04) != 0x04) {
					continue;
				}
				if (i == Download.DOWNLOAD_ORDER[Download.COMPONENT_EXPORT]
						&& (f_flags & 0x02) != 0x02) {
					continue;
				}

				Object[] msgArgs = { new Integer(Download.ORDER_TO_TAG[i]) };
				logWriter.println(MessageFormat.format(msg
						.getString("CAP.missingComp"), msgArgs));
				return FAILURE;
			}
			if (genComponent(zipFile, components[i], Download.ORDER_TO_TAG[i]) != SUCCESS) {
				return FAILURE;
			}
		}

		genEOF(); // the installer "EOF" command
		return SUCCESS;
	}

	/**
     * 
     */
    private void genInstallForLoad() {
		StringBuffer sb = new StringBuffer();
		sb.append(" ");
		sb.append(toHex((byte)aidLength));
		for (int j = 0; j < aidLength; j++) {
			sb.append(" ");
			sb.append(toHex(pkgAid[j]));
		}
		//sb.append(" 0x08 0xA0 0x00 0x00 0x00 0x03 0x00 0x00 0x00 0x00 0x00 0x00");
		//sb.append(" 0x08 0xa0 0x00 0x00 0x01 0x51 0x00 0x00 0x00 0x00 0x00 0x00");
		sb.append(" 0x" + HexUtil.byteToHex((byte)cmaid.length));
		for (int i = 0; i < cmaid.length; i++) {
			sb.append(" 0x" + HexUtil.byteToHex(cmaid[i]));
		}
		sb.append(" 0x00 0x00 0x00");

        genAPDU(Download.INS_INSTALL_ForLoad, (byte)0, sb.toString(), aidLength + cmaid.length + 5);
    }

    public int genComponent(ZipFile jf, ZipEntry je, int tag) {

		int status = SUCCESS;

		try {
			int size;
			InputStream in = jf.getInputStream(je);
			byte[] line = new byte[CAP.APDU_DATA_SIZE];

			scriptWriter.println();
			scriptWriter.println("// " + je.getName());
			//genCOMP(tag);
			int readLen = line.length;
            if(totalLen >= 0x100){
                readLen -= 4;
            }else{
                readLen -= 3;
            }
			while ((size = in.read(line, 0, readLen)) != -1) {
			    readLen = line.length;
			    
			    if (isComponentPresent(Download.DOWNLOAD_ORDER[Download.COMPONENT_DEBUG])) {
			    	if((Download.COMPONENT_TAGS[tag] == Download.COMPONENT_DEBUG )
							&&(in.read(line, 0, 0) == -1) ){
								p1_last_flag = P1_LAST_BLOCK;
					}
			    } else {
			    	if((Download.COMPONENT_TAGS[tag] == Download.COMPONENT_REFERENCELOCATION )
							&&(in.read(line, 0, 0) == -1) ){
								p1_last_flag = P1_LAST_BLOCK;
					}
			    }
			    
				
				
				if ((status = genData(tag, line, size)) != SUCCESS) {
					break;
				}
				p2_block_number ++;
			}
			in.close();
			//genEOC(tag);
		} catch (Exception e) {
			Object[] msgArgs = { e.toString() };
			logWriter.println(MessageFormat.format(
					msg.getString("CAP.genEx: "), msgArgs));
			status = FAILURE;
		}

		return status;
	}

	public int genAPDU(int cmdCode, int tag, String data, int size) {

		String apdu = "";

		switch (cmdCode) {
		case Download.INS_COMPONENT_DATA:
			switch (tag) {
                    case Download.COMPONENT_HEADER:
                        apdu = toHex(Download.INSTALLER_CLA) + " " 
                               + toHex(INS_LOAD) + " " 
                               + toHex(p1_last_flag) + " " 
                               + toHex(p2_block_number) + " " 
                               + toHex((byte)(size + capHeardLen)) + capHeard + data + " 0x7F;";
                        break;
                    case Download.COMPONENT_DIRECTORY:
                    case Download.COMPONENT_APPLET:
                    case Download.COMPONENT_IMPORT:
                    case Download.COMPONENT_CONSTANTPOOL:
                    case Download.COMPONENT_CLASS:
                    case Download.COMPONENT_METHOD:
                    case Download.COMPONENT_REFERENCELOCATION:
                    case Download.COMPONENT_STATICFIELD:
                    case Download.COMPONENT_EXPORT:
                    case Download.COMPONENT_DEBUG:
                        apdu = toHex(Download.INSTALLER_CLA) + " " + toHex(INS_LOAD) + " " + toHex(p1_last_flag) + " " + toHex(p2_block_number) + " " + toHex((byte)size) + data + " 0x7F;";
                        break;
                    default:
                        // skip
                        return SUCCESS;
                }
			break;
			
	    case Download.INS_INSTALL_ForLoad:
	        apdu = toHex(Download.INSTALLER_CLA) + " " + toHex( Download.INS_INSTALL_ForLoad) + " " + "0x02" + " " +  "0x00" + " " + toHex((byte)size) + data + " 0x7F;";
	        break;

		case Download.INS_CAP_END:
			apdu = System.getProperty("line.separator", "\n");
			//$FALL-THROUGH$
		case Download.INS_CAP_BEGIN:
			/*
			 * optionally skip printing "cap_begin" and "cap_end"
			 */
			apdu = System.getProperty("line.separator", "\n");
			if (noBeginEnd) {
				break;
			}
			//$FALL-THROUGH$
		default:
			// skip
			return SUCCESS;
		}

		//scriptWriter.println(apdu);
		apdu = apdu.replaceAll("0x", "");
        apdu = apdu.replaceAll(";", "");
        apdu = apdu.replaceAll("\\r\\n", "");
        //scriptWriter.println(apdu);
        if (apdu != null && !"".equals(apdu.trim())) {
        	//System.out.println(apdu);
        	//(new ShellCommandJob("/send " + apdu, jcsv)).schedule();
        	cmdList.add(apdu);
        }
		return SUCCESS;
	}


//	public void genScript() {
//
//	}

	public static void main(String[] args) {

    	String apdusFile = "D:\\temp\\jces4\\test_cmcc\\test_cmcc.txt";

		List<String> cmdList = new ArrayList();
		try (PrintWriter logWriter = new PrintWriter(System.err, true);
			 PrintWriter scriptWriter = new PrintWriter(System.out, true);
			 BufferedWriter writer = new BufferedWriter(new FileWriter(apdusFile))) {

			CAPScript script = new CAPScript(cmdList, "D:\\temp\\jces4\\test_cmcc\\test_cmcc.cap", logWriter, msg, null, HexUtil.hexStr2ByteArr("A00000000300037561"));

			if (script.verifyCAP() == 0) {
				script.genScript(scriptWriter, false);
			}

			writer.write("// test_cmcc.cap\r\n");
			for (String apdu : cmdList) {
				writer.write(apdu);
				writer.write("\r\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
