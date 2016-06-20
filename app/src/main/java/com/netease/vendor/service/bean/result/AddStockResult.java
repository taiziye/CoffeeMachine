package com.netease.vendor.service.bean.result;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;

public class AddStockResult extends BeanAncestor {

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    private int resCode;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_STOCK_ADD;
	}
}
