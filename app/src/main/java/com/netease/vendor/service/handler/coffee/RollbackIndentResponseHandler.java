package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.RollbackResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.request.coffee.RollbackRequest;
import com.netease.vendor.service.protocol.request.coffee.RollbackRetryTask;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.RollbackResponse;
import com.netease.vendor.util.log.LogUtil;

public class RollbackIndentResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		RollbackRetryTask task = (RollbackRetryTask) core.cancelRequestRetryTimer(
				response.getLinkFrame().serialId);
		String coffeeIndent = null;
		long rbTimestamp = 0;
		boolean isRetry = false;
		if (task != null) {
			RollbackRequest request = (RollbackRequest)task.getRequest();
			if(request != null){
				coffeeIndent = request.getCoffeeIndent();
				rbTimestamp = request.getTimestamp();
				isRetry = request.isRetry();
			}
		}
		
		if (response.isSuccess()) {
			RollbackResponse rollbackResponse = (RollbackResponse) response;
			long rbResponseTimestamp = rollbackResponse.getRbTimestamp();
			long qhTimestamp = rollbackResponse.getQhTimestamp();
			LogUtil.vendor("Rollback TimeStamp: " + rbTimestamp + ",Quhuo TimeStamp: " + qhTimestamp);
			
			if(rbTimestamp != rbResponseTimestamp){
				LogUtil.e("RollbackIndentResponseHandler", "request timestamp don't equal the server response");
				return;
			}
			
			// notify
			RollbackResult result = new RollbackResult();
			result.setResCode(rollbackResponse.getLinkFrame().resCode);
			result.setCoffeeIndent(coffeeIndent);
			result.setRetry(isRetry);
			postToUI(result.toRemote());
		}
	}
}
