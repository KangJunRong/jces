package com.ecp.jces.jctool.shell.action;

public abstract class GpAction extends Action {

	
	/*
	protected byte[] genSessionKey(byte[] constant, byte[] seqCounter, IKey key) throws CmdProcessException {
		if (constant == null || constant.length != 2 || 
				seqCounter == null || seqCounter.length != 2 || getCurCad().encKey == null) 
			throw new CmdProcessException();
		
		byte[] data = new byte[16];
		System.arraycopy(constant, 0, data, 0, 2);
		System.arraycopy(seqCounter, 0, data, 2, 2);
		return Cryto.tripleDes(key.getKeymat(), data);
	}
	*/
	protected boolean compareByte(byte[] b1, byte[] b2) {
		if (b1 == null || b2 == null || b1.length != b2.length)
			return false;
		
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}
}
