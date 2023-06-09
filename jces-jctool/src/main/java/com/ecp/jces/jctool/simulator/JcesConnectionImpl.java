package com.ecp.jces.jctool.simulator;

import java.io.IOException;

import com.ecp.jces.jctool.util.ByteArrayUtil;
//import org.apache.log4j.Logger;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;

@SuppressWarnings("restriction")
public class JcesConnectionImpl {

  //private static final Logger log = Logger.getLogger(JcesConnectionImpl.class);
	
  public static final int FILE_MAP_ALL_ACCESS = 0xf001f;

  protected static final int MIN_PACKET_LENGTH = 11;
  public static final String FILE_NAME = "Global\\JavaDebugFile";

  private static final long WAIT_TIME = 5;

  public static final byte MODE_IDLE = 0;
  public static final byte MODE_WORK_MASK = 1;
  public static final byte MODE_WORK = 1;
  public static final byte MODE_VM_WORK = 11;
  public static final byte MODE_VM_IDLE = 10;
  public static final byte MODE_JDI_WORK = 21;
  public static final byte MODE_JDI_IDLE = 20;

  public static final int FUNC_ID_SPECIAL = 0x8000;

  private HANDLE hMapFile;
  private Pointer memory;
  private boolean isOpen = false;

  private Pointer vmDataPointer;
  private Pointer stepPointer;
  private Pointer breakpointPointer;
  private Pointer cbFuncPointer;
  
  private VirtualReader vReader;

  public static final int OFFSET_HEAD = 1;
  public static final int OFFSET_MODE = 0;

  public static final int OFFSET_COUNT = 1;
  public static final int OFFSET_PACKET = 1;
  public static final int OFFSET_CONTENT = 5;

  public static final int VMDATA_BUF_SIZE = 2;

  public static final int STEP_SIZE = 24;
  public static final int STEP_MAX_COUNT = 1;
  public static final int STEP_BUF_SIZE = STEP_SIZE * STEP_MAX_COUNT + 5;

  public static final int BREAKPOINT_SIZE = 8;
  public static final int BREAKPOINT_MAX_COUNT = 128;
  public static final int BREAKPOINT_BUF_SIZE = BREAKPOINT_MAX_COUNT * BREAKPOINT_SIZE + 5;

  public static final int CB_BUF_SIZE = 0x20002;

  public static final int BUFFER_SIZE = VMDATA_BUF_SIZE + STEP_BUF_SIZE + BREAKPOINT_BUF_SIZE
      + CB_BUF_SIZE;

  public static final int P_OFFSET_VMDATA = 0;
  public static final int P_OFFSET_STEP = 2;
  public static final int P_OFFSET_BREAKPOINT = P_OFFSET_STEP + STEP_BUF_SIZE;
  public static final int P_OFFSET_CB = P_OFFSET_BREAKPOINT + BREAKPOINT_BUF_SIZE;

  /**
   * |---------------------------Share memory------------------------------------| |VMDAT( 0-1) |
   * STEP (2-1e) |BREAKPOINT( 1f-423 )|CallBack| |Mode - Head |Mode Count[4] CONTENTId[4] SIZE[4]
   * Depth[4] FP[4] STARTPC[4] ENDPC[4]|Mode Count[4][[id[4] pc[4] count[4]].. ]|424-- |
   */

  public JcesConnectionImpl(VirtualReader vReader) {
	this.vReader = vReader;
	
    // 创建共享内存

    if (hMapFile == null) {
      hMapFile = Kernel32.INSTANCE.CreateFileMapping(Kernel32.INVALID_HANDLE_VALUE, null,
          Kernel32.PAGE_READWRITE, 0, BUFFER_SIZE, "Global\\JavaDebugFile");// getFilename());
      if (hMapFile == null) {
        isOpen = false;
        return;
      }

      // WINBASEAPI PVOID WINAPI MapViewOfFile(HANDLE,DWORD,DWORD,DWORD,DWORD);
      memory = Kernel32.INSTANCE.MapViewOfFile(hMapFile, FILE_MAP_ALL_ACCESS, 0, 0, BUFFER_SIZE);
    }
    if (memory == null) {
      isOpen = false;
      return;
    }

    memory.setMemory(0, BUFFER_SIZE, (byte) 0x0);

    vmDataPointer = memory.share(P_OFFSET_VMDATA);
    stepPointer = memory.share(P_OFFSET_STEP);
    breakpointPointer = memory.share(P_OFFSET_BREAKPOINT);

    cbFuncPointer = memory.share(P_OFFSET_CB);

    isOpen = true;
  }

