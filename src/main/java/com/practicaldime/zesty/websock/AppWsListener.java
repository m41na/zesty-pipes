package com.practicaldime.zesty.websock;

import java.io.IOException;
import java.util.function.Function;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

public interface AppWsListener {
	
	String getContext();

    String timestamp();
	
	<T>T sessionId();
	
	<T>Function<Session, T> idStrategy();
	
	RemoteEndpoint getRemote();

    Session getSession();

    boolean isConnected();

    boolean isNotConnected();
    
    void onConnect() throws IOException;
    
    void onString(String message) throws IOException;
    
    void onBinary(byte[] payload, int offset, int len) throws IOException;
    
    void onClose(int statusCode, String reason);
    
    void onError(Throwable cause);
    
    void sendString(String message) throws IOException;
    
    void sendPartial(String message, Boolean isLast) throws IOException;
    
    void close();
}
