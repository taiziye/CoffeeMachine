package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.bean.result.PayQrcodeResult;
import com.netease.vendor.service.core.ResendRequestTask;
import com.netease.vendor.service.protocol.request.Request;
import com.netease.vendor.service.protocol.ResponseCode;
import com.netease.vendor.util.log.LogUtil;

public class PayQrcodeRetryTask extends ResendRequestTask {

	public PayQrcodeRetryTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {	
		
		LogUtil.e("PayQrcodeRetryTask", "pay qrcode request timeout");
		
		PayQrcodeResult result = new PayQrcodeResult();
		result.setResCode(ResponseCode.RES_ETIMEOUT);
		core.notifyListener(result.toRemote());
	}
}
