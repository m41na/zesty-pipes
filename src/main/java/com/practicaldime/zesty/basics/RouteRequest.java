package com.practicaldime.zesty.basics;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import com.practicaldime.zesty.router.RouteSearch;

public interface RouteRequest {
    
    String protocol();
    
    boolean secure();
    
    String hostname();
    
    String ip();
    
    String path();
    
    String param(String name);
    
    Map<String, String> pathParams();
    
    String query();
    
    String header(String name);
    
    <T>T attribute(String name, Class<T> type);
    
    boolean error();
    
    String message();
    
    long upload(String dest);
    
    long capture();
    
    byte[] body();
    
    <T>T body(Class<T> type);
    
    <T>T body(BodyReader<T> provider);
    
    Cookie[] cookies();
    
    RouteSearch route();
    
    void route(RouteSearch route);
    
    HttpSession session(boolean create);
}
