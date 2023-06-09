/*
 * Copyright ? 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//+
// Workfile:@(#)CAP.java	1.8
// Version:1.8
// Date:06/06/03
//
// Archive:  /Products/Europa/Tools/installer/com/sun/javacard/scriptgen/CAP.java
// Modified:06/06/03 17:06:08
// Original author: Joe Chen
//-
package com.ecp.jces.jctool.capfiledownload;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class implements CAP file utility methods
 */
public abstract class CAP {

	public final static int SUCCESS = 0;

	public final static int FAILURE = 1;

	public final static int MAX_COMPONENT = 256;

	protected static ResourceBundle msg;

	//
	// shorthand constants for verifyHeader()
	//
	public final static int PKG_MINOR_NUM = Download.OFFSET_HEADER_PKG
			+ Download.OFFSET_PKG_MINOR;

	public final static int PKG_MAJOR_NUM = Download.OFFSET_HEADER_PKG
			+ Download.OFFSET_PKG_MAJOR;

	public final static int PKG_AID_LEN = Download.OFFSET_HEADER_PKG
			+ Download.OFFSET_PKG_AID_LEN;

	protected String pkgName;

	protected ZipFile zipFile;
	
	protected ZipEntry[] components = new ZipEntry[MAX_COMPONENT];

	protected boolean[] seenList = new boolean[MAX_COMPONENT];

	protected int numberOfCustomComponents;

	protected PrintWriter scriptWriter = new PrintWriter(System.out, true);

	protected  PrintWriter logWriter = new PrintWriter(System.err, true);

	protected String installerAID = "";

	protected boolean capVerified;

	protected byte f_flags;

	protected boolean noBeginEnd;
	
/*
 *  add for GOP Script 
 */	
	protected String capHeard = "";
	
	protected int capHeardLen = 0;
	
	protected int totalLen;
	
	protected int aidLength;
	
	protected byte pkgAid[];

	/**
	 * LC for CAP data command
	 */
	public static short APDU_DATA_SIZE = 230;
	//public static short APDU_DATA_SIZE = 255;
	
	public static CAP capScriptGen = null;
	
	/*
	public static CAP GetScriptGen(String capPath, PrintWriter log, ResourceBundle msg,
			String pkgName,byte SCriptGenFormat){
		try {
			if (capScriptGen == null) {
				switch (SCriptGenFormat) {
				case Download.GOP_FORMAT:
					capScriptGen = new CAPScriptGopFormat(capPath, log, msg, pkgName);
					break;
				case Download.SUN_FORMAT:
					capScriptGen = new CAPScriptSunFormat(capPath, log, msg,
							pkgName);
					break;
				}
			}
		}catch(Exception e){

		}
		return capScriptGen;
	}
	*/
	/**
	 * constructor
	 * 
	 * @param capPath
	 *            the CAP file path
	 * @param log
	 *            the output writer
	 * @param msg
	 *            the resource bundle
	 */
	public CAP(String capPath, PrintWriter log, ResourceBundle msg,
			String pkgName) throws IOException {

		logWriter = log; // runtime messages
		capVerified = false;
		this.msg = msg;
		this.pkgName = pkgName;

		try {
			zipFile = new ZipFile(capPath); // open the CAP file
			totalLen = 0;
		} catch (IOException e) {
			// Object[] msgArgs = {e.toString()};
			Object[] msgArgs = { e.toString() };
			logWriter.println(MessageFormat.format(msg
					.getString("CAP.constructor"), msgArgs));
			throw e;
		}
	}

	/**
	 * generate The APDU script with given output files
	 * 
	 * @param out
	 *            the output writer
	 */
	public int genScript(PrintWriter out) {
		scriptWriter = out; // APDU script
		return genScript();
	}

	public int genScript(PrintWriter out, boolean noBE) {
		scriptWriter = out; // APDU script
		noBeginEnd = noBE; // true if "-nobeginend"
		return genScript();
	}

	/**
	 * generate the APDU script
	 */
	public abstract int genScript() ;

