package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.GetMachineConfigResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.GetMachineConfigResponse;
import com.netease.vendor.util.log.LogUtil;

public class GetMachineConfigResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		core.cancelRequestRetryTimer(response.getLinkFrame().serialId);

		GetMachineConfigResult result = new GetMachineConfigResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			GetMachineConfigResponse coffeeResponse = (GetMachineConfigResponse) response;
			String workTemp = coffeeResponse.getWorkTemp();
			String keepTemp = coffeeResponse.getKeepTemp();
			String washTime = coffeeResponse.getWashTime();
            LogUtil.e("vendor", "[GetMachineConfigResponse]" + "workTemp: " + workTemp + ", keepTemp = "
					+ keepTemp + ", washTime = " + washTime);
			result.setWorkTemp(workTemp);
			result.setKeepTemp(keepTemp);
			result.setWashTime(washTime);
		} 

		postToUI(result.toRemote());
	}
}
