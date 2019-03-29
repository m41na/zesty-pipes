package com.practicaldime.zesty.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class MiddlewareChain <T> implements MiddlewareLifecycle{

	private Middleware<T> head;
	private Middleware<T> tail;
	private MiddlewareLifecycle listener;
	
	public void listener(MiddlewareLifecycle listener) {
		this.listener = listener;
	}
	
	public void register(Middleware<T> action) {
		if(head == null && tail == null) {
			tail = action;
		}
		else if(head == null) {
			head = action;
			tail.setNext(head);
			head.setPrev(tail);
		}
		else {
			action.setPrev(head);
			head.setNext(action);
			head = action;
		}
		onRegistered(action.getName());
	}
	
	public void unregister(Middleware<T> action) {
		if(tail != null){
			Middleware<T> matched = tail.find(action.getName());
			if(matched != null) {
				if(matched.getPrev() != null) {
					matched.getPrev().setNext(matched.getNext());
				}
				if(matched.getNext() != null) {
					matched.getNext().setPrev(matched.getPrev());
				}
				if(matched == tail) {
					tail = matched.getNext();
				}
				if(matched == head) {
					head = head.getPrev();
				}
				if(head == tail) {
					head = null;
				}
				onUnregistered(matched.getName());
				matched = null;
			}
		}
	}
	
	public CompletableFuture<T> start(T context) {
		return tail != null
				? tail.onNext(tail.getNext(), context)
				: CompletableFuture.completedFuture(context);
	}
	
	public int size() {
		int len = 0;
		Middleware<T> current = tail;
		if(current != null) {
			while(current.getNext() != null) {
				len++;
				current = current.getNext();
			}
		}
		return len;
	}
	
	public Collection<String> names(){
		Collection<String> list = new ArrayList<>();
		Middleware<T> current = tail;
		if(current != null) {
			while(current.getNext() != null) {
				list.add(current.getName());
				current = current.getNext();
			}
		}
		return list;
	}

	@Override
	public void onRegistered(String name) {
		if(listener != null) {
			listener.onRegistered(name);
		}
		System.out.printf("handler registered -> %s%n", name);
	}

	@Override
	public void onUnregistered(String name) {
		if(listener != null) {
			listener.onUnregistered(name);
		}
		System.out.printf("handler un-registered -> %s%n", name);
	}
}
