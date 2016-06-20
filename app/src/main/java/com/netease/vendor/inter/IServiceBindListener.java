package com.netease.vendor.inter;

public interface IServiceBindListener {

	public void onBindSuccess();
	
	public void onBindFailed(String errorMessage);
}
