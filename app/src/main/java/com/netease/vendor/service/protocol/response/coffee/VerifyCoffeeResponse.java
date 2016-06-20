package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.PackIndex;
import com.netease.vendor.service.protocol.response.ResponseID;
import com.netease.vendor.service.protocol.response.SingleResponse;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.VERIFY_CODE
		+ "" })
public class VerifyCoffeeResponse extends SingleResponse {

	@PackIndex(0)
	private int coffeeId;
	@PackIndex(1)
	private String dosingContent;
	@PackIndex(2)
	private long timestamp;
	
	public int getCoffeeId() {
		return coffeeId;
	}
	
	public void setCoffeeId(int coffeeId) {
		this.coffeeId = coffeeId;
	}
	
	public String getDosingContent() {
		return dosingContent;
	}
	
	public void setDosingContent(String dosingContent) {
		this.dosingContent = dosingContent;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
