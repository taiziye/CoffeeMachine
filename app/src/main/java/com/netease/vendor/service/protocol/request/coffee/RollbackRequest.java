package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Pack;
import com.netease.vendor.service.protocol.request.Request;

public class RollbackRequest extends Request {
	
	private String coffeeIndent;
	private long timestamp;
	private boolean isRetry;
	
	public RollbackRequest(String uid, String coffeeIndent, long timestamp, boolean isRetry) {
		super(uid);
		setCoffeeIndent(coffeeIndent);
		setTimestamp(timestamp);
		setRetry(isRetry);
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putVarstr(coffeeIndent);
        pack.putLong(timestamp);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.ROLL_BACK;
	}

	public String getCoffeeIndent() {
		return coffeeIndent;
	}

	public void setCoffeeIndent(String coffeeIndent) {
		this.coffeeIndent = coffeeIndent;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isRetry() {
		return isRetry;
	}

	public void setRetry(boolean isRetry) {
		this.isRetry = isRetry;
	}
}
