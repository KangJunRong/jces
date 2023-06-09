package com.ecp.jces.jctool.detection.model;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class BaseConstant {
	
	private int tag;
	
	public abstract void analysis(DataInputStream dataIn) throws IOException;

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
	
	public static BaseConstant newInstance(DataInputStream dataIn) throws IOException {
		try {
			BaseConstant bc;
			int tag = dataIn.readUnsignedByte();
			
			switch (tag) {
			case 13: //constant package
				bc = new PackageConstant();
				break;
			case 7: // constant classref
				bc = new ClassrefConstant();
				break;
			case 3: // constant integer
				bc = new IntegerConstant();
				break;
			case 1: // constant utf8
				bc = new Utf8Constant();
				break;

			default:
				bc = null;
				break;
			}
			
			return bc;
		} catch (IOException e) {
			throw e;
		}
	}

}
