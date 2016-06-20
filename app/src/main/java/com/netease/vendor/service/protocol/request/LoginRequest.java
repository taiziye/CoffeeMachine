package com.netease.vendor.service.protocol.request;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.IAuthService;
import com.netease.vendor.service.protocol.pack.Pack;

public class LoginRequest extends Request {

	private String password;
	
	public LoginRequest(String uid) {
		super(uid);
	}

	@Override
	public Pack packRequest() {
		Pack p = new Pack();
		p.putVarstr(uid);
		p.putVarstr(password);
		return p;
	}

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_AUTH;
	}

	@Override
	public short getCommandId() {
		return IAuthService.CommandId.CID_LOGIN;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
