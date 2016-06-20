package com.netease.vendor.service.bean.action;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;

public class AppDownloadInfo extends BeanAncestor {

	private static final long serialVersionUID = 3583021391822166462L;
	
	private String uid;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_APP_DOWNLOAD;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}