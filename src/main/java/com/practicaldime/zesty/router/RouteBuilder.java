package com.practicaldime.zesty.router;

import java.util.Collections;
import java.util.Map;

public class RouteBuilder {

	public static Route create(String rid, String path, String method, String accept, String contentType, Map<String, String> headers) {
		Route route = new Route();
		route.rid = rid;
		route.path = path;
		route.method = method;
		route.accept = accept = accept != null && accept.trim().length() > 0? accept : null;
		route.contentType = contentType != null && contentType.trim().length() > 0? contentType : null;
		route.headers = headers;
		return route;
	}
	
	public static Route create(String rid, String path, String method, String accept, String contentType) {
		return create(rid, path, method, accept, contentType, Collections.emptyMap());
	}
	
	public static Route create(String rid, String path, String method, String accept) {
		return create(rid, path, method, accept, null, Collections.emptyMap());
	}
	
	public static Route create(String rid, String path, String method) {
		return create(rid, path, method, null, null, Collections.emptyMap());
	}
}
