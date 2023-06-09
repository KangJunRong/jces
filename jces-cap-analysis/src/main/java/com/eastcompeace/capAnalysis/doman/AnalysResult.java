package com.eastcompeace.capAnalysis.doman;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnalysResult {
	
	private int result;
	private String msg;
	private Set<API> disabledApis;
	private Set<String> disabledApisMethodStrs;
	public AnalysResult(int result, String msg, Set<API> disabledApis) {
		super();
		this.result = result;
		this.msg = msg;
		this.disabledApis = disabledApis;
		this.disabledApisMethodStrs= getDisabledApisMethodStrs(disabledApis);
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Set<API> getDisabledApis() {
		return disabledApis;
	}
	public void setDisabledApis(Set<API> disabledApis) {
		this.disabledApis = disabledApis;
	}
	
	
	public Set<String> getDisabledApisMethodStrs() {
		return disabledApisMethodStrs;
	}
	private Set<String> getDisabledApisMethodStrs(Set<API> disabledApisIn) {
		Set<String> set  = new HashSet<>();
		for(API api:disabledApis) {
			String disableMethodString = api.getClassName()+" "+api.getMethodName();
			set.add(disableMethodString);
		}		
		return set;		
	}
	

}
