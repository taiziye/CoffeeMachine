package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.bean.result.PayQrcodeCartResult;
import com.netease.vendor.service.core.ResendRequestTask;
import com.netease.vendor.service.protocol.ResponseCode;
import com.netease.vendor.service.protocol.request.Request;
import com.netease.vendor.util.log.LogUtil;

public class PayQrcodeCartRetryTask extends ResendRequestTask {

	public PayQrcodeCartRetryTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {	
		
		LogUtil.e("PayQrcodeCartRetryTask", "pay qrcode request timeout");

		PayQrcodeCartResult result = new PayQrcodeCartResult();
		result.setResCode(ResponseCode.RES_ETIMEOUT);
		core.notifyListener(result.toRemote());
	}
}
