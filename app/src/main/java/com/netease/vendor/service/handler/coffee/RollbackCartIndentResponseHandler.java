package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.RollbackCartResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.response.Response;

public class RollbackCartIndentResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

		RollbackCartResult result = new RollbackCartResult();
		result.setResCode(response.getLinkFrame().resCode);
		postToUI(result.toRemote());
	}
}
