package com.netease.vendor.service.bean.result;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;

public class PayStatusAskResult extends BeanAncestor {

	private int resCode;

    private String coffeeIndent;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_ASK_PAY_RESULT;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

    public String getCoffeeIndent() {
        return coffeeIndent;
    }

    public void setCoffeeIndent(String coffeeIndent) {
        this.coffeeIndent = coffeeIndent;
    }
}
