// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   Pointer.java

package com.ecp.jces.jctool.util;


public class Pointer
{

    private Object pointsTo;

    public Pointer(Object pointsTo)
    {
        this.pointsTo = pointsTo;
    }

    public Pointer()
    {
        this(null);
    }

    public Object pointsTo()
    {
        return pointsTo;
    }

    public void bend(Object obj)
    {
        pointsTo = obj;
    }
}
