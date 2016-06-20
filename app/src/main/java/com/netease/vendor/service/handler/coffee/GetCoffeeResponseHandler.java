package com.netease.vendor.service.handler.coffee;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.netease.vendor.service.bean.result.GetCoffeeResult;
import com.netease.vendor.service.bean.result.GetDiscountResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.domain.CoffeeInfo;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.marshal.Marshallable;
import com.netease.vendor.service.protocol.marshal.Property;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.GetCoffeeResponse;
import com.netease.vendor.util.log.LogUtil;

public class GetCoffeeResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {
		
		core.cancelRequestRetryTimer(response.getLinkFrame().serialId);
		
		GetCoffeeResult result = new GetCoffeeResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
			GetCoffeeResponse coffeeResponse = (GetCoffeeResponse) response;
			// 咖啡信息
			List<Marshallable> coffees = coffeeResponse.getCoffeeInfos().list;
			LogUtil.vendor("收到咖啡数为:" + coffees.size());
			for(int i = 0; i < coffees.size(); i++) {
				Property coffee = (Property) coffees.get(i);
				int id = coffee.getInteger(ICoffeeService.CoffeeType.COFFEE_TYPE_ID);
				String title = coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_TITLE);
				double price = Double.parseDouble(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_PRICE));
                double discount = Double.parseDouble(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_DISCOUNT));
				String imgURL = coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_IMGURL);
				int soldNum = coffee.getInteger(ICoffeeService.CoffeeType.COFFEE_TYPE_SOLD_NUM);
				String dosing = coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_DOSING);
                //new added
//              double discount_wx = Double.parseDouble(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_DISCOUNT_WX));
//              double discount_ali = Double.parseDouble(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_DISCOUNT_ALIPAY));
                double volume = Double.parseDouble(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_VOLUME));
                boolean isNew = Boolean.parseBoolean(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_IS_NEW));
                boolean isHot = Boolean.parseBoolean(coffee.get(ICoffeeService.CoffeeType.COFFEE_TYPE_IS_HOT));

                LogUtil.vendor("[GetCoffeeResponse]" + "coffeeID:" + id + ", coffeeTitle: " + title);
                LogUtil.vendor("[GetCoffeeResponse]" + "imgURL: " + imgURL);
                LogUtil.vendor("[GetCoffeeResponse]" + dosing);
				
				CoffeeInfo info = new CoffeeInfo();
				info.setCoffeeId(id);
				info.setCoffeeTitle(title);
				info.setPrice(price);
				info.setImgUrl(imgURL);
				info.setSoldNum(soldNum);
                info.setDiscount(discount);
//              info.setDiscount_wx(discount_wx);
//              info.setDiscount_ali(discount_ali);
                info.setVolume(volume);
                info.setHot(isHot);
                info.setNew(isNew);
				
				try{
					JSONArray array = JSON.parseArray(dosing);  
					if (array != null && array.size() > 0) {
						int size = array.size();
						for(int j = 0; j < size; ++j) {
							CoffeeDosingInfo cdi = new CoffeeDosingInfo();
							cdi.fromJSONString(array.getJSONObject(j));
							info.addDosingInfo(cdi);
						}					
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
				
				result.addCoffee(info);
			}

			// 促销信息
			String favorable = coffeeResponse.getFavorable();
			LogUtil.vendor("[GetCoffeeResponse]" + "favorable:" + favorable);
			GetDiscountResult discountInfo = new GetDiscountResult();
			discountInfo.setFavorable(favorable);
			result.setDiscountInfo(discountInfo);
		} 

		postToUI(result.toRemote());
	}
}
