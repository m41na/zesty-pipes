package com.practicaldime.zesty.servlet;

public interface MiddlewareCursor<T extends Middleware<T>> {

	Middleware<T> next();
	
	Middleware<T> prev();
	
	Middleware<T> current();
	
	boolean reset();
}
