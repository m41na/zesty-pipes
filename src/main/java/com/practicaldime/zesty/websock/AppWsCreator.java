package com.practicaldime.zesty.websock;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class AppWsCreator implements WebSocketCreator{
    
    private final AppWsProvider provider;

    public AppWsCreator(AppWsProvider provider) {
        this.provider = provider;
    }
    
    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        return provider.provide();
    }
}
