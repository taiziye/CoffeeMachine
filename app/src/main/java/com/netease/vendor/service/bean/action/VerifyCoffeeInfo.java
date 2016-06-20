package com.netease.vendor.service.bean.action;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;

public class VerifyCoffeeInfo extends BeanAncestor {
		
	private String uid;
	
	private String coffeeIndent;
	
	private long timestamp;
	
	private boolean isRetry;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_VERIFY_COFFEE;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCoffeeIndent() {
		return coffeeIndent;
	}

	public void setCoffeeIndent(String coffeeIndent) {
		this.coffeeIndent = coffeeIndent;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isRetry() {
		return isRetry;
	}

	public void setRetry(boolean isRetry) {
		this.isRetry = isRetry;
	}
	
}