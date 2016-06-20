package com.netease.vendor.beans;

import android.content.Context;

import com.netease.vendor.R;
import com.netease.vendor.common.adapter.TListItem;
import com.netease.vendor.service.domain.CoffeeInfo;

public class CartPayItem extends TListItem {

	private CoffeeInfo coffeeInfo;

	private int buyNum;

	private int sugarLevel;

	public CoffeeInfo getCoffeeInfo() {
		return coffeeInfo;
	}

	public void setCoffeeInfo(CoffeeInfo coffeeInfo) {
		this.coffeeInfo = coffeeInfo;
	}

	public int getBuyNum() {
		return buyNum;
	}

	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}

	public int getSugarLevel() {
		return sugarLevel;
	}

	public String getSugarLevelDescri(Context context){
		String description = "";
		switch(sugarLevel){
			case 1:
				description = context.getString(R.string.pay_add_no_sugar);
				break;
			case 2:
				description = context.getString(R.string.pay_add_little_sugar);
				break;
			case 3:
				description = context.getString(R.string.pay_add_middle_sugar);
				break;
			case 4:
				description = context.getString(R.string.pay_add_more_sugar);
				break;
		}

		return description;
	}

	public void setSugarLevel(int sugarLevel) {
		this.sugarLevel = sugarLevel;
	}
}
