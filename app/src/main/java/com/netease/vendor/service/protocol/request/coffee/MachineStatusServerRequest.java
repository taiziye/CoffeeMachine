package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Pack;
import com.netease.vendor.service.protocol.request.Request;

public class MachineStatusServerRequest extends Request {

	private long timestamp;
	private String machineInfo;
	
	public MachineStatusServerRequest(String uid, long timestamp, String machineInfo) {
		super(uid);
		this.timestamp = timestamp;
		this.machineInfo = machineInfo;
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(machineInfo);
        pack.putLong(timestamp);
        
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.MACHINE_STATUS_SERVER;
	}
}
