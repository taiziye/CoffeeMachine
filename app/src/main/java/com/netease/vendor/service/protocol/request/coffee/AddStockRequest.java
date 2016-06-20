package com.netease.vendor.service.protocol.request.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Pack;
import com.netease.vendor.service.protocol.request.Request;

public class AddStockRequest extends Request {

    private int userID;

    private String inventory;

	public AddStockRequest(String uid, int userID, String inventory) {
		super(uid);
        this.userID = userID;
		this.inventory = inventory;
	}
	
	@Override
    public Pack packRequest() {
        Pack pack = new Pack();
        pack.putInt(userID);
        pack.putVarstr(inventory);
        return pack;
    }

	@Override
	public short getServiceId() {
		return ServiceID.SVID_LITE_COFFEE;
	}

	@Override
	public short getCommandId() {
		return ICoffeeService.CommandId.ADD_STOCK;
	}

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
