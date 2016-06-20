package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Pack;
import com.netease.vendor.service.protocol.request.Request;

public class PayStatusAskRequest extends Request {
	
	private String coffeeindent;
	
	public PayStatusAskRequest(String uid, String coffeeindent) {
		super(uid);
		this.coffeeindent = coffeeindent;
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(coffeeindent);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.ASK_PAY_STATUS;
	}

	public String getCoffeeindent() {
		return coffeeindent;
	}

	public void setCoffeeindent(String coffeeindent) {
		this.coffeeindent = coffeeindent;
	}
}
