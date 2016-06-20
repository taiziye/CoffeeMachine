package com.netease.vendor.service.action;

import com.netease.vendor.service.Remote;
import com.netease.vendor.service.core.VendorCore;

public abstract class TAction implements IAction{
	
	public VendorCore core = VendorCore.sharedInstance();
	
	public void post2UI(Remote remote){
		core.notifyListener(remote);
	}
}
