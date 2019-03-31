package com.practicaldime.zesty.view;

import java.util.Map;

public interface ViewEngine extends ViewTemplate{
    
    String merge(String template, Map<String, Object> model) throws Exception;
}
