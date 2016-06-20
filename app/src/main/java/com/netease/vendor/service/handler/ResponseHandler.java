package com.netease.vendor.service.handler;

import com.netease.vendor.service.Remote;
import com.netease.vendor.service.core.VendorCore;
import com.netease.vendor.service.protocol.response.Response;


public abstract class ResponseHandler {
	abstract public void processResponse(Response response);
	
	protected VendorCore core = VendorCore.sharedInstance();
	protected void postToUI(Remote remote) {
		core.notifyListener(remote);
	}
}
