package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Unpack;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.GET_MACHINE_CONFIG
		+ "" })
public class GetMachineConfigResponse extends Response {

	private String workTemp;
	private String keepTemp;
	private String washTime;
	
	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		workTemp = unpack.popVarstr();
		keepTemp = unpack.popVarstr();
		washTime = unpack.popVarstr();
		return null;
	}

	public String getWashTime() {
		return washTime;
	}

	public void setWashTime(String washTime) {
		this.washTime = washTime;
	}

	public String getWorkTemp() {
		return workTemp;
	}

	public void setWorkTemp(String workTemp) {
		this.workTemp = workTemp;
	}

	public String getKeepTemp() {
		return keepTemp;
	}

	public void setKeepTemp(String keepTemp) {
		this.keepTemp = keepTemp;
	}
}
