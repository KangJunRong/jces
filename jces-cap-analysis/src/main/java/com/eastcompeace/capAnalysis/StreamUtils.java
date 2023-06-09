package com.eastcompeace.capAnalysis;

import java.io.DataInputStream;
import java.io.IOException;

public class StreamUtils {
	  public static byte[] dataRead(DataInputStream dataIn, int len) throws IOException {
	       byte[] bytes = new byte[len];

	       for (int i = 0; i < len; i++) {
	           bytes[i] = dataIn.readByte();
	       }

	       return bytes;
	   }
}
