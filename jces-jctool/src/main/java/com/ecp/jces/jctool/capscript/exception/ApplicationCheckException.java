package com.ecp.jces.jctool.capscript.exception;

public class ApplicationCheckException extends Exception {

    public ApplicationCheckException(String message) {
        super(message);
    }

    public ApplicationCheckException(String message, Throwable ex) {
        super(message, ex);
    }
}
