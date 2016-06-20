package com.netease.vendor.service.protocol.response;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.IAuthService;
import com.netease.vendor.service.protocol.pack.PackIndex;

@ResponseID(service = ServiceID.SVID_LITE_AUTH, command = { IAuthService.CommandId.CID_LOGIN
		+ "" })
public class LoginResponse extends SingleResponse {

	@PackIndex(0)
	private short status;
	@PackIndex(1)
	private String sessionId;
    @PackIndex(2)
    private String vendorName;

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
