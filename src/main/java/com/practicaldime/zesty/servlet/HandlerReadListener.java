package com.practicaldime.zesty.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class HandlerReadListener implements ReadListener {

	private ServletInputStream input = null;
	private HandlerAction<ByteBuffer, HandlerContext> action = null;
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();

	public HandlerReadListener(ServletInputStream input, HandlerAction<ByteBuffer, HandlerContext> action) {
		super();
		this.input = input;
		this.action = action;
	}

	@Override
	public void onDataAvailable() throws IOException {
		System.out.println("Data is available");
		int len = -1;
		byte b[] = new byte[1024];
		while (input.isReady() && (len = input.read(b)) != -1) {
			stream.write(b, 0, len);
		}
	}

	@Override
	public void onAllDataRead() throws IOException {
		System.out.println("Data is all read");
		
		CompletableFuture<ByteArrayOutputStream> data = CompletableFuture.supplyAsync(()->stream);
		
		data.thenApply(stream -> ByteBuffer.wrap(stream.toByteArray()))
		.thenCompose(bytes -> action.apply(bytes))
		.thenApply(context -> {
			// now handler is finished, set up a WriteListener to respond
			
			try {
				ByteBuffer bytes = context.get("DATA", ByteBuffer.class);
				ServletOutputStream output = context.getResp().getOutputStream();
				WriteListener writeListener = new HandlerWriteListener(bytes, context.getAsync(), output);
				output.setWriteListener(writeListener);
				return context;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
		}).handle((context, th) -> {
			th.printStackTrace(System.out);
			context.getResp().setStatus(500);
			context.getAsync().complete();
			return CompletableFuture.failedFuture(th);
		})
		.complete(null);
	}

	@Override
	public void onError(Throwable t) {
		//context.getAsync().complete();
		t.printStackTrace();
	}
}
