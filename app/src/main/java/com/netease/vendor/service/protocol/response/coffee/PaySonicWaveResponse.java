package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.PackIndex;
import com.netease.vendor.service.protocol.response.ResponseID;
import com.netease.vendor.service.protocol.response.SingleResponse;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.PAY_ALI_SONICWAVE
		+ "" })
public class PaySonicWaveResponse extends SingleResponse {

	@PackIndex(0)
	private String coffeeIndent;
	@PackIndex(1)
	private String tradeNO;
	
	public String getCoffeeIndent() {
		return coffeeIndent;
	}

	public void setCoffeeIndent(String coffeeIndent) {
		this.coffeeIndent = coffeeIndent;
	}

	public String getTradeNO() {
		return tradeNO;
	}

	public void setTradeNO(String tradeNO) {
		this.tradeNO = tradeNO;
	}
}
