package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.GetDiscountResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.GetDiscountResponse;

public class GetDicountResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

        GetDiscountResult result = new GetDiscountResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
            GetDiscountResponse discountResponse = (GetDiscountResponse) response;

            String favorable = discountResponse.getFavorable();
            result.setFavorable(favorable);
        }

        postToUI(result.toRemote());
	}
}
