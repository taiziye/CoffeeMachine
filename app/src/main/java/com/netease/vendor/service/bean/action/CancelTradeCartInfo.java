package com.netease.vendor.service.bean.action;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;

public class CancelTradeCartInfo extends BeanAncestor {
		
	private String uid;

	private String payIndent;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_CANCEL_TRADE_CART;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPayIndent() {
		return payIndent;
	}

	public void setPayIndent(String payIndent) {
		this.payIndent = payIndent;
	}
}