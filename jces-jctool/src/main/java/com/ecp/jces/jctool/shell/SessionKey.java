package com.ecp.jces.jctool.shell;

import com.ecp.jces.jctool.core.IKey;

public class SessionKey {
	
	private IKey sEncKey = null;
	private IKey sMacKey = null;
	private IKey sDekKey = null;
	
	private byte encKey[] = null;
	private byte macKey[] = null;
	private byte dekKey[] = null;
	
	private byte icv[] = new byte[8];
	
	public SessionKey() {
		init();
	}
	
	public SessionKey(IKey sEncKey, IKey sMacKey, IKey sDekKey) {
		setStaticKey(sEncKey, sMacKey, sDekKey);
		init();
	}
	
	/**
	 * 设置静态key
	 */
	public void setStaticKey(IKey sEncKey, IKey sMacKey, IKey sDekKey) {
		this.sEncKey = sEncKey;
		this.sMacKey = sMacKey;
		this.sDekKey = sDekKey;
	}
	
	public void init() {
		encKey = new byte[16];
		macKey = new byte[16];
		dekKey = new byte[16];
		
		derivationData = new byte[16];
		icv = new byte[8];
	}

	public void genSessionKey() {
		
	}
	
	public IKey getsEncKey() {
		return sEncKey;
	}

	public void setsEncKey(IKey sEncKey) {
		this.sEncKey = sEncKey;
	}

	public IKey getsMacKey() {
		return sMacKey;
	}

	public void setsMacKey(IKey sMacKey) {
		this.sMacKey = sMacKey;
	}

	public IKey getsDekKey() {
		return sDekKey;
	}

	public void setsDekKey(IKey sDekKey) {
		this.sDekKey = sDekKey;
	}

	public byte[] getEncKey() {
		return encKey;
	}

	public void setEncKey(byte[] encKey) {
		this.encKey = encKey;
	}

	public byte[] getMacKey() {
		return macKey;
	}

	public void setMacKey(byte[] macKey) {
		this.macKey = macKey;
	}

	public byte[] getDekKey() {
		return dekKey;
	}

	public void setDekKey(byte[] dekKey) {
		this.dekKey = dekKey;
	}

	public byte[] getIcv() {
		return icv;
	}

	public void setIcv(byte[] icv) {
		this.icv = icv;
	}

	public byte[] getDerivationData() {
		return derivationData;
	}

	public void setDerivationData(byte[] derivationData) {
		this.derivationData = derivationData;
	}

	public byte derivationData[];
}
