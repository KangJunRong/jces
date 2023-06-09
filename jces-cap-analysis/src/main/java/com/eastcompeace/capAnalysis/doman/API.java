package com.eastcompeace.capAnalysis.doman;

import java.io.Serializable;

public class API implements Serializable{
	private String packageName;
	private String className;
	private String methodName;
    private String descriptor;
    private String versionNo;
    
    public API() {
    	
    }
    
	public API(String packageName, String className, String methodName, String descriptor) {
		super();
		this.packageName = packageName;
		this.className = className;
		this.methodName = methodName;
		this.descriptor = descriptor;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getDescriptor() {
		return descriptor;
	}
	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	
	
    
    
}
