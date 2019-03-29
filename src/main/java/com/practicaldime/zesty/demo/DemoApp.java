package com.practicaldime.zesty.demo;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.practicaldime.zesty.app.AppServer;

public class DemoApp {

	private static final Logger LOG = LoggerFactory.getLogger(DemoApp.class);

	public static void main(String... args) {
		
		int port = 8080;
		String host = "localhost";

		Map<String, String> props = new HashMap<>();
		props.put("appctx", "/app");
		props.put("assets", "");

		AppServer app = new AppServer(props);

		app.listen(port, host, (msg)-> LOG.info(msg));
	}
}
