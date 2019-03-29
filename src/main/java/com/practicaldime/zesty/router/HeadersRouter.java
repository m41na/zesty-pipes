package com.practicaldime.zesty.router;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HeadersRouter implements Router{

	private List<Route> routes = new ArrayList<>();
	
	@Override
	public void accept(RouteSearch input) {
		List<Route> pool = new ArrayList<>(routes);
		for(Iterator<Route> iter = pool.iterator(); iter.hasNext();) {
			Route mapping = iter.next();
			//is 'content-type' declared in mapping route?
			String contentTypeHeader = input.requestAttrs.getHeader("Content-Type");
			if(mapping.contentType != null && mapping.contentType.trim().length() > 0) {
				if(mapping.contentType.equals("*")) {
					continue;
				}
				else if(contentTypeHeader != null) {
					if(!contentTypeHeader.contains(mapping.contentType)) {
						iter.remove();
						continue;
					}
				}
				else {
					iter.remove();
					continue;
				}
			}
			else if (mapping.contentType != null && mapping.contentType.equals("")) {
				continue;
			}
			else if(contentTypeHeader != null && contentTypeHeader.trim().length() > 0) {
				iter.remove();
				continue;
			}
			//is 'accept' declared in mapping route?
			String acceptHeader = input.requestAttrs.getHeader("Accept");
			if(mapping.accept != null && mapping.accept.trim().length() > 0) {
				if(mapping.accept.equals("*")) {
					continue;
				}
				else if(acceptHeader != null) {
					if(!acceptHeader.contains(mapping.accept)) {
						iter.remove();
						continue;
					}
				}
				else {
					iter.remove();
					continue;
				}
			}
			else if(mapping.accept != null && mapping.accept.equals("")) {
				continue;
			}
			else if(acceptHeader != null && acceptHeader.trim().length() > 0) {
				iter.remove();
				continue;
			}
			//are there headers in the mapping route that match the inputs?
			if(!mapping.headers.isEmpty()) {
				Map<String, String> headers = mapping.headers;
				for(String key : headers.keySet()) {
					if(input.requestAttrs.headers.containsKey(key)) {
						if(!input.requestAttrs.getHeader(key).contains(headers.get(key))) {
							iter.remove();
							break;
						}
					}
				}
			}
		}
		//set first matched route if it exists in the input object
		if(pool.size() > 1) {
			throw new RuntimeException("Matched more than one route. You need more specificity with these matched routes -> " + pool.toString());
		}
		else if(pool.size() == 0) {
			throw new RuntimeException("No match was matched for against incoming inputs -> " + input.requestAttrs.toString());
		}
		else{
			input.result = pool.get(0);
		}
	}

	@Override
	public void addRoute(Route route) {
		this.routes.add(route);
	}
}
