package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;

public class RequestAttrs {

	public String url;
	public String method;
	public Map<String, String> headers = new HashMap<>();
	
	public String getHeader(String name) {
		return headers.keySet().contains(name)? headers.get(name) : null;
	}

	@Override
	public String toString() {
		return "RequestAttrs [path=" + url + ", method=" + method + ", headers=" + headers + "]";
	}
}
