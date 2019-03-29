package com.practicaldime.zesty.servlet;

public abstract class AbstractMiddleware<T> implements Middleware<T>{

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
}
