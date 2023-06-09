package com.ecp.jces.jctool.exception;

public class ExpAnalysisException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExpAnalysisException() {
		super();
	}
	
	public ExpAnalysisException(String message) {
		super(message);
	}
	
	public ExpAnalysisException(String message, Throwable ex) {
		super(message, ex);
	}
}
