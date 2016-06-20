package com.netease.vendor.service.bean;

import com.netease.vendor.service.Remote;
import com.netease.vendor.service.domain.Ancestor;


public abstract class BeanAncestor extends Ancestor {
	
	public abstract int getWhat();
	public abstract int getAction();
	
	public Remote toRemote() {
		Remote remote = new Remote();
		remote.setWhat(getWhat());
		remote.setAction(getAction());
		remote.setBody(toString());
		
		return remote;
	}

	private void fromRemote(Remote remote) {
		
	}
}
