package com.practicaldime.zesty.servlet;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.FilterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.practicaldime.zesty.basics.AppRouter;
import com.practicaldime.zesty.router.RouteSearch;

public class RouteResolver extends AbstractMiddleware<RequestContext> {

	public static final Logger LOG = LoggerFactory.getLogger(RouteResolver.class);

	protected FilterConfig fConfig;
	private final AppRouter routes;
	private final Gson gson = new Gson();
	private final Map<String, Middleware<RequestContext>> handlers;

	public RouteResolver(AppRouter routes, Map<String, Middleware<RequestContext>> handlers) {
		super();
		this.routes = routes;
		this.handlers = handlers;
	}

	@Override
	public String getName() {
		return "route resolver";
	}

	@Override
	public CompletableFuture<RequestContext> apply(RequestContext context) {
		HandlerRequest httpRequest = context.getReq();
		HandlerResponse httpResponse = context.getResp();
		// set additional properties in response wrapper
		httpResponse.context(httpRequest.getContextPath());

		RouteSearch route = routes.search(httpRequest);
		if (route.result != null) {
			LOG.info("matched route -> {}", gson.toJson(route));
			httpRequest.route(route);
			Middleware<RequestContext> handler = handlers.get(route.result.rid);
			return handler.apply(context).handle(handler.onException(context)::apply).thenCompose(handler::onCompleted);
		} else {
			return CompletableFuture.failedFuture(new RuntimeException("requested path does not exist"));
		}
	}
}
