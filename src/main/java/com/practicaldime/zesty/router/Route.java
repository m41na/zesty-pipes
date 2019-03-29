package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Route {

	public String rid;
	public String path;
	public String method;
	public String accept;
	public String contentType;
	public Map<String, String> headers = new HashMap<>();
    
    public Route(String...args){
        this.path =  args.length > 0? args[0] : null;
        this.method = args.length > 1? args[1] : null;
        this.accept = args.length > 2? args[2] : null;
        this.contentType = args.length > 3? args[3] : null;
    }

    public Route(String path, String method, String accept, String contentType) {
        this.path = path;
        this.method = method;
        this.accept = accept;
        this.contentType = contentType;
    }
    
    public void setId(){
        this.rid = "/" + UUID.randomUUID().toString();
    }

	@Override
	public String toString() {
		return "Route [path=" + path + ", method=" + method + ", accept=" + accept + ", contentType="
				+ contentType + ", headers=" + headers + "]";
	}
}
