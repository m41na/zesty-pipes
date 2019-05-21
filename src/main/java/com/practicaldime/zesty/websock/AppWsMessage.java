package com.practicaldime.zesty.websock;

public class AppWsMessage {

	public String type;
    public String from;
    public String dest;
    public String role;
    public String time;
    public Object data;
    public String error;
    
    public AppWsMessage(String type, String from, String dest, String role, String time, Object data) {
    	this.type = type;
        this.from = from;
        this.dest = dest;
        this.role = role;
        this.time = time;
        this.data = data;
    }
    
    public AppWsMessage(String dest, String role, String time, String error) {
    	this.type = "error";
        this.from = "server";
        this.dest = dest;
        this.role = role;
        this.time = time;
        this.error = error;
    }

	@Override
	public String toString() {
		return "AppWsMessage [type=" + type + ", from=" + from + ", dest=" + dest + ", role=" + role + ", time=" + time
				+ ", data=" + data + ", error=" + error + "]";
	}
}
