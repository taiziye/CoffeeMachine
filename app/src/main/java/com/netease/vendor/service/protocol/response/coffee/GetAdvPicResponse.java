package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Unpack;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.GET_ADV_PIC
		+ "" })
public class GetAdvPicResponse extends Response {

	public String getAdvPics() {
		return advPics;
	}

	public void setAdvPics(String advPics) {
		this.advPics = advPics;
	}

	private String advPics;

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		this.advPics = unpack.popVarstr();
		return null;
	}
}
