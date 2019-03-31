package com.practicaldime.zesty.view;

@FunctionalInterface
public interface ViewEngineFactory {
	
	ViewEngine engine(String view, String assets, String suffix);
}
