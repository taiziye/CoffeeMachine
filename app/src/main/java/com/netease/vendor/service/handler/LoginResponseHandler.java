package com.netease.vendor.service.handler;

import com.netease.vendor.service.bean.result.LoginResult;
import com.netease.vendor.service.protocol.enums.IAuthService;
import com.netease.vendor.service.protocol.response.LoginResponse;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.util.log.LogUtil;

public class LoginResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		core.cancelRequestRetryTimer(response.getLinkFrame().serialId);
		
		LoginResult result = new LoginResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			core.setLogined();
			
			LoginResponse loginResponse = (LoginResponse) response;
			int status = loginResponse.getStatus();
			if (status != IAuthService.LoginRetType.NOTUSER) {
				onAfterLogin();
			}
			result.setRetType(status);
			
			LogUtil.vendor("LoginRetType = " + status);
            LogUtil.vendor("Session Id = " + loginResponse.getSessionId());
			core.setMySessionId(loginResponse.getSessionId());
            core.setLastVendorName(loginResponse.getVendorName());
		} else {
			core.setMyVendorNum("");
			
			core.setLastVendorNum("");
            core.setLastVendorName("");
			core.setLastVendorPwd("");

			/**
			 * disconnect anyway
			 */
			core.disconnect();
		}

		postToUI(result.toRemote());
	}

	private void onAfterLogin() {
//		core.startReportStatusTimer();
	}
}
