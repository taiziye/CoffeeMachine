package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.request.coffee.MachineStatusServerRequest;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.util.TimeUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

public class MachineStatusServerResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		if (response.isSuccess()) {			
			LogUtil.vendor("receive machine status request from server");
			
			// to get something status
			String machineInfo = "hi, i'm ok";
			
			MachineStatusServerRequest request = new MachineStatusServerRequest(U.getMyVendorNum(), 
					TimeUtil.getNow_millisecond(), machineInfo);
			core.sendRequestToServer(request);
		}
	}
}
