package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.PayNotifyResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.PayNotifyResponse;
import com.netease.vendor.util.log.LogUtil;

public class PayNotifyResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		PayNotifyResult result = new PayNotifyResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			PayNotifyResponse payNotifyResponse = (PayNotifyResponse) response;
			
			String coffeeIndent = payNotifyResponse.getCoffeeindent();
			LogUtil.vendor("successfully receive payment notice for coffeeIndent: " + coffeeIndent);
			result.setCoffeeIndent(coffeeIndent);
		}
		
		postToUI(result.toRemote());
	}
}
