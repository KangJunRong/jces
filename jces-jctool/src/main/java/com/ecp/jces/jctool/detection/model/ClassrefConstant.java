package com.ecp.jces.jctool.detection.model;

import java.io.DataInputStream;
import java.io.IOException;

public class ClassrefConstant extends BaseConstant {

	private int nameIndex;

	public int getNameIndex() {
		return nameIndex;
	}

	public void setNameIndex(int nameIndex) {
		this.nameIndex = nameIndex;
	}

	@Override
	public void analysis(DataInputStream dataIn) throws IOException {
		dataIn.readUnsignedShort();
	}

}
