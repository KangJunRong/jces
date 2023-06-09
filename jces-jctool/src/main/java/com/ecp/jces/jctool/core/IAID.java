package com.ecp.jces.jctool.core;


public interface IAID
{
    public abstract boolean isValid();

    public abstract byte[] getAid();

    public abstract int getPixOffset();

    public abstract byte[] getRid();

    public abstract boolean aidEquals(IAID iaid);

    public abstract boolean ridEquals(IAID iaid);

    public abstract boolean equals(Object obj);

    public abstract String getString();

    public abstract String toString(int i);
}
