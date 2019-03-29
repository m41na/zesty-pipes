package com.practicaldime.zesty.router;

public interface Router {

	void accept(RouteSearch input);
	
	void addRoute(Route route);
}
