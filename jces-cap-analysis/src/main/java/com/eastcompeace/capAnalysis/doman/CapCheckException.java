package com.eastcompeace.capAnalysis.doman;

public class CapCheckException extends Exception {

    public CapCheckException(String message) {
        super(message);
    }

    public CapCheckException(String message, Throwable ex) {
        super(message, ex);
    }
}
