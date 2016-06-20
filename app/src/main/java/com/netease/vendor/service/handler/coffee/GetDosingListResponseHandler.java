package com.netease.vendor.service.handler.coffee;

import com.netease.vendor.service.bean.result.GetDosingResult;
import com.netease.vendor.service.core.ResendRequestTask;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.handler.ResponseHandler;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.marshal.Marshallable;
import com.netease.vendor.service.protocol.marshal.Property;
import com.netease.vendor.service.protocol.request.coffee.GetDosingListRequest;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.coffee.GetDosingListResponse;
import com.netease.vendor.util.log.LogUtil;

import java.util.List;

public class GetDosingListResponseHandler extends ResponseHandler {

	@Override
	public void processResponse(Response response) {

        ResendRequestTask task = (ResendRequestTask) core.cancelRequestRetryTimer(
                response.getLinkFrame().serialId);
        boolean autoSycStock = false;
        if(task != null){
            GetDosingListRequest request = (GetDosingListRequest)task.getRequest();
            autoSycStock = request.isAuto();
            LogUtil.vendor("GetDosingListResponse-> retrive for GetDosingListRequest, auto is " + autoSycStock);
        }

        GetDosingResult result = new GetDosingResult();
		result.setResCode(response.getLinkFrame().resCode);
        result.setAuto(autoSycStock);
		if (response.isSuccess()) {
            GetDosingListResponse dosingListResponse = (GetDosingListResponse) response;
			List<Marshallable> dosings = dosingListResponse.getCoffeeDosingList().list;
			LogUtil.vendor("收到配料种类为:" + dosings.size());
			for(int i = 0; i < dosings.size(); i++) {
				Property dosing = (Property) dosings.get(i);
				int id = dosing.getInteger(ICoffeeService.DosingType.COFFEE_TYPE_ID);
				String title = dosing.get(ICoffeeService.DosingType.COFFEE_TYPE_TITLE);
//				double stock = Double.parseDouble(dosing.get(ICoffeeService.DosingType.COFFEE_TYPE_STOCK));
                int boxID = dosing.getInteger(ICoffeeService.DosingType.COFFEE_TYPE_BOX_ID);

                LogUtil.vendor("dosingID: " + id + ", dosingName: " + title + ", boxID: " + boxID);

                CoffeeDosingInfo info = new CoffeeDosingInfo();
                info.setId(id);
                info.setName(title);
                info.setBoxID(boxID);
				result.addDosing(info);
			}
		} 

		postToUI(result.toRemote());
	}
}
