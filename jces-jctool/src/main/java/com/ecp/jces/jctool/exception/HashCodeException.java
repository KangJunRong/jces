package com.ecp.jces.jctool.exception;

public class HashCodeException extends RuntimeException {

    public HashCodeException(String msg) {
        super(msg);
    }

    public HashCodeException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
