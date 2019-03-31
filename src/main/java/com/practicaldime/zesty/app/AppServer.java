package com.practicaldime.zesty.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.practicaldime.zesty.basics.AppRouter;
import com.practicaldime.zesty.basics.AppViewEngines;
import com.practicaldime.zesty.router.MethodRouter;
import com.practicaldime.zesty.router.Route;
import com.practicaldime.zesty.router.Router;
import com.practicaldime.zesty.servlet.AbstractMiddleware;
import com.practicaldime.zesty.servlet.HandlerConfig;
import com.practicaldime.zesty.servlet.Middleware;
import com.practicaldime.zesty.servlet.MiddlewareChain;
import com.practicaldime.zesty.servlet.RequestContext;
import com.practicaldime.zesty.servlet.RouteResolver;
import com.practicaldime.zesty.servlet.RouterServlet;
import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ViewEngineFactory;

public class AppServer {
	
	private static final Logger LOG = LoggerFactory.getLogger(AppServer.class);
	private Server server;
	private AppRouter routes;
	private String status = "stopped";
	private static ViewEngine engine;
	private final Properties locals = new Properties();
	@SuppressWarnings("unused")
	private final ThreadPoolExecutor threadPoolExecutor;
	private final Map<String, String> corscontext = new HashMap<>();
	private final LifecycleSubscriber lifecycle = new LifecycleSubscriber();
	private final MiddlewareChain<RequestContext> chain = new MiddlewareChain<>();
	private final Map<String, Middleware<RequestContext>> handlers = new HashMap<>();
	private final ViewEngineFactory engineFactory = new AppViewEngines();
	private final ServletContextHandler servlets = new ServletContextHandler(ServletContextHandler.SESSIONS);
	
	public AppServer(Map<String, String> props) {
		this.assets(Optional.ofNullable(props.get("assets")).orElse("www"));
		this.appctx(Optional.ofNullable(props.get("appctx")).orElse("/"));
		this.engine(Optional.ofNullable(props.get("engine")).orElse("*"));
		this.threadPoolExecutor = createThreadPoolExecutor();
	}
	
	public static ViewEngine engine() {
		if (engine == null) {
			throw new RuntimeException("The engine is not yet initialized");
		}
		return engine;
	}
	
	public String status() {
		return "server status is " + status;
	}

	public final void appctx(String path) {
		this.locals.put("appctx", path);
	}

	public final void assets(String path) {
		this.locals.put("assets", path);
	}
	
	public final void engine(String view) {
		switch (view) {
		case "jtwig":
			engine = engineFactory.engine(view, locals.getProperty("assets"), "html");
			break;
		case "freemarker":
			engine = engineFactory.engine(view, locals.getProperty("assets"), "ftl");
			break;
		default:
			LOG.error("specified engine not supported. defaulting to 'none' instead");
			engine = engineFactory.engine(view, locals.getProperty("assets"), "");
		}
		this.locals.put("engine", view);
	}
	
	public String resolve(String path) {
		String appctx = this.locals.getProperty("appctx");
		String path1 = !appctx.startsWith("/") ? "/" + appctx : appctx;
		if (path1.endsWith("/")) {
			path1 = path1.substring(0, path1.length() - 1);
		}
		String path2 = !path.startsWith("/") ? "/" + path : path;
		return path1 + path2;
	}

	public Set<String> locals() {
		return locals.stringPropertyNames();
	}

	public Object locals(String param) {
		return locals.get(param);
	}
	
	public AppServer cors(Map<String, String> cors) {
		this.locals.put("cors", "true");
		if(cors != null) this.corscontext.putAll(cors);
		return this;
	}
	
	public AppServer lifecycle(String event, Consumer<String> callback) {
		this.lifecycle.subscribe(event, callback);
		return this;
	}
	
	public AppServer router() {
		this.routes = new AppRouter(new MethodRouter());
		return this;
	}
	
	public AppServer router(Supplier<Router> supplier) {
		this.routes = new AppRouter(supplier.get());
		return this;
	}

//	public AppServer filter(HandlerFilter filter) {
//		return filter("/*", filter);
//	}
//
//	public AppServer filter(String context, HandlerFilter filter) {
//		FilterHolder holder = new FilterHolder(filter);
//		servlets.addFilter(holder, context, EnumSet.of(DispatcherType.REQUEST));
//		return this;
//	}
	
	public AppServer servlet(String path, HandlerConfig config, HttpServlet handler) {
		Route route = new Route(resolve(path), "all", "*", "*");
		route.setId();
		routes.addRoute(route);
		// add servlet handler
		ServletHolder holder = new ServletHolder(handler);
		if(config != null) config.configure(holder);
		servlets.addServlet(holder, route.rid);
		return this;
	}
	
