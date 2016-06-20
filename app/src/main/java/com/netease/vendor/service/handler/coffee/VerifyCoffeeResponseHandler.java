package com.netease.vendor.service.handler.coffee;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.netease.vendor.service.bean.result.VerifyCoffeeResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.request.coffee.VerifyCoffeeRequest;
import com.netease.vendor.service.protocol.request.coffee.VerifyCoffeeRetryTask;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.VerifyCoffeeResponse;
import com.netease.vendor.util.log.LogUtil;

public class VerifyCoffeeResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
        LogUtil.e("core", "response.getLinkFrame().serialId: " + response.getLinkFrame().serialId);
		VerifyCoffeeRetryTask task = (VerifyCoffeeRetryTask) core.cancelRequestRetryTimer(
				response.getLinkFrame().serialId);
		String coffeeIndent = null;
		long requestTimestamp = 0;
		if (task != null) {
			VerifyCoffeeRequest request = (VerifyCoffeeRequest)task.getRequest();
			coffeeIndent = request.getCoffeeIndent();
			requestTimestamp = request.getTimestamp();
		}

		VerifyCoffeeResult result = new VerifyCoffeeResult();
		result.setResCode(response.getLinkFrame().resCode);
		result.setCoffeeIndent(coffeeIndent);
		if (response.isSuccess()) {
			VerifyCoffeeResponse verifyResponse = (VerifyCoffeeResponse) response;
			int coffeeID = verifyResponse.getCoffeeId();
			String dosingContent = verifyResponse.getDosingContent();
            long serverTimestamp = verifyResponse.getTimestamp();
            LogUtil.e("core","VerifyCoffeeResponse->[coffeeID: " + coffeeID + "; dosingContent = " + dosingContent + "; serverTimestamp =" + serverTimestamp + "]");
			if(requestTimestamp != serverTimestamp){
				return;
			}

			result.setCoffeeId(coffeeID);
			result.setTimestamp(requestTimestamp);
			try{
				JSONArray array = JSON.parseArray(dosingContent);  
				if (array != null && array.size() > 0) {
					int size = array.size();
					for(int j = 0; j < size; ++j) {
						CoffeeDosingInfo cdi = new CoffeeDosingInfo();
						cdi.fromJSONString(array.getJSONObject(j));
						result.addDosingInfo(cdi);
					}					
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
		} 

		// notify UI
		postToUI(result.toRemote());
	}
}
