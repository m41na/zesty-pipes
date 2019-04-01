package com.practicaldime.zesty.chain;

import java.util.concurrent.CompletableFuture;

import com.practicaldime.zesty.servlet.AbstractMiddleware;
import com.practicaldime.zesty.servlet.RequestContext;

public class MultipartFilter extends AbstractMiddleware<RequestContext>{

	@Override
	public String getName() {
		return "multipart_filter";
	}

	@Override
	public CompletableFuture<RequestContext> apply(RequestContext context) {
		// TODO Auto-generated method stub
		// for multipart/form-data, customize the servlet holder
//				if (type.toLowerCase().contains("multipart/form-data")) {
//					MultipartConfigElement mpce = new MultipartConfigElement("temp", 1024 * 1024 * 50, 1024 * 1024, 5);
//					holder.getRegistration().setMultipartConfig(mpce);
//				}
		return onCompleted(context);
	}
}
