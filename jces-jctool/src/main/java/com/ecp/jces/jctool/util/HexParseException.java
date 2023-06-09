// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   Hex.java

package com.ecp.jces.jctool.util;

// Referenced classes of package com.eastcompeace.jces.eclipse.internal.util:
//            Hex

public class HexParseException extends Exception
{

    private int errorCode;
    private int offset;
    private byte partial[];

    public int getErrorCode()
    {
        return errorCode;
    }

    public int getOffset()
    {
        return offset;
    }

    public byte[] getPartial()
    {
        return partial;
    }

    private static String errorMessage(int errorCode, int offset, String raw)
    {
        switch(errorCode)
        {
            case -2:
                return "Unexpected character '" +  raw.substring(offset, offset + 1) + "'";

            case -3:
                return "Unexpected end of string";
        }
        return "No further information available.";
    }

    private HexParseException(int errorCode, int offset, byte partial[], String raw)
    {
        super(errorMessage(errorCode, offset, raw));
        this.errorCode = errorCode;
        this.offset = offset;
        this.partial = partial;
    }

    HexParseException(int i, int j, byte abyte0[], String s, HexParseException hexparseexception)
    {
        this(i, j, abyte0, s);
    }
}
