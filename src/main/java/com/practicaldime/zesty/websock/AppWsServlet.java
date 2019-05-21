package com.practicaldime.zesty.websock;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class AppWsServlet extends WebSocketServlet {

	private static final long serialVersionUID = 1L;
	private final AppWsProvider provider;
	private final Map<String, Long> policy;

	public AppWsServlet(AppWsProvider provider) {
		this.provider = provider;
		this.policy = Collections.emptyMap();
	}

	public AppWsServlet(AppWsProvider provider, Map<String, Long> policy) {
		this.provider = provider;
		this.policy = policy;
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		if (policy.containsKey(AppWsPolicy.IDLE_TIMEOUT)) {
			factory.getPolicy().setIdleTimeout(policy.get(AppWsPolicy.IDLE_TIMEOUT));
		}
		if (policy.containsKey(AppWsPolicy.MAX_BINARY_MESSAGE_BUFFER_SIZE)) {
			factory.getPolicy().setMaxBinaryMessageBufferSize(policy.get(AppWsPolicy.MAX_BINARY_MESSAGE_BUFFER_SIZE).intValue());
		}
		if (policy.containsKey(AppWsPolicy.MAX_BINARY_MESSAGE_SIZE)) {
			factory.getPolicy().setMaxBinaryMessageSize(policy.get(AppWsPolicy.MAX_BINARY_MESSAGE_SIZE).intValue());
		}
		if (policy.containsKey(AppWsPolicy.MAX_TEXT_MESSAGE_BUFFER_SIZE)) {
			factory.getPolicy().setMaxTextMessageBufferSize(policy.get(AppWsPolicy.MAX_TEXT_MESSAGE_BUFFER_SIZE).intValue());
		}
		if (policy.containsKey(AppWsPolicy.MAX_TEXT_MESSAGE_SIZE)) {
			factory.getPolicy().setMaxTextMessageSize(policy.get(AppWsPolicy.MAX_TEXT_MESSAGE_SIZE).intValue());
		}
		// factory.register(AppWebSocket.class);
		factory.setCreator(new AppWsCreator(provider));
	}
}
