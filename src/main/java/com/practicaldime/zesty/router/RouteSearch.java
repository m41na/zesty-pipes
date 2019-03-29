package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;

public class RouteSearch {

	public final RequestAttrs requestAttrs;
	public final Map<String, String> pathParams = new HashMap<>();
	public Route result;
	
	public RouteSearch(RequestAttrs requestAttrs) {
		super();
		this.requestAttrs = requestAttrs;
	}
	
	public void visit(Router router) {
		router.accept(this);
	}
}
