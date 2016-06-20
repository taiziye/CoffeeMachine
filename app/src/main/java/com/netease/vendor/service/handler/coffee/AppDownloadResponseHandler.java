package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.AppDownloadResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.AppDownloadResponse;
import com.netease.vendor.util.log.LogUtil;

public class AppDownloadResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		AppDownloadResult result = new AppDownloadResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			AppDownloadResponse coffeeResponse = (AppDownloadResponse) response;
			String iosDownloadURL = coffeeResponse.getIosDownloadUrl();
			String androidDownloadURL = coffeeResponse.getAndroidDownloadUrl();
            LogUtil.e("vendor", "[AppDownloadResponse]" + "iosDownloadURL: " + iosDownloadURL + ", androidURL = " + androidDownloadURL);
			result.setAndroidDownloadURL(androidDownloadURL);
			result.setIosDownloadURL(iosDownloadURL);
		} 

		postToUI(result.toRemote());
	}
}
