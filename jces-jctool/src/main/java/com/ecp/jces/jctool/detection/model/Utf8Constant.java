package com.ecp.jces.jctool.detection.model;

import java.io.DataInputStream;
import java.io.IOException;

public class Utf8Constant extends BaseConstant {

	private int length;
	private byte[] bytes;

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public void analysis(DataInputStream dataIn) throws IOException {
		length = dataIn.readUnsignedShort();

		bytes = new byte[length];
		dataIn.read(bytes);
	}

}
