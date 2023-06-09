// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   Key.java

package com.ecp.jces.jctool.core;


import com.ecp.jces.jctool.util.Hex;

public class Key
    implements IKey
{

    private int keyset;
    private int id;
    private String type;
    private byte keymat[];
    private String string;

    public Key(int keyset, int id, String type, byte keymat[])
    {
        this.keyset = keyset;
        this.id = id;
        this.type = type;
        this.keymat = keymat;
        string = toString(this);
    }

    public Key(String rep)
    {
        try
        {
            int idx;
            keyset = Integer.parseInt(rep.substring(0, idx = rep.indexOf(47)));
            id = Integer.parseInt(rep.substring(++idx, idx = rep.indexOf(47, idx)));
            type = rep.substring(++idx, idx = rep.indexOf(47, idx));
            keymat = Hex.toByteArray(rep.substring(++idx), null);
            string = toString(this);
        }
        catch(Exception e)
        {
            throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(e);
        }
    }

    public int getKeyset()
    {
        return keyset;
    }

    public int getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public byte[] getKeymat()
    {
        return keymat;
    }

    public String getString()
    {
        return string;
    }

    public String toString()
    {
        return getString();
    }

    private static String toString(Key key)
    {
        return Integer.toString(key.keyset) + '/' + Integer.toString(key.id) + '/' + key.type + '/' + Hex.toString(key.keymat, 0, key.keymat.length, 0);
    }
    
    public boolean equals(Object obj) {
    	if (obj instanceof Key) {
			Key key = (Key) obj;
			
			if (this.keyset == key.keyset && this.id == key.id && this.type != null && key.type != null && this.type.equals(key.type.toUpperCase())) {
				return true;
			}
		}

    	return false;
    }
}
