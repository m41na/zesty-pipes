package com.practicaldime.zesty.servlet;

public interface HandlerContext {

	boolean isSuccessfull();
	
	void setSuccessfull(boolean success);
	
	Throwable getFailure();
	
	void setFailure(Throwable th);
}
