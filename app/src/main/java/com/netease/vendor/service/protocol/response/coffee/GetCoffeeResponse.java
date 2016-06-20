package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.marshal.ArrayMable;
import com.netease.vendor.service.protocol.marshal.Property;
import com.netease.vendor.service.protocol.pack.PackIndex;
import com.netease.vendor.service.protocol.pack.Unpack;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.GET_COFFEE
		+ "" })

public class GetCoffeeResponse extends Response {
	@PackIndex(0)
	private ArrayMable coffeeInfos;
	@PackIndex(1)
	private String favorable;

	
	public Unpack unpackBody(Unpack unpack) throws Exception {
		setCoffeeInfos(new ArrayMable(Property.class));
		coffeeInfos.unmarshal(unpack);
		this.favorable = unpack.popVarstr();
		return null;
	}

	public ArrayMable getCoffeeInfos() {
		return coffeeInfos;
	}

	public void setCoffeeInfos(ArrayMable coffeeInfos) {
		this.coffeeInfos = coffeeInfos;
	}

	public String getFavorable() {
		return favorable;
	}

	public void setFavorable(String favorable) {
		this.favorable = favorable;
	}
}
