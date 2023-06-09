package com.ecp.jces.jctool.simulator;

import java.util.HashMap;
import java.util.Map;

public class SimulatorManager {
	
	private static final Map<String, SimulatorProcess> simulatorMap = new HashMap<>();
	
	public static SimulatorProcess newInstance(String path, Map<String, String> param) throws Exception {
		SimulatorProcess sp = new SimulatorProcess(path, param);
		
		sp.init();
		
		simulatorMap.put(sp.getName(), sp);
		return sp;
	}
	
	public static SimulatorProcess get(String key) {
		return simulatorMap.get(key);
	}
	
	public static void remove(String key) {
		SimulatorProcess sp = simulatorMap.remove(key);

		if (sp != null) {
			sp.destroy();;
		}
	}

}
