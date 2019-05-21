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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HandlerServletAsync extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(HandlerServletAsync.class);

	private final MiddlewareChain<RequestContext> chain;

	public abstract CompletableFuture<RequestContext> handler(RequestContext context);

	public HandlerServletAsync(MiddlewareChain<RequestContext> chain) {
		super();
		this.chain = chain;
	}

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
		HandlerAction<ByteBuffer, RequestContext> action = data -> {
			RequestContext context = new RequestContext(req, resp, async);
			context.getReq().body = data.array();
			LOG.info("*********doProcess(): starting chain for {} and then handler thereafter", req.getRequestURI());			
			return chain.start(context).thenCompose(ctx -> handler(ctx));
		};

        ReadListener readListener = new HandlerReadListener(input, action);
        input.setReadListener(readListener);
	}
}
