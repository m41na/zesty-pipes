package com.practicaldime.zesty.servlet;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * TODO: provide reference implementation of MiddlewareCursor
 * This test is very incomplete. Without a MiddlewareCursor implementation, there isn't any
 * concept or ideas to test.
 * 
 * @author Mainas
 *
 */
public class MiddlewareCursorTest {

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
	public void testNavigation() {
		Middleware<Integer> action = middleware.get(0);
		Integer count = action.onNext(action.getNext(), 0).join();
		assertEquals("Expecting 4", 4, count.intValue());
	}
}
