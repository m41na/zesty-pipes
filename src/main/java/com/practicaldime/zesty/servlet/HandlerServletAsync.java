package com.practicaldime.zesty.servlet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.practicaldime.zesty.app.AppServer;

public abstract class HandlerServletAsync extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public abstract CompletableFuture<HandlerContext> handler(HandlerContext context);
	private MiddlewareChain<HandlerContext> chain = AppServer.chain();

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// delegate to super implementation
		super.doTrace(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// delegate to super implementation
		super.doOptions(req, resp);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// delegate to super implementation
		super.doHead(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doProcess(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doProcess(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doProcess(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doProcess(req, resp);
	}

	protected void doProcess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AsyncContext async = req.startAsync();
		ServletInputStream input = req.getInputStream();
		HandlerAction<ByteBuffer, HandlerContext> action = data -> {
			HandlerContext context = new HandlerContext(req, resp, async);
			context.set("DATA", data);
			System.out.printf("*********doProcess(): starting chain for %s%n", req.getRequestURI());			
			//return CompletableFuture.supplyAsync(() -> context).thenCompose(ctx -> handler(ctx));
			return chain.start(context).thenCompose(ctx -> handler(ctx));
		};

        ReadListener readListener = new HandlerReadListener(input, action);
        input.setReadListener(readListener);
	}
}
