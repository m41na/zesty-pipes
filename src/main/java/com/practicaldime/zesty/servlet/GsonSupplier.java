package com.practicaldime.zesty.servlet;

import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSupplier implements Supplier<Gson> {

	private final Gson gson;

	public GsonSupplier() {
		super();
		String pattern = "MMM dd, yyyy";
		this.gson = new GsonBuilder().setDateFormat(pattern).create();	
	}

	@Override
	public Gson get() {
		return this.gson;
	}
}
