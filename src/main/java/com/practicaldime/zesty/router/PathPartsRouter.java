package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;

public class PathPartsRouter implements Router{

	private Map<Integer, Router> routers = new HashMap<>();

	@Override
	public void accept(RouteSearch input) {
		String inputPath = input.requestAttrs.url;
		String path = inputPath != null? (inputPath.startsWith("/")? inputPath.substring(1) : inputPath) : null;
		String[] parts = path != null? path.split("/") : null;
		if(parts != null) {
			Integer length = Integer.valueOf(parts.length);
			if(routers.containsKey(length)) {
				routers.get(length).accept(input);
			}
		}
	}

	@Override
	public void addRoute(Route route) {
		String routePath = route.path;
		String path = routePath != null? (routePath.startsWith("/")? routePath.substring(1) : routePath) : null;
		String[] parts = path != null? path.split("/") : null;
		if(parts != null) {
			Integer length = Integer.valueOf(parts.length);
			if(!routers.containsKey(length)) {
				routers.put(length, new PathRegexRouter());
			}
			routers.get(length).addRoute(route);
		}
	}
}
