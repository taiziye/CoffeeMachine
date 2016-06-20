package com.netease.vendor.service.bean.action;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;

public class PayQrcodeCartInfo extends BeanAncestor {
		
	private String uid;
	private String coffeeIndents;
	private short provider;
	
	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_PAY_QRCODE_CART;
	}

	public String getCoffeeIndents() {
		return coffeeIndents;
	}

	public void setCoffeeIndents(String coffeeIndents) {
		this.coffeeIndents = coffeeIndents;
	}

	public short getProvider() {
		return provider;
	}

	public void setProvider(short provider) {
		this.provider = provider;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
}