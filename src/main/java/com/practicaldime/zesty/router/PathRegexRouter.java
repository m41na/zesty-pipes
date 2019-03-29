package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathRegexRouter implements Router{

	private Map<PathPattern, Router> routers = new HashMap<>();
	
	@Override
	public void accept(RouteSearch input) {
		for(PathPattern pathRegex : routers.keySet()) {
			Pattern valuesPattern = pathRegex.valuesPattern;
			Matcher matcher = valuesPattern.matcher(input.requestAttrs.url);
			if(matcher.matches()) {
				routers.get(pathRegex).accept(input);
				//if a matching route is found, extract path params from the input's url and get the values path params if any
				if(input.result != null) {
					if(matcher.groupCount() > 0) {
						Pattern paramsPattern = pathRegex.paramsPattern;
						Matcher routeMatcher = paramsPattern.matcher(input.result.path);
						if(routeMatcher.matches()) {
							for(int i = 1; i <= matcher.groupCount(); i++) {
								String paramVal = matcher.group(i);
								String paramKey = routeMatcher.group(i);
								input.pathParams.put(paramKey, paramVal);
							}
						}
						else {
							throw new RuntimeException("Expected paramsPattern to match on input path");
						}
					}
				}
			}
		}
	}

	@Override
	public void addRoute(Route route) {
		String valuesRegex = route.path.replaceAll("\\{(.+?)\\}", "(.+?)").replaceAll("\\/", "\\\\/");
		String paramsRegex = route.path.replaceAll("\\{(.+?)\\}", "\\\\{(.+?)\\\\}").replaceAll("\\/", "\\\\/");
		PathPattern pathRegex = new PathPattern(valuesRegex, paramsRegex);
		if(!routers.containsKey(pathRegex)) {
			routers.put(pathRegex, new HeadersRouter());
		}
		routers.get(pathRegex).addRoute(route);
	}
	
	public static class PathPattern implements Comparable<PathPattern>{
		
		private final String valuesRegex;
		private final String paramsRegex;
		public final Pattern valuesPattern;
		public final Pattern paramsPattern;
		
		public PathPattern(String valuesRegex, String paramsRegex) {
			super();
			this.valuesRegex = valuesRegex;
			this.paramsRegex = paramsRegex;
			this.valuesPattern = Pattern.compile(valuesRegex);
			this.paramsPattern = Pattern.compile(paramsRegex);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((paramsRegex == null) ? 0 : paramsRegex.hashCode());
			result = prime * result + ((valuesRegex == null) ? 0 : valuesRegex.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PathPattern other = (PathPattern) obj;
			if (paramsRegex == null) {
				if (other.paramsRegex != null)
					return false;
			} else if (!paramsRegex.equals(other.paramsRegex))
				return false;
			if (valuesRegex == null) {
				if (other.valuesRegex != null)
					return false;
			} else if (!valuesRegex.equals(other.valuesRegex))
				return false;
			return true;
		}

		@Override
		public int compareTo(PathPattern that) {
			if(that == null) return 1;
			if(this == that) return 0;
			return this.paramsRegex.compareTo(that.paramsRegex);
		}
	}
	
	public static void main(String...args) {
		String regex = "\\/todo\\/(\\{.+?\\})";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher("/todo/{id}");
		System.out.println(matcher.find());
		
		String value = "/todo/({.+?})";
		System.out.println(value.replaceAll("\\{(.+?, replacement)\\}", "\\\\{$1\\\\}").replaceAll("\\/", "\\\\/"));
	}
}
