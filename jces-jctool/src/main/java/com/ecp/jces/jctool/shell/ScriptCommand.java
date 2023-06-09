package com.ecp.jces.jctool.shell;

import com.ecp.jces.jctool.util.HexUtil;
import com.sun.javacard.apduio.Apdu;

public class ScriptCommand {

	public final static int CLOSE = -1; //关闭连接
	public final static int OPEN = 0; //打开连接
	public final static int RESET = 0x80; //reset
    public final static int POWER_UP = 1;
    public final static int POWER_DOWN = 2;
    public final static int DELAY = 3;
    public final static int APDU = 4;
    public final static int APDU_ACTION = 40;
    public final static int ECHO = 5;
    public final static int OUTPUTON = 6;
    public final static int OUTPUTOFF = 7;
    public final static int NOT = 0xff;

    private String input;
    private String output;
    
    public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output += output;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	protected int type;
    protected Object data;

    public ScriptCommand(int type) {
        this.type = type;
        this.data = null;
    }

    public ScriptCommand(int type, Object data, String input) {
        this.type = type;
        this.data = data;
        this.input = input;
    }
    
    public ScriptCommand(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public int getType() { return type; }
    public Object getData() { return data; }
    
    public byte[] getResp() {
    	if (this.type == APDU || this.type == APDU_ACTION) {
    		
    		Apdu apdu = (Apdu)getData();
    		if (apdu.Le > 0) {
    			byte[] rs = new byte[apdu.Le];
        		
    			System.arraycopy(apdu.getDataOut(), 0, rs, 0, rs.length);
        		return rs;
    		}
    	}
    	return null;
    }
    
    public int getRespLength() {
    	Object data = getData();
    	if (data instanceof Apdu) {
			Apdu apdu = (Apdu) data;
			return apdu.Le;
		}
    	return 0;
    }
    public int getStatus() {
    	if (this.type == APDU || this.type == APDU_ACTION) {
    		Apdu apdu = (Apdu)getData();
    		return apdu.getStatus();
    	}
    	return 0;
    }
    
    public String getStatusCode() {
    	String code = Integer.toHexString(getStatus());
    	if (code.length() < 4) {
    		return "0000";
    	} else {
    		return code.toUpperCase();
    	}
    }
    
    public String getApduCmd() {
    	Apdu apdu = (Apdu)getData();
    	StringBuffer apduStr = new StringBuffer();
    	if (apdu != null) {
    		//case 1
    		apduStr.append(HexUtil.byteToHex(apdu.command[Apdu.CLA])).append(" ");
    		apduStr.append(HexUtil.byteToHex(apdu.command[Apdu.INS])).append(" ");
    		apduStr.append(HexUtil.byteToHex(apdu.command[Apdu.P1])).append(" ");
    		apduStr.append(HexUtil.byteToHex(apdu.command[Apdu.P2]));
    		
    		if (apdu.getDataIn() != null) { //case 3
    			apduStr.append(" ").append(HexUtil.intToHex(apdu.Lc));
    			apduStr.append(" ").append(HexUtil.byteArr2HexStr(apdu.getDataIn(), " ")); 
    			if (apdu.getLe() > 0) { //case 4
    				apduStr.append(" ").append(HexUtil.intToHex(apdu.getLe()));
    			}
    		} else {
    			if (apdu.Le > 0) { //case 2
    				apduStr.append(" ").append(HexUtil.intToHex(apdu.Le));
    			}
    		}
    		
    		return apduStr.toString();
    	}
    	
    	return "";
    }
    
}
