package com.netease.vendor.action;

import com.netease.vendor.application.MyApplication;
import com.netease.vendor.common.action.TAction;
import com.netease.vendor.domain.CoffeeIndentStatus;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.GetAdvPicsResult;
import com.netease.vendor.service.bean.result.GetDiscountResult;
import com.netease.vendor.service.bean.result.RollbackResult;
import com.netease.vendor.service.bean.result.VerifyCoffeeResult;
import com.netease.vendor.util.SharePrefConfig;

public class CoffeeAction extends TAction {

	@Override
	public void execute(Remote remote) {
		send(remote);
	}

	@Override
	public void receive(Remote remote) {
		if (remote.getAction() == ITranCode.ACT_COFFEE_INDENT_TIME_OUT) {
			String coffeeIndent = remote.getBody();
			CoffeeIndentStatus oldStatus = MyApplication.Instance().getIndentStatus(coffeeIndent);
			if(oldStatus != null && oldStatus.getStatus() == CoffeeIndentStatus.INDENT_STATUS_REQUESTING){
				oldStatus.setStatus(CoffeeIndentStatus.INDENT_STATUS_TIMEOUT);
			}
		}else if(remote.getAction() == ITranCode.ACT_COFFEE_INDENT_ROLLBACK){
			RollbackResult result = GeneralActionResult.parseObject(remote.getBody());
			if(result != null ){
				String coffeeIndent = result.getCoffeeIndent();
				MyApplication.Instance().removeIndentStatus(coffeeIndent);
			}
		}else if(remote.getAction() == ITranCode.ACT_COFFEE_VERIFY_COFFEE){
			VerifyCoffeeResult result = GeneralActionResult.parseObject(remote.getBody());
			if(result != null){
				String coffeeIndent = result.getCoffeeIndent();
				MyApplication.Instance().removeIndentStatus(coffeeIndent);
			}
		}else if(remote.getAction() == ITranCode.ACT_COFFEE_GET_DISCOUNT){
			GetDiscountResult result = GeneralActionResult.parseObject(remote.getBody());
			if(result != null && result.getResCode() == 200){
				MyApplication.Instance().setDiscountInfo(result);
			}
		}else if(remote.getAction() == ITranCode.ACT_COFFEE_GET_ADV_PICS){
			GetAdvPicsResult result = GeneralActionResult.parseObject(remote.getBody());
			if(result != null && result.getResCode() == 200){
				SharePrefConfig.getInstance().setAdvImgs(result.getAdvImgUrls());
			}
		}
		
		notifyAll(remote);
	}

	@Override
	public int getKey() {
		return ITranCode.ACT_COFFEE;
	}
}
