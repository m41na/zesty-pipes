package com.practicaldime.zesty.servlet;

import java.util.concurrent.CompletableFuture;

public class RouterServlet extends HandlerServletAsync {

	private static final long serialVersionUID = 1L;
	private Middleware<HandlerContext> router; 

	public RouterServlet(Middleware<HandlerContext> router) {
		super();
		this.router = router;
	}

	@Override
	public CompletableFuture<HandlerContext> handler(HandlerContext context) {
		return router.apply(context);
	}
}
