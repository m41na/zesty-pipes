package com.practicaldime.zesty.basics;

import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.Map;

public interface RouteResponse {
    
    void header(String header, String value);
    
    void templates(String folder);
    
    void context(String ctx);
    
    void status(int status);
    
    void sendStatus(int status);
    
    void ok(Object payload);
    
    void accepted();
    
    void send(String payload);
    
    void send(byte[] payload);
    
    void send(ByteBuffer payload);
    
    void json(Object payload);
    
    void jsonp(Object payload);
    
    <T>void xml(Object payload, Class<T> template);
    
    <T>void content(T payload, BodyWriter<T> writer);
    
    void render(String template, Map<String, Object>  model);
    
    void next(String path);
    
    void redirect(String path);
    
    void redirect(int status, String path);
    
    void type(String mimetype);
    
    void cookie(String name, String value);
    
    void attachment(String filename);
    
    void download(String path, String filename, String mimeType, HandlerStatus status);
    
    byte[] getContent();
    
    String readContent(String folder, String file);
    
    Reader getReader(String folder, String file);
}
