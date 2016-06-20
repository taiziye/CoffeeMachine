package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.PayQrcodeCartResult;
import com.netease.vendor.service.bean.result.PayQrcodeResult;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.request.coffee.PayQrcodeCartRetryTask;
import com.netease.vendor.service.protocol.request.coffee.PayQrcodeRetryTask;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.PayQrcodeCartResponse;
import com.netease.vendor.service.protocol.response.coffee.PayQrcodeResponse;
import com.netease.vendor.util.log.LogUtil;

public class PayQrcodeCartResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

		PayQrcodeCartRetryTask task = (PayQrcodeCartRetryTask) core.cancelRequestRetryTimer(
				response.getLinkFrame().serialId);
		
		PayQrcodeCartResult result = new PayQrcodeCartResult();
		result.setResCode(response.getLinkFrame().resCode);
		
		if (response.isSuccess()) {
            PayQrcodeCartResponse payQrcodeResponse = (PayQrcodeCartResponse) response;

            String payIndent = payQrcodeResponse.getPayIndent();
            String coffeeIndents = payQrcodeResponse.getCoffeeIndents();
            String QTUrl = payQrcodeResponse.getQrcodeUrl();
            String price = payQrcodeResponse.getPrice();
            String priceOri = payQrcodeResponse.getPriceOri();

            LogUtil.vendor("PayQrcodeCartResponse -> " + "payIndent: " + payIndent + "; coffeeIndents: " + coffeeIndents + "; QTUrl:" + QTUrl +
                    "; price:" + price + ", priceOri:" + priceOri);

            result.setPayIndent(payIndent);
            result.setCoffeeIndents(coffeeIndents);
            result.setQrCodeUrl(QTUrl);
            result.setPrice(price);
            result.setPriceOri(priceOri);
        }

        postToUI(result.toRemote());

    }
}
