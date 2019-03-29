package com.practicaldime.zesty.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerWriteListener implements WriteListener {

	private final Logger LOG = LoggerFactory.getLogger(HandlerWriteListener.class);

	private final ByteArrayInputStream content;
	private final AsyncContext async;
	private final ServletOutputStream out;

	public HandlerWriteListener(ByteBuffer content, AsyncContext async, ServletOutputStream out) {
		this.content = new ByteArrayInputStream(content.array());
		this.async = async;
		this.out = out;
	}

	public void onWritePossible() throws IOException {
		byte[] buffer = new byte[4096];

		// while we are able to write without blocking
		while (out.isReady()) {
			// read some content into the copy buffer
			int len = content.read(buffer);

			// If we are at EOF then complete
			if (len < 0) {
				async.complete();
				out.flush();
				return;
			}

			// write out the copy buffer.
			out.write(buffer, 0, len);
		}
	}

	public void onError(Throwable t) {
		LOG.error("Async servlet error: {}", t.getMessage());
		async.complete();
		t.printStackTrace();
	}
}
