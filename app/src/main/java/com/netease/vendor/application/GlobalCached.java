package com.netease.vendor.application;

import com.netease.vendor.util.U;

public class GlobalCached {
	
	public static String activeVendor = "";

	static{
		refreshCached();
	}
	
	public static void refreshCached() {
		try{
			activeVendor = U.queryAppSet(U.KEY_USER_VENDOR);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void clear() {
		activeVendor = "";
	}
}
