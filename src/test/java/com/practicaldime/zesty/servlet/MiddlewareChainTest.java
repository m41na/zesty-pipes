package com.practicaldime.zesty.servlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MiddlewareChainTest {

	private static int size = 4;
	
	@Test
	public void testChainStart() {
		MiddlewareChain<Integer> chain = new MiddlewareChain<>();
		for(int i = 0; i < size; i++) {
			Middleware<Integer> action = new MiddlewareAction();
			chain.register(action);
		}
		
		Integer count = chain.start(0).join();
		assertEquals("Expecting 4", 4, count.intValue());
	}
	
	@Test
	public void testRegisterAndUnregister() {
		MiddlewareChain<Integer> chain = new MiddlewareChain<>();
		
		Middleware<Integer> action1 = new MiddlewareAction();
		chain.register(action1);
		
		Middleware<Integer> action2 = new MiddlewareAction();
		chain.register(action2);
		
		Middleware<Integer> action3 = new MiddlewareAction();
		chain.register(action3);
		
		Integer count3 = chain.start(0).join();
		assertEquals("Expecting 3", 3, count3.intValue());
		
		chain.unregister(action1);
		
		Integer count2 = chain.start(0).join();
		assertEquals("Expecting 2", 2, count2.intValue());
		
		chain.unregister(action2);
		
		Integer count1 = chain.start(0).join();
		assertEquals("Expecting 1", 1, count1.intValue());
		
		chain.unregister(action1);
		
		Integer count = chain.start(0).join();
		assertEquals("Expecting 0", 0, count.intValue());
	}
}
