package com.practicaldime.zesty.servlet;

import java.util.concurrent.CompletableFuture;

public interface Middleware<T> extends HandlerAction<T, T> {

	String getName();

	void setNext(Middleware<T> next);

	Middleware<T> getNext();

	void setPrev(Middleware<T> prev);

	Middleware<T> getPrev();

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
		return apply(value).handle((res, th) -> {
			if (res != null) {
				return res;
			} else {
				System.out.println(th.getMessage());
				return null;
			}
		}).thenCompose(res -> handler != null 
				? handler.onNext(handler.getNext(), res)
				: CompletableFuture.completedFuture(res));
	}
}