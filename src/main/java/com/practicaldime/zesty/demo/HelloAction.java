package com.practicaldime.zesty.demo;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import com.practicaldime.zesty.servlet.AbstractMiddleware;
import com.practicaldime.zesty.servlet.HandlerContext;

public class HelloAction extends AbstractMiddleware<HandlerContext> {

	@Override
	public String getName() {
		return "hello handler";
	}

	@Override
	public CompletableFuture<HandlerContext> apply(HandlerContext context) {
		context.set("DATA", ByteBuffer.wrap("Hello from middleware!!!!!".getBytes()));
		return CompletableFuture.supplyAsync(() -> context);
	}
}
