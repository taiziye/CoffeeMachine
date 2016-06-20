package com.netease.vendor.service.handler;

import com.netease.vendor.service.protocol.enums.IAuthService;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.UserKickedResponse;

public class UserKickedResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		UserKickedResponse kickResponse = (UserKickedResponse)response;
		if (kickResponse.isSuccess()) {
			core.handleKickout(IAuthService.KickoutReason.Normal);
		}
	}

}
