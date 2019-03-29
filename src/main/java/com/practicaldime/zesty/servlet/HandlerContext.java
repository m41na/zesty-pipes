package com.practicaldime.zesty.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerContext {
	
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private AsyncContext async;
	private Map<String, Object> context = new HashMap<>();
	
	public HandlerContext(HttpServletRequest req, HttpServletResponse resp, AsyncContext async) {
		super();
		this.req = req;
		this.resp = resp;
		this.async = async;
	}

	public HttpServletRequest getReq() {
		return req;
	}

	public HttpServletResponse getResp() {
		return resp;
	}

	public AsyncContext getAsync() {
		return async;
	}
	
	public void set(String key, Object value) {
		context.put(key, value);
	}
	
	public <V>V get(String key, Class<V> type) {
		return type.cast(context.get(key));
	}
}
