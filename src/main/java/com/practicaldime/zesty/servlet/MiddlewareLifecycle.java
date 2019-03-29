package com.practicaldime.zesty.servlet;

public interface MiddlewareLifecycle {

	void onRegistered(String name);
	
	void onUnregistered(String name);
}
