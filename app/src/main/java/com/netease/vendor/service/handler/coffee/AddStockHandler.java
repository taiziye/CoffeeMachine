package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.AddStockResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.response.Response;

public class AddStockHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

        AddStockResult result = new AddStockResult();
        result.setResCode(response.getLinkFrame().resCode);

		postToUI(result.toRemote());
	}
}
