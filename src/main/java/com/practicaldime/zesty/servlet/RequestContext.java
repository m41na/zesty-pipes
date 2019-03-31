package com.practicaldime.zesty.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestContext implements HandlerContext{
	
	private HandlerRequest req;
	private HandlerResponse resp;
	private AsyncContext async;
	private Map<String, Object> context = new HashMap<>();
	//Handler context
	private boolean success;
	private Throwable failure;
	
	public RequestContext(HttpServletRequest req, HttpServletResponse resp, AsyncContext async) {
		super();
		this.req = new HandlerRequest(req);
		this.resp = new HandlerResponse(resp);
		this.async = async;
	}

	public HandlerRequest getReq() {
		return req;
	}

	public HandlerResponse getResp() {
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

	@Override
	public boolean isSuccessfull() {
		return this.success;
	}

	@Override
	public void setSuccessfull(boolean success) {
		this.success = success;
	}

	@Override
	public Throwable getFailure() {
		return this.failure;
	}

	@Override
	public void setFailure(Throwable th) {
		this.failure = th;
	}
}
