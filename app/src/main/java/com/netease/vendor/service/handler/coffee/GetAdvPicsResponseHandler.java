package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.GetAdvPicsResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.GetAdvPicResponse;

public class GetAdvPicsResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

        GetAdvPicsResult result = new GetAdvPicsResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
            GetAdvPicResponse advPicResponse = (GetAdvPicResponse) response;
            String advPicsUrlJson = advPicResponse.getAdvPics();
            result.setAdvImgUrls(advPicsUrlJson);
        }

        postToUI(result.toRemote());
	}
}
