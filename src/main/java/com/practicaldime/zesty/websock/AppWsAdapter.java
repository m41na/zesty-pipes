package com.practicaldime.zesty.websock;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Not dest be used directly as it has no business logic. Instead, use it dest wrap around an implementation of
 * AppWsListener, where you can handle your, for instance AppWsEvents specific business requirements
 * @author Mainas
 *
 */
public abstract class AppWsAdapter extends WebSocketAdapter implements AppWsListener{

    private static final Logger LOG = LoggerFactory.getLogger(AppWsAdapter.class);

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        try {
            onConnect();
        } catch (IOException ex) {
            LOG.error("onWebSocketConnect () error", ex);
        }
    }

    @Override
    public void onWebSocketText(String json) {
        try {
            onString(json);
        } catch (IOException ex) {
            LOG.error("onWebSocketText() error", ex);
        }
        super.onWebSocketText(json);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
            LOG.warn("Socket Closed: {}", reason);
            onClose(statusCode, reason);
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    		LOG.error("Socket error", cause);
            onError(cause);
        super.onWebSocketError(cause);
    }

    @Override
    public void onBinary(byte[] payload, int offset, int len) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
	public <T>T sessionId() {
		return (T)idStrategy().apply(getSession());
	}

    @Override
    public void sendString(String message) throws IOException {
        getRemote().sendString(message);
    }

    @Override
    public void sendPartial(String message, Boolean isLast) throws IOException {
        getRemote().sendPartialString(message, isLast);
    }

    @Override
    public void close() {
        getSession().close();
    }

    protected void log(String message) {
    	log("info", message);
    }
    
    protected void log(String level, String message, Object...args) {
    	switch(level) {
	    	case "error": {
	    		LOG.error(message, args);
	    		break;
	    	}
	    	case "warn": {
	    		LOG.warn(message, args);
	    		break;
	    	}
	    	case "info": {
	    		LOG.info(message, args);
	    		break;
	    	}
	    	case "debug": {
	    		LOG.debug(message, args);
	    		break;
	    	}
	    	case "trace": {
	    		LOG.trace(message, args);
	    		break;
	    	}
	    	default: {
	    		System.out.println(message);
	    		break;
	    	}
    	}
    }
}
