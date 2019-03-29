package com.practicaldime.zesty.servlet;

import java.util.concurrent.CompletableFuture;

public class MiddlewareAction extends AbstractMiddleware<Integer> {

	@Override
	public String getName() {
		return "Test_MiddlewareAction";
	}

	@Override
	public CompletableFuture<Integer> apply(Integer context) {
		Integer count = context;
		if (count == null) {
			count = 0;
		} else {
			count += 1;
		}
		System.out.printf("COUNT is %d%n", count);
		return CompletableFuture.completedFuture(count);
	}
}
