package com.netease.vendor.service.bean;

import com.netease.vendor.service.ITranCode;

public class RebootNotify extends BeanAncestor {

	private int status;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_SYS;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_SYS_REBOOT;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
