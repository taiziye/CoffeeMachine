package com.netease.vendor.service.bean.action;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;

public class LoginInfo extends BeanAncestor {

	private static final long serialVersionUID = -858794333301795277L;
	
	private String uid;
	private String password; 
		
	@Override
	public int getWhat() {
		return ITranCode.ACT_USER;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_USER_LOGIN;
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}