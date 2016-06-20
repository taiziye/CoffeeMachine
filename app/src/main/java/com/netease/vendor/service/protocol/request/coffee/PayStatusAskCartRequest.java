package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Pack;
import com.netease.vendor.service.protocol.request.Request;

public class PayStatusAskCartRequest extends Request {

	private String payIndent;

	public PayStatusAskCartRequest(String uid, String payIndent) {
		super(uid);
		this.payIndent = payIndent;
	}

	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(payIndent);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.ASK_PAY_STATUS_CART;
	}

	public String getPayIndent() {
		return payIndent;
	}

	public void setPayIndent(String payIndent) {
		this.payIndent = payIndent;
	}
}
