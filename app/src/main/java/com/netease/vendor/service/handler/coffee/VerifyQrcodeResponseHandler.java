package com.netease.vendor.service.handler.coffee;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.netease.vendor.service.bean.result.VerifyQrcodeResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.VerifyQrcodeResponse;
import com.netease.vendor.util.log.LogUtil;

public class VerifyQrcodeResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		VerifyQrcodeResult result = new VerifyQrcodeResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			VerifyQrcodeResponse verifyResponse = (VerifyQrcodeResponse) response;
			int coffeeID = verifyResponse.getCoffeeId();
			String dosingContent = verifyResponse.getDosingContent();
			String user = verifyResponse.getUser();
			String coffeeIndent = verifyResponse.getCoffeeIndent();
			
			LogUtil.vendor("VerifyQrcodeResponse-> coffeeID:" + coffeeID + ", dosingContent:" + dosingContent +
					", user:" + user + ", coffeeIndent:" + coffeeIndent);

			result.setCoffeeId(coffeeID);
			result.setUser(user);
			result.setCoffeeIndent(coffeeIndent);
			
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
