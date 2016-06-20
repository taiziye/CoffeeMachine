package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Pack;
import com.netease.vendor.service.protocol.request.Request;

public class CancelTradeRequest extends Request {

	private String coffeeIndent;

	public CancelTradeRequest(String uid, String coffeeIndent) {
		super(uid);
		setCoffeeIndent(coffeeIndent);
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(coffeeIndent);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.CANCEL_TRADE;
	}

	public String getCoffeeIndent() {
		return coffeeIndent;
	}

	public void setCoffeeIndent(String coffeeIndent) {
		this.coffeeIndent = coffeeIndent;
	}
}
