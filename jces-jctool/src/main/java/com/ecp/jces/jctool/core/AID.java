// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   AID.java

package com.ecp.jces.jctool.core;


import com.ecp.jces.jctool.util.AssociativeHashCollection;
import com.ecp.jces.jctool.util.Hex;
import com.ecp.jces.jctool.util.Pointer;

import java.util.Arrays;

public class AID
    implements AssociativeHashCollection.HashCollectible
{
    public static class AIDParseException extends Exception
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

        private AIDParseException(Hex.HexParseException e)
        {
            errorCode = e.getErrorCode();
            offset = e.getOffset();
            partial = e.getPartial();
        }

        private AIDParseException(int errorCode, int offset, byte partial[])
        {
            this.errorCode = errorCode;
            this.offset = offset;
            this.partial = partial;
        }

        AIDParseException(Hex.HexParseException hexparseexception, AIDParseException aidparseexception)
        {
            this(hexparseexception);
        }

        AIDParseException(int i, int j, byte abyte0[], AIDParseException aidparseexception)
        {
            this(i, j, abyte0);
        }
    }


    private String rep;
    private byte aid[];
    private int pixOffset;
    public static final int PARSE_ERROR_SHORT_RID = -11;
    public static final int PARSE_ERROR_LONG_AID = -12;

    public AID(String aidString)
    {
        if(aidString == null)
            throw new NullPointerException();
        rep = aidString;
        try
        {
            Pointer p = new Pointer();
            aid = parse(aidString, p);
            pixOffset = ((int[])p.pointsTo())[5];
        }
        catch(AIDParseException _ex)
        {
            aid = null;
            pixOffset = 0;
        }
    }

    public String getString()
    {
        return rep;
    }

    public static byte[] parse(String aid)
        throws AIDParseException
    {
        return parse(aid, null);
    }

    public static AID create(byte ba[])
    {
        return new AID(Hex.toString(ba, 4));
    }

    public static byte[] parse(String aid, Pointer bap)
        throws AIDParseException
    {
        byte ba[];
        Pointer offpointer;
        offpointer = new Pointer();
        try
        {
            ba = Hex.toByteArray(aid, offpointer);
        }
        catch(Hex.HexParseException e)
        {
            throw new AIDParseException(e, null);
        }
        finally
        {
            if(bap != null)
                bap.bend(offpointer.pointsTo());
        }
        int offsets[] = (int[])offpointer.pointsTo();
        if(ba.length < 5)
            throw new AIDParseException(-11, aid.length(), ba, null);
        if(ba.length > 16)
            throw new AIDParseException(-12, offsets[16], ba, null);
        else
            return ba;
    }

    public boolean isValid()
    {
        return aid != null;
    }

    public byte[] getAid()
    {
        if(aid == null)
        {
            throw new IllegalStateException();
        } else
        {
            byte ans[] = new byte[aid.length];
            System.arraycopy(aid, 0, ans, 0, aid.length);
            return ans;
        }
    }

    public int getPixOffset()
    {
        if(aid == null)
            throw new IllegalStateException();
        else
            return pixOffset;
    }

    public byte[] getRid()
    {
        if(aid == null)
        {
            throw new IllegalStateException();
        } else
        {
            byte rid[] = new byte[5];
            System.arraycopy(aid, 0, rid, 0, 5);
            return rid;
        }
    }

    public boolean aidEquals(IAID other)
    {
        if(aid == null || !other.isValid())
            return false;
        else
            return Arrays.equals(aid, other.getAid());
    }

    public boolean ridEquals(IAID other)
    {
        if(aid == null || !other.isValid())
            return false;
        byte otheraid[] = other.getAid();
        for(int i = 0; i < 5; i++)
            if(aid[i] != otheraid[i])
                return false;

        return true;
    }

    public boolean equals(Object o)
    {
        if(o instanceof AID)
            return rep.equals(((AID)o).rep);
        else
            return false;
    }

    public int hashCode()
    {
        return rep.hashCode();
    }

    public int hcHashCode()
    {
        if(aid == null)
            return super.hashCode();
        else
            return Hex.toString(aid).hashCode();
    }

    public boolean hcEquals(Object other)
    {
        if(other instanceof AID)
            return aidEquals((IAID)other);
        else
            return false;
    }

    public String toString()
    {
        return getString();
    }

    public String toString(int format)
    {
        if(aid == null)
            return getString();
        else
            return Hex.toString(aid, 0, aid.length, format);
    }
}
