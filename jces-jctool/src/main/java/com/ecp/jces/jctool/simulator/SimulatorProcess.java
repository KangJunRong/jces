package com.ecp.jces.jctool.simulator;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecp.jces.jctool.util.StringUtil;

public class SimulatorProcess {

	public static final String PARAM_PORT_KEY = "port";
	public static final String PARAM_EVENT_PORT_KEY = "eventPort";
	public static final String PARAM_DEBUG_PORT_KEY = "debugPort";

	private String name;
	private String path;
	private JcesConnectionImpl conn;

	private String port;
	private String eventPort;
	private String dport;
	private String[] cmdLine;
	
	private VirtualReader vReader;

	private Process process;

	public SimulatorProcess(String path, Map<String, String> params) {
		this.path = path;

		this.port = params.get(PARAM_PORT_KEY);
		this.eventPort = params.get(PARAM_EVENT_PORT_KEY);
		this.dport = params.get(PARAM_DEBUG_PORT_KEY);
	}

	public String getName() {
		return name;
	}

	public void init() throws Exception {
		if (StringUtil.isEmpty(dport) || StringUtil.isEmpty(port) || StringUtil.isEmpty(eventPort) ) {
			throw new Exception("port is Null!");
		}

		this.name = "Jces:" + dport;

		
		vReader = new VirtualReader(Integer.parseInt(this.port), Integer.parseInt(this.eventPort));
		vReader.init();
		
//		conn = new JcesConnectionImpl(vReader);
//
//		if (!conn.isOpen()) {
//			throw new Exception("Share memory init error!");
//		}

		
	}

	public Process exec() throws IOException {

//		if (!conn.isOpen()) {
//			throw new IOException("Simulation could not be launched.");
//		}

		List<String> args = new ArrayList<>();
		args.add(this.path);
		args.add("-port");
		args.add(String.valueOf(port));
		args.add("-eport");
		args.add(String.valueOf(eventPort));
//		args.add("-javadebug");
		args.add("-dport");
		args.add(String.valueOf(dport));

		this.cmdLine = (String[])toArray(args, String.class);
		process = Runtime.getRuntime().exec(this.cmdLine);

		return process;
	}

//	public JcesConnectionImpl getConn() {
//		return conn;
//	}

	public VirtualReader getVirtualReader() {
		return vReader;
	}
	
	public String[] getCmdLine() {
		return this.cmdLine;
	}

	public static Object[] toArray(Collection l, Class c) {
		if (l != null)
			return l.toArray((Object[]) Array.newInstance(c, l.size()));
		else
			return (Object[]) Array.newInstance(c, 0);
	}

	public void destroy() {
		if (process != null) {
			process.destroy();
		}
	}
}
