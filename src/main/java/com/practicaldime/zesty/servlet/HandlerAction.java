package com.practicaldime.zesty.servlet;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@FunctionalInterface
public interface HandlerAction<T, R> extends Function<T, CompletableFuture<R>>{

}
