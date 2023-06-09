package com.ecp.jces.jctool.util;

public class ByteArrayUtil {

  public static void writeInt(int value, byte[] data, int index) {
    if (index + 4 > data.length) {
      return;
    }

    data[index] = (byte) ((value >> 24) & 0xFF);
    data[index + 1] = (byte) ((value >> 16) & 0xFF);
    data[index + 2] = (byte) ((value >> 8) & 0xFF);
    data[index + 3] = (byte) ((value) & 0xFF);
  }

  public static void writeShort(short value, byte[] data, int index) {
    if (index + 2 > data.length) {
      return;
    }

    data[index] = (byte) ((value >> 8) & 0xFF);
    data[index + 1] = (byte) ((value) & 0xFF);
  }

  public static void writeByte(byte value, byte[] data, int index) {
    if (index > data.length) {
      return;
    }

    data[index] = (byte) ((value) & 0xFF);
  }

  public static int readInt(byte[] data, int offset) {
    return (((data[offset + 0] & 0xFF) << 24) | ((data[offset + 1] & 0xFF) << 16)
        | ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF));
  }

  public static short readShort(byte[] data, int offset) {
    return (short) (((data[offset + 0] & 0xFF) << 8) | (data[offset + 1] & 0xFF));
  }

  public static int readUnsignShort(byte[] data, int offset) {
    return (((data[offset + 0] & 0xFF) << 8) | (data[offset + 1] & 0xFF)) & 0xFFFF;
  }

  public static byte[] toByteArray(int value) {
    byte[] data = new byte[4];
    data[0] = (byte) ((value >> 24) & 0xFF);
    data[1] = (byte) ((value >> 16) & 0xFF);
    data[2] = (byte) ((value >> 8) & 0xFF);
    data[3] = (byte) ((value) & 0xFF);
    return data;
  }

  public static boolean isEmpty(byte[] data) {
    if (data != null && data.length > 0) {
      return false;
    }
    return true;
  }
}
