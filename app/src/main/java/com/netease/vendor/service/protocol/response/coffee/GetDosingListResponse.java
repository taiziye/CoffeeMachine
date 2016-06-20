package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.marshal.ArrayMable;
import com.netease.vendor.service.protocol.marshal.Property;
import com.netease.vendor.service.protocol.pack.PackIndex;
import com.netease.vendor.service.protocol.pack.Unpack;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.GET_DOSING
		+ "" })

public class GetDosingListResponse extends Response {

    @PackIndex(0)
	private ArrayMable coffeeDosingList;
	
	public Unpack unpackBody(Unpack unpack) throws Exception {
        setCoffeeDosingList(new ArrayMable(Property.class));
        coffeeDosingList.unmarshal(unpack);
		return null;
	}

    public ArrayMable getCoffeeDosingList() {
        return coffeeDosingList;
    }

    public void setCoffeeDosingList(ArrayMable coffeeDosingList) {
        this.coffeeDosingList = coffeeDosingList;
    }
}
