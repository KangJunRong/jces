package com.ecp.jces.jctool.core;

import java.util.List;

public interface IKeySet {
	

	public boolean addKey(IKey key) ;
	
	public List getKeyList();
	
	public IKey getKey(int id);
	
	public int getKeySet();
	
	public String getKeyType();
}
