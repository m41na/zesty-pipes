package com.practicaldime.zesty.view.string;

import java.io.IOException;
import java.util.Map;

import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.string.ViewProcessor.Lookup;

public class DefaultViewEngine implements ViewEngine{

	private static DefaultViewEngine instance;
    private final ViewProcessor view;
	private final String templateDir;
	private final String templateExt = "*";
	private final ViewProcessor.Lookup strategy = Lookup.FILE;
	
	private DefaultViewEngine(String templateDir) {
    	super();
        this.templateDir = templateDir;
        this.view = new DefaultViewProcessor();
	}
	
	@Override
	public String templateDir() {
		return this.templateDir;
	}

	@Override
	public String templateExt() {
		return this.templateExt;
	}
	
	public static DefaultViewEngine create(String templateDir) throws IOException {
        if (instance == null) {
            synchronized (DefaultViewEngine.class) {
                instance = new DefaultViewEngine(templateDir);
            }
        }
        return instance;
    }
    
    public static DefaultViewEngine instance() throws IOException {
    	return instance;
    }
    
    public static ViewProcessor getProcessor() {
        return DefaultViewEngine.instance.view;
    }

	@Override
	public String merge(String template, Map<String, Object> model) throws Exception {	    	
    	return view.resolve(templateDir, template, strategy);
	}
}
