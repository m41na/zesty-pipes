package com.practicaldime.zesty.router;

import java.util.EnumMap;
import java.util.Map;

public class MethodRouter implements Router{

	public static enum Method {POST, GET, PUT, DELETE, OPTIONS, HEAD, ALL};
	private Map<Method, Router> routers = new EnumMap<>(Method.class);
	
	public MethodRouter() {
		super();
		routers.put(Method.GET, new PathPartsRouter());
		routers.put(Method.PUT, new PathPartsRouter());
		routers.put(Method.POST, new PathPartsRouter());
		routers.put(Method.HEAD, new PathPartsRouter());
		routers.put(Method.DELETE, new PathPartsRouter());
		routers.put(Method.OPTIONS, new PathPartsRouter());
		routers.put(Method.ALL, new PathPartsRouter());
	}

	@Override
	public void accept(RouteSearch input) {
		String method = input.requestAttrs.method;
		Method type = method != null? Method.valueOf(method.toUpperCase()) : null;
		if(type != null) {
			this.routers.get(type).accept(input);
			//if no match if found for the specific request method, look into 'all' methods
			if(input.result == null) {
				this.routers.get(Method.ALL).accept(input);
			}
			//if a matching route is found, set the method value in the result
			if(input.result != null) {
				input.result.method = type.name();
			}
		}
	}

	@Override
	public void addRoute(Route route) {
		String method = route.method;
		Method type = method != null? Method.valueOf(method.toUpperCase()) : null;
		if(type != null) {
			this.routers.get(type).addRoute(route);
		}
	}
}
