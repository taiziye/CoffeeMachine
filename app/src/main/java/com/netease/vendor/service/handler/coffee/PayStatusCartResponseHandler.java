package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.PayStatusAskCartResult;
import com.netease.vendor.service.core.ResendRequestTask;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.request.coffee.PayStatusAskCartRequest;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.util.log.LogUtil;

public class PayStatusCartResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
        ResendRequestTask task = (ResendRequestTask) core.cancelRequestRetryTimer(
                response.getLinkFrame().serialId);
        String payIndent = null;
        if (task != null) {
            PayStatusAskCartRequest request = (PayStatusAskCartRequest)task.getRequest();
            payIndent = request.getPayIndent();
            LogUtil.vendor("PayStatusResponse-> retrive for PayStatusAskCartRequest, indent is " + payIndent);
        }

		if (response.isSuccess() && payIndent!= null) {
            PayStatusAskCartResult result = new PayStatusAskCartResult();
            result.setResCode(response.getLinkFrame().resCode);
            result.setPayIndent(payIndent);
			postToUI(result.toRemote());
		}
	}
}
