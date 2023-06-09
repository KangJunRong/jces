package com.ecp.jces.jctool.simulator;

public class EventPacket {
	
	private int cmd;
	private int length;
	private byte[] data;
	
	public EventPacket(int cmd) {
		this.cmd = cmd;
	}
	
	public int getCmd() {
		return cmd;
	}
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	

}
