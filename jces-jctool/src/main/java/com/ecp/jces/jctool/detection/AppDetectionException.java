package com.ecp.jces.jctool.detection;

public class AppDetectionException extends Exception {

    public AppDetectionException(String message) {
        super(message);
    }

    public AppDetectionException(String message, Throwable ex) {
        super(message, ex);
    }
}
