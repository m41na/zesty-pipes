package com.practicaldime.zesty.demo;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import com.practicaldime.zesty.servlet.AbstractMiddleware;
import com.practicaldime.zesty.servlet.RequestContext;

public class HelloAction extends AbstractMiddleware<RequestContext> {

	@Override
	public String getName() {
		return "hello handler";
	}

	@Override
	public CompletableFuture<RequestContext> apply(RequestContext context) {
		CompletableFuture<RequestContext> future = new CompletableFuture<>();
		String data = "Hello data from apply()!!!!!";
		System.out.println("****** data is ready for response: " + data);
		context.getResp().send(data.getBytes());
		//future.complete(context);
		future.completeExceptionally(new RuntimeException("****Hello action error"));
		return future;
	}

	@Override
	public CompletableFuture<RequestContext> onCompleted(RequestContext context) {
		Throwable err = context.get("ERROR", Throwable.class);
		if(err == null) {
			String data = "Hello data from onCompleted()!!!!!";
			System.out.println("****** data is ready for response: " + data);
			context.set("DATA", ByteBuffer.wrap(data.getBytes()));			
		}
		else {
			String data = err.getMessage();
			System.out.println("****** data is ready for response: " + data);
			context.set("DATA", ByteBuffer.wrap(data.getBytes()));
		}
		return super.onCompleted(context);
	}
	
	@Override
	public BiFunction<RequestContext, Throwable, RequestContext> onException(RequestContext context) {
		return (RequestContext res, Throwable th) -> {
			if(res == null) {
				System.err.printf("*********%s%n", th.getMessage());
				context.set("ERROR", th);
				return context;
			}
			return res;
		};
	}
}
