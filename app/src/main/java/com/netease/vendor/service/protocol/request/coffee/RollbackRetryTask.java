package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.core.ResendRequestTask;
import com.netease.vendor.service.protocol.request.Request;

public class RollbackRetryTask extends ResendRequestTask {

	public RollbackRetryTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {	
		// make it more intelligent
	}
}