  public final void close() throws IOException {
	  
	vReader.destory();
	
    if (memory != null) {
      Kernel32.INSTANCE.UnmapViewOfFile(memory);
      memory = null;
    }

    if (hMapFile != null) {
      Kernel32.INSTANCE.CloseHandle(hMapFile);
      hMapFile = null;
    }
  }


  public boolean isOpen() {
    return isOpen;
  }


  public byte[] readPacket() throws IOException {
    byte[] data = null;
    byte mode;

    while (true) {
      mode = cbFuncPointer.getByte(OFFSET_MODE);

      if (mode == MODE_VM_IDLE) {
        cbFuncPointer.setByte(0, MODE_JDI_WORK);
        break;
      }

      try {
        Thread.sleep(WAIT_TIME);
      } catch (InterruptedException e) {
    	  //log.error(e.getMessage(), e);
      }
    }

    int len = cbFuncPointer.getInt(OFFSET_PACKET);

    data = cbFuncPointer.getByteArray(OFFSET_PACKET, len);
    cbFuncPointer.setByte(0, MODE_IDLE);
    return data;
  }

  public void writePacket(byte[] data) throws IOException {

    if (data == null || data.length < 4) {
      throw new IOException("Illegal argument.");
    }

    int len = ByteArrayUtil.readInt(data, 0);
    if (len < MIN_PACKET_LENGTH) {
      throw new IOException("The length is invalid.");
    }

    byte mode;
    while (true) {
      mode = cbFuncPointer.getByte(OFFSET_MODE);

      if (mode == MODE_IDLE) {
        cbFuncPointer.setByte(0, MODE_JDI_WORK);
        break;
      }

      try {
        Thread.sleep(WAIT_TIME);
      } catch (InterruptedException e) {
    	  //log.error(e.getMessage(), e);
      }
    }

    cbFuncPointer.write(OFFSET_PACKET, data, 0, data.length);
    cbFuncPointer.setByte(OFFSET_MODE, MODE_JDI_IDLE);
  }

  public void setVmStatus(byte status) {
    byte mode;
    while (true) {
      mode = vmDataPointer.getByte(OFFSET_MODE);

      if (mode == MODE_IDLE) {
        vmDataPointer.setByte(OFFSET_MODE, MODE_JDI_WORK);
        break;
      }

      try {
        Thread.sleep(WAIT_TIME);
      } catch (InterruptedException e) {
    	  //log.error(e.getMessage(), e);
      }
    }

    vmDataPointer.setByte(OFFSET_HEAD, status);
    vmDataPointer.setByte(OFFSET_MODE, MODE_IDLE);
  }

  public byte getVmStatus() {
    byte mode;
    while (true) {
      mode = vmDataPointer.getByte(OFFSET_MODE);

      if (mode == MODE_IDLE) {
        return vmDataPointer.getByte(OFFSET_HEAD);
      }

      try {
        Thread.sleep(WAIT_TIME);
      } catch (InterruptedException e) {
    	  //log.error(e.getMessage(), e);
      }
    }
  }

  private void breakpointWorkMode() {
    byte mode;
    while (true) {
      mode = breakpointPointer.getByte(OFFSET_MODE);

      if (mode == MODE_IDLE) {
        breakpointPointer.setByte(OFFSET_MODE, MODE_JDI_WORK);
        break;
      }

      try {
        Thread.sleep(WAIT_TIME);
      } catch (InterruptedException e) {
    	  //log.error(e.getMessage(), e);
      }
    }
  }

