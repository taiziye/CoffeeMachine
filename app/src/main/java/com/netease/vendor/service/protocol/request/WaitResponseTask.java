package com.netease.vendor.service.protocol.request;

import com.netease.vendor.service.core.ResendRequestTask;

public class WaitResponseTask extends ResendRequestTask {

	public WaitResponseTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {
		// do noting
	}

}
