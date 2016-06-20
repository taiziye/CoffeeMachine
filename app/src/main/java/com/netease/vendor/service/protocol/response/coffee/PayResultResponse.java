package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Unpack;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.PAY_RESULT
		+ "" })
public class PayResultResponse extends Response {

	private String coffeeindent;
	
	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		this.setCoffeeindent(unpack.popVarstr());
		return null;
	}

	public String getCoffeeindent() {
		return coffeeindent;
	}

	public void setCoffeeindent(String coffeeindent) {
		this.coffeeindent = coffeeindent;
	}
}
