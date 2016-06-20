package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Pack;
import com.netease.vendor.service.protocol.request.Request;

public class CancelTradeCartRequest extends Request {

	private String payIndent;

	public CancelTradeCartRequest(String uid, String payIndent) {
		super(uid);
		setPayIndent(payIndent);
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
		return ICoffeeService.CommandId.CANCEL_TRADE_CART;
	}

	public String getPayIndent() {
		return payIndent;
	}

	public void setPayIndent(String payIndent) {
		this.payIndent = payIndent;
	}

}
