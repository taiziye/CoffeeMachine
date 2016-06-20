package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Unpack;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.GET_DISCOUNT
		+ "" })
public class GetDiscountResponse extends Response {

	public String getFavorable() {
		return favorable;
	}

	public void setFavorable(String favorable) {
		this.favorable = favorable;
	}

	private String favorable;

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		this.favorable = unpack.popVarstr();
		return null;
	}
}
