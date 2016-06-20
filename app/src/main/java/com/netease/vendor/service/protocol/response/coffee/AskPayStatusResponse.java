package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Unpack;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.ASK_PAY_STATUS
		+ "" })
public class AskPayStatusResponse extends Response {

	private Boolean result;
	
	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		this.result = unpack.popBoolean();
		return null;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}
}
