package com.netease.vendor.application;

import java.util.List;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.GetCoffeeResult;
import com.netease.vendor.service.bean.result.GetDiscountResult;
import com.netease.vendor.service.domain.CoffeeInfo;

import android.os.Handler;
import android.os.Message;

public class DataCacheHandler extends Handler {
	@Override
	public void handleMessage(Message message) {		
		Remote remote = (Remote) message.obj;
		if (remote.getWhat() == ITranCode.ACT_COFFEE) {
			if (remote.getAction() == ITranCode.ACT_COFFEE_GET_COFFEE) {
				GetCoffeeResult result = GeneralActionResult.parseObject(remote.getBody());
				if(result != null && result.getResCode() == 200){
					// coffee information
					List<CoffeeInfo> coffees = result.getCoffees();
					if(coffees != null){
						MyApplication.Instance().getCoffeeInfos().clear();
						MyApplication.Instance().getCoffeeInfos().addAll(coffees);
					}
					// discount
					GetDiscountResult discountInfo = result.getDiscountInfo();
					MyApplication.Instance().setDiscountInfo(discountInfo);
				}
			}
		} 
	}
}
