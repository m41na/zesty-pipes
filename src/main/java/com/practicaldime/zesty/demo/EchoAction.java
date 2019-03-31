package com.practicaldime.zesty.demo;

import java.util.concurrent.CompletableFuture;

import com.practicaldime.zesty.servlet.AbstractMiddleware;
import com.practicaldime.zesty.servlet.RequestContext;

public class EchoAction extends AbstractMiddleware<RequestContext>{

	@Override
	public String getName() {
		return "echo handler";
	}

	@Override
	public CompletableFuture<RequestContext> apply(RequestContext context) {
		return CompletableFuture.supplyAsync(() -> context);
	}
}
