package com.practicaldime.zesty.websock;

import java.util.HashMap;
import java.util.Map;

public interface AppWsPolicy {

	String IDLE_TIMEOUT = "IDLE_TIMEOUT";
	String MAX_TEXT_MESSAGE_SIZE = "MAX_TEXT_MESSAGE_SIZE";
	String MAX_BINARY_MESSAGE_SIZE = "MAX_BINARY_MESSAGE_SIZE";
	String MAX_TEXT_MESSAGE_BUFFER_SIZE = "MAX_TEXT_MESSAGE_BUFFER_SIZE";
	String MAX_BINARY_MESSAGE_BUFFER_SIZE = "MAX_BINARY_MESSAGE_BUFFER_SIZE";

	Map<String, Long> getPolicy();
	
	static Map<String, Long> defaultConfig(){
		Map<String, Long> props = new HashMap<>();
		props.put(IDLE_TIMEOUT, 300000L);
		props.put(MAX_TEXT_MESSAGE_SIZE, Double.valueOf(0.5 * 1024 * 1024).longValue());
		props.put(MAX_BINARY_MESSAGE_SIZE, Double.valueOf(0.5 * 1024 * 1024).longValue());
		props.put(MAX_TEXT_MESSAGE_BUFFER_SIZE, Double.valueOf(1 * 1024 * 1024).longValue());
		props.put(MAX_BINARY_MESSAGE_BUFFER_SIZE, Double.valueOf(1 * 1024 * 1024).longValue());
		return props;
	}
}
