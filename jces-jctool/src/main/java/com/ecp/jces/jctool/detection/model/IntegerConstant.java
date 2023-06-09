package com.ecp.jces.jctool.detection.model;

import java.io.DataInputStream;
import java.io.IOException;

public class IntegerConstant extends BaseConstant {

	private int bytes;

	public int getBytes() {
		return bytes;
	}

	public void setBytes(int bytes) {
		this.bytes = bytes;
	}

	@Override
	public void analysis(DataInputStream dataIn) throws IOException {
		bytes = dataIn.readInt();

	}

}
