// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   Hex.java

package com.ecp.jces.jctool.util;

import java.util.Stack;

// Referenced classes of package com.eastcompeace.jces.eclipse.internal.util:
//            Pointer

public class Hex
{
    public static class HexParseException extends Exception
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


    public static final int FORMAT_0102 = 0;
    public static final int FORMAT_1_2 = 1;
    public static final int FORMAT_0x01_0x02 = 2;
    public static final int FORMAT_01_02 = 3;
    public static final int FORMAT_TEXT = 4;
    private static final int PARSE_ERROR_NOERROR_DONE = -1;
    public static final int PARSE_ERROR_UNEXPECTED_CHAR = -2;
    public static final int PARSE_ERROR_UNEXPECTED_EOS = -3;
    public static final int PARSE_ERROR_USER_SPACE = -10;

    public Hex()
    {
    }

    public static String toString(byte a[], int off, int len, int format)
    {
        if(a == null)
            return "";
        if(format == 4)
        {
            StringBuffer buf = new StringBuffer();
            buf.append('|');
            for(int i = 0; i < len; i++)
            {
                byte b = a[i + off];
                if(b < 32 || b > 126 || b == 124)
                {
                    format = 0;
                    break;
                }
                buf.append((char)b);
            }

            return buf.toString();
        }
        StringBuffer b = new StringBuffer();
        for(int i = 0; i < len; i++)
        {
            if(format == 2)
                b.append("0x");
            if(format != 1 || (a[i + off] & 0xf0) != 0)
                b.append(Integer.toHexString((a[i + off] & 0xf0) >> 4));
            b.append(Integer.toHexString(a[i + off] & 0xf));
            if(format == 1 || format == 2 && i < len - 1)
                b.append(':');
            else
            if(format == 3 && i < len - 1)
                b.append(' ');
        }

        return b.toString();
    }

    public static String toString(byte a[], int off, int len)
    {
        return toString(a, off, len, 0);
    }

    public static String toString(byte a[])
    {
        return toString(a, 0, a.length);
    }

    public static String toString(byte a[], int format)
    {
        return toString(a, 0, a.length, format);
    }

    public static byte[] toByteArray(String hex, Pointer offpointer)
        throws HexParseException
    {
        char ca[] = hex.toCharArray();
        int state = 0;
        int clen = 0;
        boolean c_0_1 = true;
        Stack s_5_1 = new Stack();
        byte ba[] = new byte[ca.length];
        int oa[] = new int[ca.length];
        int off;
        for(off = 0; state >= 0; off++)
        {
            char ch = off >= ca.length ? '\0' : ca[off];
            switch(state)
            {
            default:
                break;

            case 0: // '\0'
                if(ch >= '1' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F')
                {
                    ba[clen] = (byte)(charToHex(ch) << 4);
                    oa[clen] = off;
                    state = 2;
                } else
                if(ch == '|')
                    state = 1;
                else
                if(ch == '0')
                {
                    oa[clen] = off;
                    state = 3;
                } else
                if(ch == 0)
                    state = -1;
                else
                if(!c_0_1 && (ch == '-' || ch == ':' || ch == ' '))
                    c_0_1 = true;
                else
                if(ch == '#')
                {
                    oa[clen] = off;
                    state = 5;
                } else
                if(ch == ')')
                {
                    if(s_5_1.empty())
                    {
                        state = -2;
                    } else
                    {
                        int loff = ((Integer)s_5_1.pop()).intValue();
                        ba[loff] = (byte)(clen - loff - 1 & 0xff);
                        state = 0;
                    }
                } else
                {
                    state = -2;
                }
                if(state != 0)
                    c_0_1 = false;
                break;

            case 1: // '\001'
                if(ch == '|')
                {
                    state = 0;
                    break;
                }
                if(ch == 0)
                {
                    state = -1;
                } else
                {
                    oa[clen] = off;
                    ba[clen++] = (byte)(ch & 0xff);
                }
                break;

            case 2: // '\002'
                if(ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F')
                {
                    ba[clen++] |= (byte)charToHex(ch);
                    state = 0;
                    break;
                }
                if(!c_0_1 && (ch == '-' || ch == ':' || ch == ' '))
                {
                    c_0_1 = true;
                    ba[clen] >>= 4;
                    ba[clen++] &= 0xf;
                    state = 0;
                } else
                {
                    state = ch != 0 ? -2 : -3;
                }
                break;

            case 3: // '\003'
                if(ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F')
                {
                    ba[clen++] |= (byte)charToHex(ch);
                    state = 0;
                    break;
                }
                if(ch == 'x')
                {
                    state = 4;
                    break;
                }
                if(!c_0_1 && (ch == '-' || ch == ':' || ch == ' '))
                {
                    c_0_1 = true;
                    ba[clen] >>= 4;
                    ba[clen++] &= 0xf;
                    state = 0;
                } else
                {
                    state = ch != 0 ? -2 : -3;
                }
                break;

            case 4: // '\004'
                if(ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F')
                {
                    ba[clen] = (byte)(charToHex(ch) << 4);
                    state = 2;
                } else
                {
                    state = ch != 0 ? -2 : -3;
                }
                break;

            case 5: // '\005'
                if(ch == '(')
                {
                    s_5_1.push(new Integer(clen++));
                    state = 0;
                } else
                {
                    state = ch != 0 ? -2 : -3;
                }
                break;
            }
        }

        if(!s_5_1.empty())
        {
            do
            {
                int loff = ((Integer)s_5_1.pop()).intValue();
                ba[loff] = (byte)(clen - loff - 1 & 0xff);
            } while(!s_5_1.empty());
            if(state == -1)
                state = -3;
        }
        while(!s_5_1.empty()) 
        {
            int loff = ((Integer)s_5_1.pop()).intValue();
            ba[loff] = (byte)(clen - loff - 1 & 0xff);
        }
        byte ansBa[] = new byte[clen];
        System.arraycopy(ba, 0, ansBa, 0, clen);
        if(offpointer != null)
        {
            int ansOa[] = new int[clen + 1];
            System.arraycopy(oa, 0, ansOa, 0, clen);
            ansOa[clen] = state != -1 ? off - 2 : ca.length;
            offpointer.bend(ansOa);
        }
        if(state == -1)
            return ansBa;
        else
            throw new HexParseException(state, off - 1, ansBa, hex, null);
    }

    private static int charToHex(char ch)
    {
        if(ch >= '0' && ch <= '9')
            return ch - 48 & 0xf;
        if(ch >= 'a' && ch <= 'f')
            return (ch - 97) + 10 & 0xf;
        if(ch >= 'A' && ch <= 'F')
            return (ch - 65) + 10 & 0xf;
        else
            throw new NumberFormatException("Unknown Hex Digit " + ch);
    }
}
