package com.netease.vendor.service.bean.result;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;


public class LoginResult extends BeanAncestor {

	private int resCode;
	private int retType;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_USER;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_USER_LOGIN;
	}

	public int getRetType() {
		return retType;
	}

	public void setRetType(int retType) {
		this.retType = retType;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}
}
