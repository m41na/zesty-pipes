package com.practicaldime.zesty.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerReadListener implements ReadListener {

	private static final Logger LOG = LoggerFactory.getLogger(HandlerReadListener.class);
	private ServletInputStream input = null;
	private HandlerAction<ByteBuffer, RequestContext> action = null;
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();

	public HandlerReadListener(ServletInputStream input, HandlerAction<ByteBuffer, RequestContext> action) {
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
		LOG.info("Data is all read");
		
		CompletableFuture<ByteArrayOutputStream> data = CompletableFuture.supplyAsync(()->stream);
		
		data.thenApply(stream -> ByteBuffer.wrap(stream.toByteArray()))
		.thenCompose(bytes -> action.apply(bytes))
		.handle((context, th) -> {
			if(th != null) {
				th.printStackTrace(System.err);
				context.getResp().setStatus(500);
				try {
					ByteBuffer bytes = ByteBuffer.wrap(th.getMessage().getBytes());
					ServletOutputStream output = context.getResp().getOutputStream();
					WriteListener writeListener = new HandlerWriteListener(bytes, context.getAsync(), output);
					output.setWriteListener(writeListener);
				}
				catch(IOException e) {
					LOG.error("***********{}", e.getMessage());
				}
			}
			return context;
		})
		.thenAccept(context -> {
			context.getResp().setStatus(200);
			try {
				ByteBuffer bytes = context.getResp().content;
				ServletOutputStream output = context.getResp().getOutputStream();
				WriteListener writeListener = new HandlerWriteListener(bytes, context.getAsync(), output);
				output.setWriteListener(writeListener);
			} catch (IOException e) {
				LOG.error("***********{}", e.getMessage());
			}
		});
	}

	@Override
	public void onError(Throwable t) {
		//context.getAsync().complete();
		t.printStackTrace();
	}
}
