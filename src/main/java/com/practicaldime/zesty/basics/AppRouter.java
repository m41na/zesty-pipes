package com.practicaldime.zesty.basics;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.practicaldime.zesty.router.RequestAttrs;
import com.practicaldime.zesty.router.Route;
import com.practicaldime.zesty.router.RouteSearch;
import com.practicaldime.zesty.router.Router;

public class AppRouter implements Router{

	private final Router routeTree;
	
	public AppRouter(Router routeTree) {
		super();
		this.routeTree = routeTree;
	}
	
	public RouteSearch search(HttpServletRequest request) {
		RequestAttrs search = new RequestAttrs();
		search.url = request.getRequestURI();
        search.method = request.getMethod();
        for(Enumeration<String> keys = request.getHeaderNames(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            search.headers.put(key, request.getHeader(key));
        }
        return searchRoute(search);
	}
	
	public RouteSearch searchRoute(RequestAttrs requestAttrs) {
		RouteSearch input = new RouteSearch(requestAttrs);
		this.accept(input);
		return input;
	}

	@Override
	public void accept(RouteSearch input) {
		routeTree.accept(input);
	}

	@Override
	public void addRoute(Route route) {
		routeTree.addRoute(route);
	}
}
