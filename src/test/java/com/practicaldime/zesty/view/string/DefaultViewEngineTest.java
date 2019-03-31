package com.practicaldime.zesty.view.string;

import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.practicaldime.zesty.view.string.ViewProcessor.Lookup;

public class DefaultViewEngineTest {

	private String fileFolder = "src/test/resources/template/js";
	private String classpathFolder = "/template/js";
	private DefaultViewEngine engine;
	
	@Before
	public void setup() throws IOException {
		engine = DefaultViewEngine.create(fileFolder);
	}
	
	@Test
	public void testResolveFileLookup() throws Exception {
		String path = DefaultViewEngine.getProcessor().resolve(fileFolder, "index-test.js", Lookup.FILE);
		System.out.printf("*******path resolved: %s%n", path);
	}

	@Test
	public void testMerge() throws Exception {
		String merged = engine.merge("index-test.js", Collections.emptyMap());
		System.out.printf("*******merged output: %s%n", merged);
	}
	
	@Test
	public void testResolveClasspathLookup() throws Exception {
		String path = DefaultViewEngine.getProcessor().resolve(classpathFolder, "index-test.js", Lookup.CLASSPATH);
		System.out.printf("*******path resolved: %s%n", path);
	}

}
