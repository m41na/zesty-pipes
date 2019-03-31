package com.practicaldime.zesty.view;

import java.util.Map;

public interface ViewBuilder {
        
    String getEngine();

    String getLayout();
    
    String getTitle();
    
    String getCharset();
    
    <T>T getContent();

    String getContentType();
    
    Map<String, Object> getModel();
    
    String mergeTemplate(String name, String markup);

    String getDestFile();
    
    void writeTemplate(String template, String content);
    
    String loadMarkup(String template);
    
    String[] getMetaTags();
    
    String[] getStyles();
    
    String[] getScripts(boolean top);
}
