package com.ecp.jces.jctool.capfiledownload;

import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.shell.JcesShellView;
import com.ecp.jces.jctool.util.StringUtil;

import java.io.InputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CAPScriptSunFormat extends CAP {

	private JcesShellView jcsv;
	
	public CAPScriptSunFormat(JcesShellView jcsv, String capPath, PrintWriter log,
			ResourceBundle msg, String pkgName) throws Exception {
		super(capPath, log, msg, pkgName);
		this.jcsv = jcsv;
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

		genBeginCAP(""); // the installer "Begin CAP" command

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

	public int genComponent(ZipFile jf, ZipEntry je, int tag) {

		int status = SUCCESS;

		try {
			int size;
			InputStream in = jf.getInputStream(je);
			byte[] line = new byte[CAP.APDU_DATA_SIZE];

			scriptWriter.println();
			scriptWriter.println("// " + je.getName());

			genCOMP(tag);
			while ((size = in.read(line, 0, line.length)) != -1) {
				if ((status = genData(tag, line, size)) != SUCCESS) {
					break;
				}
			}
			in.close();
			genEOC(tag);
		} catch (Exception e) {
			Object[] msgArgs = { e.toString() };
			logWriter.println(MessageFormat.format(
					msg.getString("CAP.genEx: "), msgArgs));
			status = FAILURE;
		}

		return status;
	}

	/**
	 * generate an installer "begin component" command
	 */
	public int genCOMP(int tag) {
		return genAPDU(Download.INS_COMPONENT_BEGIN, tag, null, 0);
	}

	/**
	 * generate an installer "end of component" command
	 */
	public int genEOC(int tag) {
		return genAPDU(Download.INS_COMPONENT_END, tag, null, 0);
	}

	public int genAPDU(int cmdCode, int tag, String data, int size) {

		String apdu = "";

		switch (cmdCode) {
		case Download.INS_COMPONENT_DATA:
			switch (tag) {
			case Download.COMPONENT_HEADER:
			case Download.COMPONENT_DIRECTORY:
			case Download.COMPONENT_APPLET:
			case Download.COMPONENT_IMPORT:
			case Download.COMPONENT_CONSTANTPOOL:
			case Download.COMPONENT_CLASS:
			case Download.COMPONENT_METHOD:
			case Download.COMPONENT_REFERENCELOCATION:
			case Download.COMPONENT_STATICFIELD:
			case Download.COMPONENT_EXPORT:
				apdu = toHex(Download.INSTALLER_CLA) + " "
						+ toHex(Download.INS_COMPONENT_DATA) + " "
						+ toHex(Download.COMPONENT_TAGS[tag]) + " 0x00 "
						+ toHex((byte) size) + data + " 0x7F;";
				break;
			default:
				// skip
				return SUCCESS;
			}
			break;

		case Download.INS_CAP_END:
			apdu = System.getProperty("line.separator", "\n");
			//$FALL-THROUGH$
		case Download.INS_CAP_BEGIN:
			/*
			 * optionally skip printing "cap_begin" and "cap_end"
			 */
			if (noBeginEnd) {
				break;
			}
			//$FALL-THROUGH$
		case Download.INS_COMPONENT_BEGIN:
		case Download.INS_COMPONENT_END:
			apdu += toHex(Download.INSTALLER_CLA) + " " + toHex((byte) cmdCode)
					+ (tag >= 0 ? " " + toHex((byte) tag) : " 0x00") + " 0x00 "
					+ (size > 0 ? " " + toHex((byte) size) : "0x00")
					+ ((data == null || data.length() <= 0) ? "" : " " + data)
					+ " 0x7F;";
			break;

		default:
			// skip
			return SUCCESS;
		}

		//scriptWriter.println(apdu);
		apdu = apdu.replaceAll("0x", "");
        apdu = apdu.replaceAll(";", "");
        apdu = apdu.replaceAll("\\r\\n", "");
        //scriptWriter.println(apdu);
        if (!StringUtil.isEmpty(apdu)) {
        	System.out.println(apdu);
//        	(new ShellCommandJob("/send " + apdu, jcsv)).schedule();
			JcesShell.lineInput("/send " + apdu);
        }
		return SUCCESS;
	}

}