	/**
	 * perform CAP file verification
	 */
	public int verifyCAP() {

		if (capVerified) {
			return SUCCESS;
		}

		// loop through the components in the CAP file
		for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
			ZipEntry ze = (ZipEntry) e.nextElement();
			totalLen += ze.getSize();
			if (sortComponent(ze, components, pkgName) != SUCCESS) {
				return FAILURE;
			}
		}

		// verify header information
		if (verifyHeader(zipFile,
				components[Download.DOWNLOAD_ORDER[Download.COMPONENT_HEADER]]) != SUCCESS) {
			return FAILURE;
		}
		
		if(calculateCapSize(zipFile,
				components[Download.DOWNLOAD_ORDER[Download.COMPONENT_DIRECTORY]]) != SUCCESS){
		    return FAILURE;
		}

		capVerified = true;
		return SUCCESS;
	}

	/**
	 * verify the CAP file header information
	 */
	public int verifyHeader(ZipFile jf, ZipEntry header) {

		int status = SUCCESS;
		aidLength = 0;
		Object[] msgArgs = { header.getName() };

		try {
			int size;
			InputStream in = jf.getInputStream(header);
			byte[] line = new byte[286]; // 286 is the max

			// read the complete header component
			if ((size = in.read(line)) <= 0) {
				logWriter.println(MessageFormat.format(msg
						.getString("CAP.read1"), msgArgs));
				status = FAILURE;
				in.close();
				return status; // do not proceed
			}
			// right tag?
			if (line[Download.OFFSET_COMPONENT_TAG] != Download.COMPONENT_HEADER) {
				logWriter.println(MessageFormat.format(msg
						.getString("CAP.tag1"), msgArgs));
				status = FAILURE;
			}
			// right size?
			short len = (short) (((short) line[Download.OFFSET_COMPONENT_SIZE] << 8) + (short) line[Download.OFFSET_COMPONENT_SIZE + 1]);
			if (len != (size - 3)) {
				logWriter.println(MessageFormat.format(msg
						.getString("CAP.size1"), msgArgs));
				status = FAILURE;
			}
			// right magic?
			if (!((line[Download.OFFSET_HEADER_MAGIC] == Download.CAP_MAGIC1)
					&& (line[Download.OFFSET_HEADER_MAGIC + 1] == Download.CAP_MAGIC2)
					&& (line[Download.OFFSET_HEADER_MAGIC + 2] == Download.CAP_MAGIC3) && (line[Download.OFFSET_HEADER_MAGIC + 3] == Download.CAP_MAGIC4))) {
				logWriter.println(MessageFormat.format(msg
						.getString("CAP.magic"), msgArgs));
				status = FAILURE;
			}
			// right CAP minor number?
			if (line[Download.OFFSET_HEADER_MINOR] > Download.CAP_MINOR) {
				logWriter.println(MessageFormat.format(msg
						.getString("CAP.minor"), msgArgs));
				status = FAILURE;
			}
			//
			// right CAP major number?
			//
			if (line[Download.OFFSET_HEADER_MAJOR] > Download.CAP_MAJOR) {
				logWriter.println(MessageFormat.format(msg
						.getString("CAP.major"), msgArgs));
				status = FAILURE;
			}
			// right int flag?
			f_flags = line[Download.OFFSET_HEADER_FLAGS];
			if (((f_flags & 0x01) == 0x01) && !Download.INTEGER_MODE) {
				logWriter.println(MessageFormat.format(msg
						.getString("CAP.intNot"), msgArgs));
				status = FAILURE;
			}
			// right export flag?
			if ((f_flags & 0x02) == 0x02) {
				if (!isComponentPresent(Download.DOWNLOAD_ORDER[Download.COMPONENT_EXPORT])) {
					logWriter.println(MessageFormat.format(msg
							.getString("CAP.expMissing"), msgArgs));
					status = FAILURE;
				}
			}
			// right applet flag?
			if ((f_flags & 0x04) == 0x04) {
				// if (!isComponentPresent(Download.ORDER_Applet)) {
				if (!isComponentPresent(Download.DOWNLOAD_ORDER[Download.COMPONENT_APPLET])) {
					logWriter.println(MessageFormat.format(msg
							.getString("CAP.appMissing"), msgArgs));
					status = FAILURE;
				}
			}
			// right minor number?
			/*
			 * -------------------- if (line[PKG_MINOR_NUM] >
			 * Download.PKG_MINOR) { logWriter.println("Wrong package minor
			 * version number: " + header.getName()); status = FAILURE; } // //
			 * right major number? // if (line[PKG_MAJOR_NUM] !=
			 * Download.PKG_MAJOR) { logWriter.println("Wrong package major
			 * version number: " + header.getName()); status = FAILURE; }
			 * ----------------------
			 */
			// right AID length?
			if (((aidLength = line[PKG_AID_LEN]) > Download.MAX_AID_LEN)
					|| (aidLength < Download.MIN_AID_LEN)) {
				logWriter.println(MessageFormat.format(msg
						.getString("CAP.aidLength"), msgArgs));
				status = FAILURE;
			}
			
			pkgAid = new byte [aidLength];
			for(int i = 0 ; i < aidLength ; i++){
			    pkgAid[i] = line[PKG_AID_LEN + i + 1];
			}
			
			in.close();
		} catch (Exception e) {
			Object[] expArg = { e.toString() };
			logWriter.println(MessageFormat.format(msg
					.getString("CAP.verifyHeader"), expArg));
			status = FAILURE;
		}

		return status;
	}
	
	private int calculateCapSize(ZipFile jf, ZipEntry header) {
		int cLen;
        int status = SUCCESS;
        int offset = 0;
        totalLen = 0;
        Object[] msgArgs = { header.getName() };

        try {
            int size;
            InputStream in = jf.getInputStream(header);
            byte[] line = new byte[286]; // 286 is the max

            // read the complete header component
            if ((size = in.read(line)) <= 0) {
                logWriter.println(MessageFormat.format(msg.getString("CAP.read1"), msgArgs));
                status = FAILURE;
                in.close();
                return status; // do not proceed
            }
            //			 right tag?
            if (line[Download.OFFSET_COMPONENT_TAG] != Download.COMPONENT_DIRECTORY) {
                logWriter.println(MessageFormat.format(msg.getString("CAP.tag1"), msgArgs));
                status = FAILURE;
            }
            short len = (short)(((short)line[Download.OFFSET_COMPONENT_SIZE] << 8) + (short)line[Download.OFFSET_COMPONENT_SIZE + 1]);
            if (len != (size - 3)) {
                logWriter.println(MessageFormat.format(msg.getString("CAP.size1"), msgArgs));
                status = FAILURE;
            }
            offset = Download.OFFSET_COMPONENT_SIZE + 2;
			for(int i = 1 ; i <= 12 ; i++){

				if (i != Download.ORDER_DESCRIPTOR && components[Download.DOWNLOAD_ORDER[i]] != null) {
					cLen = ((int)((line[offset] << 8) & 0xFFFF) + (int)(line[offset + 1] & 0xff));
					totalLen += cLen;
					totalLen += 3;
				}

//            	if (i != 10 && i != 11) {
//            		cLen = ((int)((line[offset] << 8) & 0xFFFF) + (int)(line[offset + 1] & 0xff));
//            		totalLen += cLen;
//            		if (cLen > 0) {
//            			totalLen += 3;
//            		}
//            	}
				offset += 2;
			}
//            totalLen += 30;
			if(totalLen >= 0x10000){
				capHeard = " 0xC4 0x83 " + toHex((byte)((totalLen >> 16) & 0xFF)) + toHex((byte)((totalLen >> 8) & 0xFF)) + " "+  toHex((byte)(totalLen & 0xff));
				capHeardLen = 5;
			} else if(totalLen >= 0x100){
				capHeard = " 0xC4 0x82 " + toHex((byte)((totalLen >> 8) & 0xFF)) + " "+  toHex((byte)(totalLen & 0xff));
				capHeardLen = 4;
			}else{
				capHeard = " 0xC4 0x81 " + toHex((byte)(totalLen & 0xff));
				capHeardLen = 3;
			}
            in.close();
        } catch (Exception e) {
            Object[] expArg = { e.toString() };
            logWriter.println(MessageFormat.format(msg.getString("CAP.verifyHeader"), expArg));
            status = FAILURE;
        }
        return status;
  }

	/**
	 * order the components based on their file names
	 */
	int sortComponent(ZipEntry je, ZipEntry[] list, String pkgName) {

		String name = je.getName();
		int order = 0;

		boolean pkgOK = false;

		if (pkgName == null) {
			pkgOK = true;
		}

		// reduce name to short name
		int slashindex = name.lastIndexOf('/');
		if (slashindex >= 0 && slashindex < name.length()) {
			String dir = name.substring(0, slashindex);
			name = name.substring(slashindex + 1).toLowerCase();
			if (pkgName != null
					&& dir.replace('/', '.').equals(pkgName + ".javacard")) {
				pkgOK = true;
			}
		} else {
			name = name.toLowerCase();
		}

		Object[] msgArgs = { name };

		if (pkgOK && name.equals(Download.HEADER_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_HEADER];
		} else if (pkgOK && name.equals(Download.DIRECTORY_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_DIRECTORY];
		} else if (pkgOK && name.equals(Download.APPLET_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_APPLET];
		} else if (pkgOK && name.equals(Download.IMPORT_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_IMPORT];
		} else if (pkgOK && name.equals(Download.CONSTANTPOOL_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_CONSTANTPOOL];
		} else if (pkgOK && name.equals(Download.CLASS_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_CLASS];
		} else if (pkgOK && name.equals(Download.METHOD_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_METHOD];
		} else if (pkgOK && name.equals(Download.STATICFIELD_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_STATICFIELD];
		} else if (pkgOK && name.equals(Download.EXPORT_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_EXPORT];
		} else if (pkgOK && name.equals(Download.REFERENCELOCATION_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_REFERENCELOCATION];
		} else if (pkgOK && name.equals("descriptor.cap")) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_DESCRIPTOR];
		} else if (pkgOK && name.equals(Download.DEBUG_FILE_NAME)) {
			order = Download.DOWNLOAD_ORDER[Download.COMPONENT_DEBUG];
		} else {
			//order = ++numberOfCustomComponents + Download.COMPONENT_DESCRIPTOR;
			order = ++numberOfCustomComponents + Download.COMPONENT_DEBUG;
		}

		if (seenList[order]) {
			logWriter.println(MessageFormat.format(msg.getString("CAP.dup"),
					msgArgs));
			return FAILURE;
		}
		seenList[order] = true; // mark it

		list[order] = je; // register it

		return SUCCESS;
	}

	/**
	 * convert a CAP file component to APDUs
	 */
	public abstract int genComponent(ZipFile jf, ZipEntry je, int tag);

	/**
	 * convert data to an APDU command
	 */
	protected int genData(int cmdCode, byte[] data, int size) {

		StringBuffer sb = new StringBuffer();

		for (int j = 0; j < size; j++) {
			sb.append(" ");
			sb.append(toHex(data[j]));
		}
		return genAPDU(Download.INS_COMPONENT_DATA, cmdCode, sb.toString(),
				size);
	}

	/**
	 * create an APDU command
	 */
	public abstract int genAPDU(int cmdCode, int tag, String data, int size); 

	/**
	 * convert one byte to hex string
	 */
	protected String toHex(byte b) {
		String h = "";
		String n = Integer.toHexString(b & 0xff).toUpperCase();
		if (n.length() == 1) {
			h = "0x0" + n;
		} else {
			h = "0x" + n;
		}
		return h;
	}

	/**
	 * return true if the component by given tag is in the CAP file
	 */
	public boolean isComponentPresent(byte tag) {
		boolean status = true;
		try {
			if (components[tag] == null) {
				status = false;
			}
		} catch (Exception e) {
			status = false;
		}
		return status;
	}
	/**
	 * generate an installer "begin CAP file" command
	 */
	public int genBeginCAP(String data) {
		return genAPDU(Download.INS_CAP_BEGIN, 0, data, data.length());
	}

	/**
	 * generate an installer "end of file" command
	 */
	public int genEOF() {
		return genAPDU(Download.INS_CAP_END, 0, null, 0);
	}
	
	public void close() {
		try {
			
			if (zipFile != null) {
				zipFile.close();
			}
			
			if (scriptWriter != null) {
				scriptWriter.close();
			}
			
			if (logWriter != null) {
				logWriter.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
