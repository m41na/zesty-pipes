package com.practicaldime.zesty.servlet;

import java.util.concurrent.CompletableFuture;

public class RouterServlet extends HandlerServletAsync {

	private static final long serialVersionUID = 1L;
	private Middleware<RequestContext> router; 

	public RouterServlet(MiddlewareChain<RequestContext> chain, Middleware<RequestContext> router) {
		super(chain);
		this.router = router;
	}

	@Override
	public CompletableFuture<RequestContext> handler(RequestContext context) {
		return router.apply(context);
	}
}
