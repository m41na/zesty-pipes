package com.practicaldime.zesty.view.string;

public interface ViewProcessor {
	
	public enum Lookup { FILE, CLASSPATH, REMOTE, ANY };

	public String resolve(String templatePath, String template, Lookup strategy) throws Exception;
}
