package com.ecp.jces.file.util;

public class FileHelper {
    
    public static String hash(byte[] bytes) {
        return SM3Util.hash2Str(bytes);
    }
}
