// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   IKey.java

package com.ecp.jces.jctool.core;


public interface IKey
{

    public abstract int getKeyset();

    public abstract int getId();

    public abstract String getType();

    public abstract byte[] getKeymat();

    public abstract String getString();
}

