package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.RollbackCoffeeIndent;
import com.netease.vendor.service.core.ResendRequestTask;
import com.netease.vendor.service.protocol.request.Request;
import com.netease.vendor.util.TimeUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

public class VerifyCoffeeRetryTask extends ResendRequestTask {

	public VerifyCoffeeRetryTask(Request request) {
		super(request);
	}

	@Override
	public void onTimeout() {	
		
		LogUtil.e("VerifyCoffeeRetryTask", "verify code request timeout");
		
		// revoke the request
		VerifyCoffeeRequest request = (VerifyCoffeeRequest)getRequest();
		String coffeeIndent = request.getCoffeeIndent();
		boolean isRetry = request.isRetry();
		LogUtil.e("VerifyCoffeeRetryTask", "coffeeIndent :" + coffeeIndent);
		
		// notify
		Remote remote = new Remote();
		remote.setWhat(ITranCode.ACT_COFFEE);
		remote.setAction(ITranCode.ACT_COFFEE_INDENT_TIME_OUT);
		remote.setBody(coffeeIndent);
		core.notifyListener(remote);

		// send the roll back request
		long rbTimestamp = TimeUtil.getNow_millisecond();
		RollbackCoffeeIndent info = new RollbackCoffeeIndent();
    	info.setUid(U.getMyVendorNum());
    	info.setCoffeeIndent(coffeeIndent);
    	info.setTimestamp(rbTimestamp);
    	info.setRetry(isRetry);
    	core.sendPacket(info.toRemote());
	}
}
