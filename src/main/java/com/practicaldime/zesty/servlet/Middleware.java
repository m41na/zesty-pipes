package com.practicaldime.zesty.servlet;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public interface Middleware<T> extends HandlerAction<T, T> {

	String getName();

	void setNext(Middleware<T> next);

	Middleware<T> getNext();

	void setPrev(Middleware<T> prev);

	Middleware<T> getPrev();
	
	CompletableFuture<T> onCompleted(T value);
	
	BiFunction<T, Throwable, T> onException(T value);

	default Middleware<T> find(String name){
		Middleware<T> current = this;
		while(!getName().equals(name)) {
			current = this.getNext();
			if(current == null) {
				break;
			}
		}
		return current;
	}

	default CompletableFuture<T> onNext(Middleware<T> handler, T value) {
		return apply(value).thenCompose(res -> handler != null 
				? handler.onNext(handler.getNext(), res)
				: onCompleted(res)).handle(onException(value)::apply);
	}
}