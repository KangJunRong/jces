package com.ecp.jces.jctool.detection.model;

import java.io.DataInputStream;
import java.io.IOException;

public class PackageConstant extends BaseConstant {

	private int flags;
	private int nameIndex;
	private int minorVersion;
	private int majorVersion;
	private int aidLength;
	private byte[] aid;

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public void setNameIndex(int nameIndex) {
		this.nameIndex = nameIndex;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getAidLength() {
		return aidLength;
	}

	public void setAidLength(int aidLength) {
		this.aidLength = aidLength;
	}

	public byte[] getAid() {
		return aid;
	}

	public void setAid(byte[] aid) {
		this.aid = aid;
	}

	@Override
	public void analysis(DataInputStream dataIn) throws IOException {
		flags = dataIn.readUnsignedByte();
		nameIndex = dataIn.readUnsignedShort();
		minorVersion = dataIn.readUnsignedByte();
		majorVersion = dataIn.readUnsignedByte();
		aidLength = dataIn.readUnsignedByte();

		aid = new byte[aidLength];
		dataIn.read(aid);
	}

}