  private void breakpointIdleMode() {
    breakpointPointer.setByte(OFFSET_MODE, MODE_IDLE);
  }

  public final void addBreakpoint(int requestId, int pc) {
    breakpointWorkMode();
    int count = breakpointPointer.getInt(OFFSET_COUNT);
    if (count >= BREAKPOINT_MAX_COUNT) {
      return;
    }

    int offset = OFFSET_CONTENT + (count * BREAKPOINT_SIZE);
    breakpointPointer.setInt(offset, requestId);
    breakpointPointer.setInt(offset + 4, pc);
    count++;
    breakpointPointer.setInt(OFFSET_COUNT, count);

    breakpointIdleMode();
  }

  public final void clearBreakpoint(int requestId) {
    breakpointWorkMode();

    int count = breakpointPointer.getInt(OFFSET_COUNT);
    if (count >= BREAKPOINT_MAX_COUNT) {
      return;
    }

    int offset;
    int id;
    for (int i = 0; i < count; i++) {
      offset = OFFSET_CONTENT + (i * BREAKPOINT_SIZE);
      id = breakpointPointer.getInt(offset);

      if (requestId == id) {
        int src = offset + BREAKPOINT_SIZE;
        int size = (count - i - 1) * BREAKPOINT_SIZE;

        if ((i + 1) < count) {
          byte[] buf = breakpointPointer.getByteArray(src, size);
          breakpointPointer.write(offset, buf, 0, size);

          breakpointPointer.setMemory(offset + size, BREAKPOINT_SIZE, (byte) 0);
        } else {
          breakpointPointer.setMemory(offset, BREAKPOINT_SIZE, (byte) 0);
        }

        count--;
        breakpointPointer.setInt(OFFSET_COUNT, count);
        break;
      }
    }

    breakpointIdleMode();
  }

  public final void clearAllBreakpoint() {
    breakpointWorkMode();
    breakpointPointer.setMemory(OFFSET_COUNT, BREAKPOINT_SIZE * BREAKPOINT_MAX_COUNT + 4, (byte) 0);
    breakpointIdleMode();
  }

  private void stepWorkMode() {
    byte mode;
    while (true) {
      mode = stepPointer.getByte(OFFSET_MODE);

      if (mode == MODE_IDLE) {
        stepPointer.setByte(OFFSET_MODE, MODE_JDI_WORK);
        break;
      }

      try {
        Thread.sleep(WAIT_TIME);
      } catch (InterruptedException e) {
    	  //log.error(e.getMessage(), e);
      }
    }
  }

  private void stepIdleMode() {
    stepPointer.setByte(OFFSET_MODE, MODE_IDLE);
  }

  public final void addStep(int requestId, int size, int depth, int fp, int startPC, int endPc) {
    stepWorkMode();

    int offset = OFFSET_CONTENT;
    stepPointer.setInt(offset, requestId);
    offset += 4;

    stepPointer.setInt(offset, size);
    offset += 4;

    stepPointer.setInt(offset, depth);
    offset += 4;

    stepPointer.setInt(offset, fp);
    offset += 4;

    stepPointer.setInt(offset, startPC);
    offset += 4;

    stepPointer.setInt(offset, endPc);
    offset += 4;

    stepPointer.setInt(OFFSET_COUNT, STEP_MAX_COUNT);
    stepIdleMode();
  }

  public final void clearStep() {
    stepWorkMode();
    stepPointer.setMemory(OFFSET_COUNT, STEP_SIZE * STEP_MAX_COUNT + 4, (byte) 0);
    stepIdleMode();
  }

  public final void clearAllStep() {
    stepWorkMode();
    stepPointer.setMemory(OFFSET_COUNT, STEP_SIZE * STEP_MAX_COUNT + 4, (byte) 0);
    stepIdleMode();
  }
}
