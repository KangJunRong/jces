package com.ecp.jces.jctool.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//import org.apache.log4j.Logger;


public class VirtualReader {
	
	//private static final Logger log = Logger.getLogger(VirtualReader.class);
	
	public static final int COMMAND_TYPE_RESET = 0;
	public static final int COMMAND_TYPE_ATR = 1;
	public static final int COMMAND_TYPE_TRANSMIT = 2;
	
	public static final int COMMAND_TYPE_RESET_VM_INFO = 0x80;
	public static final int COMMAND_TYPE_GET_VM_INFO = 0x81;
	public static final int COMMAND_TYPE_GET_EVENT_INFO = 0x82;
	
	public static final int EVENT_TYPE_REMOVED = 0;
	public static final int EVENT_TYPE_INSERTED = 1;
	
	
	private int port;
	private int eventPort;
	
	private ServerSocket server;
	private Socket socket;
	
	private InputStream is;
	private OutputStream os;
	
	private ServerSocket eventServer;
	private Socket eventSocket;
	
	private OutputStream eventOs;
	private InputStream eventIs;
	
	private boolean isConn;
	private boolean isEventConn;
	
	private BlockingQueue<EventPacket> eventPacketQueue = new LinkedBlockingQueue<>();
	
	public VirtualReader(int port, int eventPort) {
		this.port = port;
		
		this.eventPort = eventPort;
	}
	
	public void init() throws IOException {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					server = new ServerSocket(port);
					socket = server.accept();
					
					is = socket.getInputStream();
					os = socket.getOutputStream();
					isConn = true;
					
					eventServer = new ServerSocket(eventPort);
					eventSocket = eventServer.accept();

					
					eventIs = eventSocket.getInputStream();
					eventOs = eventSocket.getOutputStream();
					
					isEventConn = true;
					while (true) {
						int event = readInt(eventIs);

						if (event >= 0x80) {
							EventPacket packet = new EventPacket(event);
							
							int len = readInt(eventIs);
							packet.setLength(len);


							if (len > 0) {
								if (event == 0x82) {
									byte[] datas = new byte[len];

									byte buf[] = new byte[len];
									eventIs.read(buf, 0, 1);
									datas[0] = buf[0];

									eventIs.read(buf, 0, datas[0]);
									System.arraycopy(buf, 0, datas, 1, datas[0]);

									eventIs.read(buf, 0, 1);
									int offset = datas[0] + 1;
									datas[offset] = buf[0];
									eventIs.read(buf, 0, datas[offset]);
									System.arraycopy(buf, 0, datas, offset + 1, datas[offset]);
									packet.setData(datas);
								} else {
									byte[] datas = new byte[len];
									readBytes(eventIs, datas);
									packet.setData(datas);
								}

//								byte[] datas = new byte[len];
//								readBytes(eventIs, datas);
//								packet.setData(datas);
							}

							try {
								eventPacketQueue.put(packet);
							} catch (InterruptedException e) {
								//log.error(e.getMessage(), e);
							}
						} else {
							System.out.println("Virtual Reader Event: " + event);
						}
					}
					
					
				} catch (IOException e) {
					//log.error(e.getMessage(), e);
				}
				
			}
		}).start();
	}
	
	public byte[] atr(boolean reset) throws IOException {
		if (!isConn) {
			throw new IOException("Cart not conneted!");
		}
		
		byte[] commands = new byte[4];
		
		if (reset) {
			writeInt(commands, 0, COMMAND_TYPE_RESET);
		} else {
			writeInt(commands, 0, COMMAND_TYPE_ATR);
		}

		os.write(commands);
		
		int len = readInt(is);
		
		byte[] res;
		if (len != 0) {
			res = new byte[len];
			is.read(res);
		} else {
			res = new byte[0];
		}
		
		return res;
	}
	
	private void writeInt(byte[] data, int offset, int value) {
		data[offset + 3] = (byte) ((value >>> 24) & 0xFF);
		data[offset + 2] = (byte) ((value >>> 16) & 0xFF);
		data[offset + 1] = (byte) ((value >>> 8) & 0xFF);
		data[offset + 0] = (byte) ((value >>> 0) & 0xFF);
	}
	
	private int readInt(InputStream is) throws IOException {
		byte[] datas = new byte[4];
		
		if (is.read(datas) != 4) {
			throw new IOException("read int exception!");
		}
		
		int len = (((datas[3] & 0xFF) << 24) 
		          + ((datas[2] & 0xFF) << 16) 
		          + ((datas[1] & 0xFF) << 8) 
		          + ((datas[0] & 0xFF) << 0));
		
		return len;
	}
	
	private void readBytes(InputStream is, byte[] datas) throws IOException {
		
		if (is.read(datas) != datas.length) {
			throw new IOException("read int exception!");
		}
	}
	
	public byte[] transmit(byte[] apdu) throws IOException {
		
		if (!isConn) {
			throw new IOException("Cart not conneted!");
		}
		
		byte[] outs = new byte[4];
		writeInt(outs, 0, COMMAND_TYPE_TRANSMIT);
		os.write(outs);
		writeInt(outs, 0, apdu.length);
		os.write(outs);
		os.write(apdu);
		
		int len = readInt(is);
		byte[] res;
		if (len != 0) {
			res = new byte[len];
			is.read(res);
		} else {
			res = new byte[0];
		}
		
		return res;
	}
	
	
	public void destory() {
		
		isConn = false;
		
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				//log.error(e.getMessage(), e);
			}
		}
		
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				//log.error(e.getMessage(), e);
			}
		}
		
		if (eventSocket != null) {
			try {
				eventSocket.close();
			} catch (IOException e) {
				//log.error(e.getMessage(), e);
			}
		}
		
		if (eventServer != null) {
			try {
				eventServer.close();
			} catch (IOException e) {
				//log.error(e.getMessage(), e);
			}
		}
		
	}
	
	
	private byte[] vmCmd(int cmd, byte[] datas) throws IOException {
		if (!isEventConn) {
			throw new IOException("Cart not conneted!");
		}

		byte[] commands = null;
		if (datas != null && datas.length > 0) {
			commands = new byte[4 + datas.length];
			writeInt(commands, 0, cmd);

			System.arraycopy(datas, 0, commands, 4, datas.length);
		} else {
			commands = new byte[4];
			writeInt(commands, 0, cmd);
		}

		eventOs.write(commands);
		eventOs.flush();

		try {
			EventPacket packet = eventPacketQueue.poll(10, TimeUnit.SECONDS);

			if (packet != null) {
				return packet.getData();
			}
		} catch (InterruptedException e) {
			//log.error(e.getMessage(), e);
		}
		
		
		return null;
	}
	
	public byte[] resetVmInfo() throws IOException {
		return vmCmd(COMMAND_TYPE_RESET_VM_INFO, null);
	}
	
	public byte[] getVmInfo() throws IOException {
		byte[] datas = vmCmd(COMMAND_TYPE_GET_VM_INFO, null);
		
		return datas;
	}

	public byte[] getVmEventInfo(byte[] aid) throws IOException {
		if (aid == null || aid.length <= 0) {
			return null;
		}

		byte[] lv = new byte[aid.length + 1];
		lv[0] = (byte)(aid.length & 0xFF);
		System.arraycopy(aid, 0, lv, 1, aid.length);
		return vmCmd(COMMAND_TYPE_GET_EVENT_INFO, lv);
	}

	public boolean isConnection() {
		if (isConn && isEventConn) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		VirtualReader vReader = new VirtualReader(9001, 9002);
		
		try {
			vReader.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
