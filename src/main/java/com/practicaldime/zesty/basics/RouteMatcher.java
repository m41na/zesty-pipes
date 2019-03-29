package com.practicaldime.zesty.basics;

import org.eclipse.jetty.server.Request;

import com.practicaldime.zesty.router.Route;

import com.practicaldime.zesty.router.RouteSearch;

public interface RouteMatcher {

	void addRoute(Route route);
	
	RouteSearch match(Request search);
}
