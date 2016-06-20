package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.PackIndex;
import com.netease.vendor.service.protocol.response.ResponseID;
import com.netease.vendor.service.protocol.response.SingleResponse;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.ROLL_BACK
		+ "" })
public class RollbackResponse extends SingleResponse {

	@PackIndex(0)
	private long rbTimestamp;
	@PackIndex(1)
	private long qhTimestamp;

	public long getRbTimestamp() {
		return rbTimestamp;
	}

	public void setRbTimestamp(long rbTimestamp) {
		this.rbTimestamp = rbTimestamp;
	}

	public long getQhTimestamp() {
		return qhTimestamp;
	}

	public void setQhTimestamp(long qhTimestamp) {
		this.qhTimestamp = qhTimestamp;
	}
}
