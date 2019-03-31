package com.practicaldime.zesty.servlet;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMiddleware<T> implements Middleware<T>{

	private static final Logger LOG = LoggerFactory.getLogger(AbstractMiddleware.class);
	private Middleware<T> next;
	private Middleware<T> prev;

	@Override
	public void setNext(Middleware<T> next) {
		this.next = next;
	}

	@Override
	public Middleware<T> getNext() {
		return this.next;
	}

	@Override
	public void setPrev(Middleware<T> prev) {
		this.prev = prev;
	}

	@Override
	public Middleware<T> getPrev() {
		return this.prev;
	}

	@Override
	public CompletableFuture<T> onCompleted(T value) {
		CompletableFuture<T> future = new CompletableFuture<>();
		future.complete(value);
		return future;
	}

	@Override
	public BiFunction<T, Throwable, T> onException(T value) {
		return (T res, Throwable th) -> {
			if(res == null) {
				LOG.error("*********{}", th.getMessage());
			}
			return res;
		};
	}
}
