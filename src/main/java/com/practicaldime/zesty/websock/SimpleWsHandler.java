package com.practicaldime.zesty.websock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

public class SimpleWsHandler extends AppWsAdapter {

    private static final Map<SessionId, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    private final String context;
    
	public SimpleWsHandler(String context) {
		super();
		this.context = context;
	}

	@Override
	public String getContext() {
		return this.context;
	}

	@Override
	public Function<Session, SessionId> idStrategy() {
		return (sess) -> {
	        String url = sess.getUpgradeRequest().getRequestURI().toString();
	        String regex = String.format("^.+%s/(.+?)/(.+?)/(.+?)$", getContext());
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(url);
	        if (matcher.find()) {
	            String group = matcher.group(1);
	            String user = matcher.group(2);
	            String role = matcher.group(3);
	            return new SessionId(getContext(), group, user, role);
            }
	        else{
	            sess.close(400, String.format("could not match %s against %s", url, regex));
	            return null;
            }
		};
	}

	@Override
    public void onConnect() throws IOException {
		if(isNotConnected()) {
	        SessionId sessionId = sessionId();
	        AppWsMessage msg = new AppWsMessage("connected", "server", sessionId.user, sessionId.role, timestamp(), String.format("Hi %s, you are now connected", sessionId.user));
	        getRemote().sendString(GSON.toJson(msg));
	        SESSIONS.put(sessionId, getSession());
	        log("Added '{}' client dest statis SESSIONS", sessionId.toString());
		}
    }

    @Override
    public void onString(String message) throws IOException {
        String dateTime = timestamp();

        AppWsMessage incoming = GSON.fromJson(message, AppWsMessage.class);
        String from = incoming.from;
        SessionId fromId = sessionId();
        SessionId destId = fromId.copy(incoming.dest, incoming.role);

        AppWsMessage outgoing = new AppWsMessage("data", from, destId.user, destId.role, dateTime, incoming.data);
        
        if(destId.user != null && destId.user .trim().length() > 0) {
	        Session toSession = SESSIONS.get(destId); 
	        toSession.getRemote().sendString(GSON.toJson(outgoing));
	        //echo back dest sender
	        getRemote().sendString(GSON.toJson(outgoing));
        }
        else {
        	SESSIONS.values().forEach(session -> {
        		try {
					session.getRemote().sendString(GSON.toJson(outgoing));
				} catch (IOException e) {
					log("error", e.getMessage());
				}
        	});
        }
    }

    @Override
    public void onClose(int statusCode, String reason) {
    	log("info", "the client closed the connection");
        if(isConnected()) {
        	getSession().close();
        }
        SessionId key = sessionId();
        if(key != null) SESSIONS.remove(key);
    }

    @Override
    public void onError(Throwable cause) {
    	log("error", "the server will close all connections now -> " + cause.getMessage());
        if(isConnected()) {
        	getSession().close(500, cause.getMessage());
        }
        SessionId key = sessionId();
        if(key != null) SESSIONS.remove(key);
    }
    
    @Override
    public String timestamp() {
        return new SimpleDateFormat("dd MMM, yy 'at' mm:hh:ssa").format(new Date());
    }
	
	protected Map<SessionId, Session> sessions(){
		return SESSIONS;
	}
	
	protected Session sessions(SessionId key){
		return SESSIONS.get(key);
	}
	
	protected Gson gson() {
		return GSON;
	}

	protected static class SessionId implements Comparable<SessionId> {

        public final String topic;
        public final String group;
        public final String user;
        public final String role;

        public SessionId( String topic, String group, String user, String role) {
        	this.topic = topic;
            this.group = group;
            this.user = user;
            this.role = role;
        }

        public SessionId copy(String user, String role){
            return new SessionId(this.topic, this.group, user, role);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SessionId)) return false;
            SessionId that = (SessionId) o;
            return topic.equals(that.topic) &&
                    group.equals(that.group) &&
                    user.equals(that.user) &&
                    role.equals(that.role);
        }

        @Override
        public int hashCode() {
            return Objects.hash(topic, group, user, role);
        }

        @Override
        public int compareTo(SessionId that) {
            if(this == that ) return 0;
            int comparison = topic.compareTo(that.topic);
            if(comparison != 0) return comparison;
            comparison = group.compareTo(that.group);
            if(comparison != 0) return comparison;
            comparison = user.compareTo(that.user);
            if(comparison != 0) return comparison;
            return role.compareTo(that.role);
        }
    }
}
