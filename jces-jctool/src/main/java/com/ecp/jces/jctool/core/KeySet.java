package com.ecp.jces.jctool.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class KeySet implements IKeySet{
	
	private int keySet;
	private String type;
	private List keyList;
	
	public KeySet(int keySet, String type) {
		this.keySet = keySet; 
		this.type = type.toUpperCase();
		keyList = new ArrayList();
	}
	
	public boolean addKey(IKey key) {
		if (keySet == key.getKeyset() && type.equals(key.getType().toUpperCase())) {
			if (keyList.contains(key)) {
				keyList.remove(key);
			}
			
			keyList.add(key);
		}
		
		return false;
	}
	
	public List getKeyList() {
		Collections.sort(keyList, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				IKey key0 = (IKey)arg0;
				IKey key1 = (IKey)arg1;
				
				if (key0.getKeyset() == key1.getKeyset()) {
					return key0.getId() - key1.getId();
				}
				return key0.getKeyset() - key1.getKeyset();
			}
		});
		return keyList;
	}

	public IKey getKey(int id) {
		IKey key = new Key(this.keySet, id, this.type, new byte[0]); 
		
		int index = keyList.indexOf(key);
		if (index >= 0) {
			return (IKey)keyList.get(index);
		}
		
		return null;
	}

    public boolean equals(Object obj) {
    	if (obj instanceof KeySet) {
    		KeySet keySet = (KeySet) obj;
			
			if (this.keySet == keySet.keySet && this.type != null && keySet.type != null && this.type.equals(keySet.type.toUpperCase())) {
				return true;
			}
		}

    	return false;
    }

	public int getKeySet() {
		return this.keySet;
	}

	public String getKeyType() {
		return this.type;
	}
}