	public AppServer route(String method, String path, Middleware<RequestContext> handler) {
		switch (method.toLowerCase()) {
		case "get":
			return get(path, "", "", handler);
		case "post":
			return post(path, "", "", handler);
		case "put":
			return put(path, "", "", handler);
		case "delete":
			return delete(path, "", "", handler);
		case "options":
			return options(path, "", "", handler);
		case "trace":
			return trace(path, "", "", handler);
		case "head":
			return head(path, "", "", handler);
		case "all":
			return all(path, "", "", handler);
		default:
			throw new UnsupportedOperationException(method + " is not a supported method");
		}
	}
	
	// ************* HEAD *****************//
	public AppServer head(String path, Middleware<RequestContext> handler) {
		return head(path, "", "", handler);
	}

	public AppServer head(String path, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return head(path, "", "", new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("head_%s", path);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer head(String path, String accept, String type, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return head(path, accept, type, new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("head_%s_accept_%s_type_%s", path, accept, type);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer head(String path, String accept, String type, Middleware<RequestContext> handler) {
		Route route = new Route(resolve(path), "head", accept, type);
		route.setId();
		routes.addRoute(route);
		//add handler
		handlers.put(route.rid, handler);
		return this;
	}

	// ************* TRACE *****************//
	public AppServer trace(String path, Middleware<RequestContext> handler) {
		return trace(path, "", "", handler);
	}

	public AppServer trace(String path, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return trace(path, "", "", new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("trace_%s", path);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer trace(String path, String accept, String type, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return trace(path, accept, type, new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("trace_%s_accept_%s_type_%s", path, accept, type);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer trace(String path, String accept, String type, Middleware<RequestContext> handler) {
		Route route = new Route(resolve(path), "trace", accept, type);
		route.setId();
		routes.addRoute(route);
		// add handler
		handlers.put(route.rid, handler);
		return this;
	}

	// ************* OPTIONS *****************//
	public AppServer options(String path, Middleware<RequestContext> handler) {
		return options(path, "", "", handler);
	}

	public AppServer options(String path, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return options(path, "", "", new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("options_%s", path);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer options(String path, String accept, String type, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return options(path, accept, type, new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("options_%s_accept_%s_type_%s", path, accept, type);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer options(String path, String accept, String type, Middleware<RequestContext> handler) {
		Route route = new Route(resolve(path), "options", accept, type);
		route.setId();
		routes.addRoute(route);
		// add handler
		handlers.put(route.rid, handler);
		return this;
	}

	// ************* GET *****************//
	public AppServer get(String path, Middleware<RequestContext> handler) {
		return get(path, "", "", handler);
	}

	public AppServer get(String path, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return get(path, "", "", new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("get_%s", path);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer get(String path, String accept, String type, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return get(path, accept, type, new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("get_%s_accept_%s_type_%s", path, accept, type);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer get(String path, String accept, String type, Middleware<RequestContext> handler) {
		Route route = new Route(resolve(path), "get", accept, type);
		route.setId();
		routes.addRoute(route);
		// add handler
		handlers.put(route.rid, handler);
		return this;
	}

	// ************* POST *****************//
	public AppServer post(String path, Middleware<RequestContext> handler) {
		return post(path, "", "", handler);
	}

	public AppServer post(String path,  Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return post(path, "", "", new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("post_%s", path);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer post(String path, String accept, String type, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return post(path, accept, type, new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("post_%s_accept_%s_type_%s", path, accept, type);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer post(String path, String accept, String type, Middleware<RequestContext> handler) {
		Route route = new Route(resolve(path), "post", accept, type);
		route.setId();
		routes.addRoute(route);
		// add handler
		handlers.put(route.rid, handler);
		return this;
	}

	// ************* PUT *****************//
	public AppServer put(String path, Middleware<RequestContext> handler) {
		return put(path, "", "", handler);
	}

	public AppServer put(String path, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return put(path, "", "", new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("put_%s", path);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer put(String path, String accept, String type, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return put(path, accept, type, new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("put_%s_accept_%s_type_%s", path, accept, type);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer put(String path, String accept, String type, Middleware<RequestContext> handler) {
		Route route = new Route(resolve(path), "put", accept, type);
		route.setId();
		routes.addRoute(route);
		// add handler
		handlers.put(route.rid, handler);
		return this;
	}

	// ************* DELETE *****************//
	public AppServer delete(String path, Middleware<RequestContext> handler) {
		return delete(path, "", "", handler);
	}

	public AppServer delete(String path, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return delete(path, "", "", new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("delete_%s", path);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer delete(String path, String accept, String type, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return delete(path, accept, type, new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("delete_%s_accept_%s_type_%s", path, accept, type);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer delete(String path, String accept, String type, Middleware<RequestContext> handler) {
		Route route = new Route(resolve(path), "delete", accept, type);
		route.setId();
		routes.addRoute(route);
		// add handler
		handlers.put(route.rid, handler);
		return this;
	}

	// ************* ALL *****************//
	public AppServer all(String path, Middleware<RequestContext> handler) {
		return all(path, "", "", handler);
	}

	public AppServer all(String path, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return all(path, "", "", new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("all_%s", path);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer all(String path, String accept, String type, Function<RequestContext, CompletableFuture<RequestContext>> handler) {
		return all(path, accept, type, new AbstractMiddleware<RequestContext>() {

			@Override
			public String getName() {
				return String.format("all_%s_accept_%s_type_%s", path, accept, type);
			}

			@Override
			public CompletableFuture<RequestContext> apply(RequestContext context) {
				return handler.apply(context);
			}
		});
	}

	public AppServer all(String path, String accept, String type, Middleware<RequestContext> handler) {
		Route route = new Route(resolve(path), "all", accept, type);
		route.setId();
		routes.addRoute(route);
		// add handler
		handlers.put(route.rid, handler);
		return this;
	}

//	// ************* WEBSOCKETS *****************//
//	public AppServer websocket(String ctx, AppWsProvider provider) {
//		// Add a websocket to a specific path spec
//		ServletHolder holderEvents = new ServletHolder("ws-events", new AppWsServlet(provider));
//		servlets.addServlet(holderEvents, ctx);
//		return this;
//	}
//
//	// ************* WORDPRESS *****************//
//	public AppServer wordpress(String home, String proxyTo) {
//		this.wpcontext.put("activate", "true");
//		this.wpcontext.put("resourceBase", home);
//		this.wpcontext.put("welcomeFile", "index.php");
//		this.wpcontext.put("proxyTo", proxyTo);
//		this.wpcontext.put("scriptRoot", home);
//		return this;
//	}
	
	// ************* START *****************//
	public void listen(int port, String host) {
		listen(port, host, null);
	}

	public void listen(int port, String host, Consumer<String> result) {		
		try {
			status = "starting";
			// create server with thread pool
			QueuedThreadPool threadPool = new QueuedThreadPool(500, 5, 3000);
			server = new Server(threadPool);

			// Scheduler
			server.addBean(new ScheduledExecutorScheduler());

			// configure connector
			ServerConnector http = new ServerConnector(server);
			http.setHost(host);
			http.setPort(port);
			http.setIdleTimeout(3000);
			server.addConnector(http);

			// TODO: configure secure connector
			// enable CORS
//			if(Boolean.valueOf(this.locals.getProperty("cors", "false"))){
//				FilterHolder corsFilter = new FilterHolder(CrossOriginFilter.class);
//				//add default values
//				corsFilter.setInitParameter(ALLOWED_ORIGINS_PARAM, Optional.ofNullable(corscontext.get(ALLOWED_ORIGINS_PARAM)).orElse("*"));
//				corsFilter.setInitParameter(ALLOWED_METHODS_PARAM, Optional.ofNullable(corscontext.get(ALLOWED_METHODS_PARAM)).orElse("GET,POST,PUT,DELETE,OPTIONS,HEAD"));
//				corsFilter.setInitParameter(ALLOWED_HEADERS_PARAM, Optional.ofNullable(corscontext.get(ALLOWED_HEADERS_PARAM)).orElse("Content-Type,Accept,Origin"));
//				corsFilter.setInitParameter(ALLOW_CREDENTIALS_PARAM, Optional.ofNullable(corscontext.get(ALLOW_CREDENTIALS_PARAM)).orElse("true"));
//				corsFilter.setInitParameter(PREFLIGHT_MAX_AGE_PARAM, Optional.ofNullable(corscontext.get(PREFLIGHT_MAX_AGE_PARAM)).orElse("728000"));
//				//add other user defined values that are not in the list of default keys
//				List<String> skipKeys = Arrays.asList(
//						ALLOWED_ORIGINS_PARAM, 
//						ALLOWED_METHODS_PARAM,
//						ALLOWED_HEADERS_PARAM, 
//						ALLOW_CREDENTIALS_PARAM, 
//						PREFLIGHT_MAX_AGE_PARAM);
//				corscontext.keySet().stream().filter(key-> !skipKeys.contains(key)).forEach(key->{
//					corsFilter.setInitParameter(key, corscontext.get(key));
//				});
//				corsFilter.setName("zesty-cors-filter");
//	
//				FilterMapping corsMapping = new FilterMapping();
//				corsMapping.setFilterName("cross-origin");
//				corsMapping.setPathSpec("*");
//				servlets.addFilter(corsFilter, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
//			}
			// add routes filter
			//servlets.addFilter(new FilterHolder(new RouteFilter(this.routes)), "/*", EnumSet.of(DispatcherType.REQUEST));

			// configure resource handlers
			String resourceBase = this.locals.getProperty("assets");

			// configure context for servlets
			String appctx = this.locals.getProperty("appctx");
			servlets.setContextPath(appctx);

			// configure default servlet for app context
			ServletHolder defaultServlet = createResourceServlet(resourceBase);
			servlets.addServlet(defaultServlet, "/*");
			
			// configure route resolver
			Middleware<RequestContext> router = new RouteResolver(this.routes, this.handlers);
			
			//configure routes servlet
			ServletHolder routeHolder = new ServletHolder(new RouterServlet(chain, router));
			routeHolder.setAsyncSupported(true);
			servlets.addServlet(routeHolder, "/api/*");
			
			// configure ResourceHandler to serve static files
			ResourceHandler appResources = createResourceHandler(resourceBase);

			// collect all context handlers
			ContextHandlerCollection contextHandlers = new ContextHandlerCollection();
			contextHandlers.addHandler(servlets);

			// add handlers to the server
			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { appResources, contextHandlers, new DefaultHandler() });
			server.setHandler(handlers);

			// add shutdown hook
			addRuntimeShutdownHook(server);

			// start and access server using http://localhost:8080
			server.start();
			status = "running";
			if (result != null) {
				result.accept("AppServer is now listening on port " + port + "!");
			}
			server.join();
			status = "stopped";
		} catch (Throwable e) {
			e.printStackTrace(System.err);
			status = "stopped";
			result.accept("AppServer could not start because -> " + e.getMessage());
			System.exit(1);
		}
	}
	
	protected ThreadPoolExecutor createThreadPoolExecutor() {
		int poolSize = Integer.valueOf(this.locals.getProperty("poolSize", "100"));
		int maxPoolSize = Integer.valueOf(this.locals.getProperty("maxPoolSize", "200"));
		Long keepAliveTime = Long.valueOf(this.locals.getProperty("keepAliveTime", "5000"));
		return new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime,
				TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(poolSize));
	}
	
	protected ResourceHandler createResourceHandler(String resourceBase) {
		ResourceHandler appResources = new ResourceHandler();
		appResources.setDirectoriesListed(false);
		appResources.setWelcomeFiles(new String[] { "index.html" });
		appResources.setResourceBase(resourceBase);
		return appResources;
	}

	protected ServletHolder createResourceServlet(String resourceBase) {
		// DefaultServlet should be named 'default'
		ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
		defaultServlet.setInitParameter("resourceBase", resourceBase);
		defaultServlet.setInitParameter("dirAllowed", "false");
		return defaultServlet;
	}
	
	private void addRuntimeShutdownHook(final Server server) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (server.isStarted()) {
				server.setStopAtShutdown(true);
				try {
					server.stop();
				} catch (Exception e) {
					LOG.error("Error while shutting down jetty server", e);
					throw new RuntimeException(e);
				}
			}
		}));
	}

	class LifecyclePublisher implements LifeCycle.Listener {

		private final LifecycleSubscriber subscriber;

		public LifecyclePublisher(LifecycleSubscriber subscriber) {
			this.subscriber = subscriber;
		}

		@Override
		public void lifeCycleStarting(LifeCycle event) {
			subscriber.onStarting();
		}

		@Override
		public void lifeCycleStarted(LifeCycle event) {
			subscriber.onStarted();
		}

		@Override
		public void lifeCycleFailure(LifeCycle event, Throwable cause) {
			subscriber.onFailed(cause);
		}

		@Override
		public void lifeCycleStopping(LifeCycle event) {
			subscriber.onStopping();
		}

		@Override
		public void lifeCycleStopped(LifeCycle event) {
			subscriber.onStopped();
		}
	}

	class LifecycleSubscriber {

		private final Map<String, Consumer<String>> subscribers;
		private String[] stages = {"starting", "started", "stopping", "stopped", "failed"};

		public LifecycleSubscriber() {
			this.subscribers = new HashMap<>();
		}

		public void subscribe(String event, Consumer<String> callback) {
			if (subscribers.keySet().contains(event)) {
				this.subscribers.put(event, callback);
			} else {
				LOG.error("There is no such event as {}", event);
			}
		}

		public void onStarting() {
			subscribers.get("starting").accept(stages[0]);
		}

		public void onStarted() {
			subscribers.get("started").accept(stages[1]);
		}

		public void onStopping() {
			subscribers.get("stopping").accept(stages[2]);
		}

		public void onStopped() {
			subscribers.get("stopped").accept(stages[3]);
		}

		public void onFailed(Throwable thr) {
			subscribers.get("failed").accept(stages[4]);
		}
	}
}
