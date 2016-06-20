package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.PaySonicWaveResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.PaySonicWaveResponse;
import com.netease.vendor.util.log.LogUtil;

public class PaySonicWaveResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		PaySonicWaveResult result = new PaySonicWaveResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			PaySonicWaveResponse paySonicWaveResponse = (PaySonicWaveResponse) response;
			String coffeeIndent = paySonicWaveResponse.getCoffeeIndent();
			String tradeNO = paySonicWaveResponse.getTradeNO();
            LogUtil.vendor("PaySonicWave -> " + "[coffeeIndent-" + coffeeIndent + "; tradeNO-" + tradeNO);
			
			result.setCoffeeIndent(coffeeIndent);
			result.setTradeNo(tradeNO);
		}
		
		postToUI(result.toRemote());
	}
}
