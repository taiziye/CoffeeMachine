package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.UpdateStockResult;
import com.netease.vendor.service.core.ResendRequestTask;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.request.coffee.UpdateStockRequest;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.util.log.LogUtil;

public class UpdateStockHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        ResendRequestTask task = (ResendRequestTask) core.cancelRequestRetryTimer(
                response.getLinkFrame().serialId);
        boolean autoSycStock = false;
        if(task != null){
            UpdateStockRequest request = (UpdateStockRequest)task.getRequest();
            autoSycStock = request.isAuto();
            LogUtil.vendor("UpdateStockResponse-> retrive for UpdateStockRequest, auto is " + autoSycStock);
        }

        UpdateStockResult result = new UpdateStockResult();
        result.setAuto(autoSycStock);
        result.setResCode(response.getLinkFrame().resCode);

		postToUI(result.toRemote());
	}
}
