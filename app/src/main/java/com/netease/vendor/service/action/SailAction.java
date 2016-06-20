package com.netease.vendor.service.action;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.util.log.LogUtil;

public class SailAction extends TAction {

	@Override
	public void execute(final Remote remote) {
		if (remote.getAction() == ITranCode.ACT_SYS_FIRE) {
			LogUtil.vendor("启动做事情...");
		}
	}

	public int getWhat() {
		return ITranCode.ACT_SYS;
	}
}
