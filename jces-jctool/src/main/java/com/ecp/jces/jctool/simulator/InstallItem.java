package com.ecp.jces.jctool.simulator;

import java.util.List;

public class InstallItem {

	private String packageAid; //包AID，或者 exe load file AID
	private String appletAid; //应用AID，或者 exe module AID
	private String instanceAid; //实例AID

	private int newObjectCount; // install过程new 对象的实际数量
	private int newObjectSpace; // install过程new 对象的实际空间

	private int newArrayCount; // install过程new 数组的实际数量
	private int newArraySpace; // install过程new 数组的实际空间

	private int dtrSpace; //memory_dtr_size
	private int rtrSpace; //memory_rtr_size
	private int nvmSpace; //memory_code_size

	private List<String> eventList; //移动禁用事件列表

	public String getPackageAid() {
		return packageAid;
	}

	public void setPackageAid(String packageAid) {
		this.packageAid = packageAid;
	}

	public String getAppletAid() {
		return appletAid;
	}

	public void setAppletAid(String appletAid) {
		this.appletAid = appletAid;
	}

	public String getInstanceAid() {
		return instanceAid;
	}

	public void setInstanceAid(String instanceAid) {
		this.instanceAid = instanceAid;
	}

	public int getNewObjectCount() {
		return newObjectCount;
	}

	public void setNewObjectCount(int newObjectCount) {
		this.newObjectCount = newObjectCount;
	}

	public int getNewObjectSpace() {
		return newObjectSpace;
	}

	public void setNewObjectSpace(int newObjectSpace) {
		this.newObjectSpace = newObjectSpace;
	}

	public int getNewArrayCount() {
		return newArrayCount;
	}

	public void setNewArrayCount(int newArrayCount) {
		this.newArrayCount = newArrayCount;
	}

	public int getNewArraySpace() {
		return newArraySpace;
	}

	public void setNewArraySpace(int newArraySpace) {
		this.newArraySpace = newArraySpace;
	}

	public int getDtrSpace() {
		return dtrSpace;
	}

	public void setDtrSpace(int dtrSpace) {
		this.dtrSpace = dtrSpace;
	}

	public int getRtrSpace() {
		return rtrSpace;
	}

	public void setRtrSpace(int rtrSpace) {
		this.rtrSpace = rtrSpace;
	}

	public int getNvmSpace() {
		return nvmSpace;
	}

	public void setNvmSpace(int nvmSpace) {
		this.nvmSpace = nvmSpace;
	}

	public int getAllCount() {
		return newObjectCount + newArrayCount;
	}
	
	public int getAllSpace() {
		return newObjectSpace + newArraySpace + dtrSpace + rtrSpace + nvmSpace;
	}

	public List<String> getEventList() {
		return eventList;
	}

	public void setEventList(List<String> eventList) {
		this.eventList = eventList;
	}
}
