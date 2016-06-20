package com.netease.vendor.action;

import android.content.Intent;

import com.netease.vendor.activity.LoginActivity;
import com.netease.vendor.application.GlobalCached;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.common.action.TAction;
import com.netease.vendor.helper.cache.BaseDataCacher;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.util.log.LogUtil;

public class UserAction extends TAction {

	@Override
	public void execute(Remote remote) {
		send(remote);
	}

	@Override
	public void receive(Remote remote) {
		LogUtil.vendor("UserAction receive...");
		notifyAll(remote);
		
		if (remote.getAction() == ITranCode.ACT_USER_LOGOUT) {
			onActLogout(remote);
		}
	}

	@Override
	public int getKey() {
		return ITranCode.ACT_USER;
	}
	
	private void onActLogout(Remote remote) {
		// clear cache
		GlobalCached.clear();
		BaseDataCacher.clearCache();
		// back to login page
		Intent intent = new Intent();
		intent.setClass(MyApplication.Instance().getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MyApplication.Instance().getApplicationContext().startActivity(intent);
	}
}
