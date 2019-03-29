package com.practicaldime.zesty.demo;

import java.util.concurrent.CompletableFuture;

import com.practicaldime.zesty.servlet.AbstractMiddleware;
import com.practicaldime.zesty.servlet.HandlerContext;

public class EchoAction extends AbstractMiddleware<HandlerContext>{

	@Override
	public String getName() {
		return "echo handler";
	}

	@Override
	public CompletableFuture<HandlerContext> apply(HandlerContext context) {
		return CompletableFuture.supplyAsync(() -> context);
	}
}
