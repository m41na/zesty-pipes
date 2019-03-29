package com.practicaldime.zesty.servlet;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MiddlewareTest {

	private static int size = 4;
	private static List<Middleware<Integer>> middleware = new ArrayList<>(size);
	static {
		for(int i = 0; i < size; i++) {
			Middleware<Integer> action = new MiddlewareAction();
			middleware.add(action);
			if(i > 0) {
				middleware.get(i - 1).setNext(action);
			}
		}
	}
	
	@Test
	public void testOnNext() {
		Middleware<Integer> action = middleware.get(0);
		Integer count = action.onNext(action.getNext(), 0).join();
		assertEquals("Expecting 4", 4, count.intValue());
	}
}
