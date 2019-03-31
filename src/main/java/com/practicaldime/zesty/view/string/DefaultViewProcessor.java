package com.practicaldime.zesty.view.string;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Notes: Class.getResourceAsStream() - you can pass both absolute and relative
 * path ClassLoader.getResourceAsStream() - takes an absolute path. If the name
 * begins with a '/' ('\u002f'), then the absolute name of the resource is the
 * portion of the name following the '/'. Otherwise, the absolute name is of the
 * following form: modified_package_name/name where the modified_package_name is
 * the package name of this object with '/' substituted for '.' ('\u002e')
 */
public class DefaultViewProcessor implements ViewProcessor {

	@Override
	public String resolve(String templateDir, String template, Lookup strategy) throws Exception {
		switch (strategy) {
		case FILE: {
			String baseDir = System.getProperty("user.dir");
			Path path = Paths.get(baseDir, templateDir);
			return new String(Files.readAllBytes(path.resolve(template)));
		}
		case CLASSPATH: {
			URL url = this.getClass().getClassLoader().getResource("");
			String baseDir = resolveName(url.getPath());
			Path path = Paths.get(baseDir, templateDir, template);
			return new String(Files.readAllBytes(path));		
		}
		default: {
			return null;
		}
		}
	}

	private String resolveName(String name) {
		if (name == null) {
			return name;
		}
		if (!name.startsWith("/")) {
			Class<?> c = this.getClass();
			while (c.isArray()) {
				c = c.getComponentType();
			}
			String baseName = c.getName();
			int index = baseName.lastIndexOf('.');
			if (index != -1) {
				name = baseName.substring(0, index).replace('.', '/') + "/" + name;
			}
		} else {
			name = name.substring(1);
		}
		return name;
	}
}
