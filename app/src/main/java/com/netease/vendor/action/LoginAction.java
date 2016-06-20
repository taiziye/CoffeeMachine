package com.netease.vendor.action;

import com.netease.vendor.common.action.TAction;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.protocol.ResponseCode;

public class LoginAction extends TAction {

	@Override
	public void execute(Remote remote) {

	}

	@Override
	public void receive(Remote remote) {
		if (remote.getAction() == ITranCode.ACT_USER_LOGIN) {
			
			GeneralActionResult result = GeneralActionResult.parseObject(remote.getBody());
			if (result.getResCode() == ResponseCode.RES_SUCCESS) {

				//LogUtil.vendor("登陆成功! MyApplication.synchronizeDataFlag = "
				//		+ MyApplication.synchronizeDataFlag);
				//MyApplication.synchronizeDataFlag++;
				//if (MyApplication.synchronizeDataFlag == 2) {
				//	remote = new Remote();
				//	remote.setWhat(ITranCode.ACT_CONTACTS);
				//	remote.setAction(ITranCode.ACT_CONTACTS_GETINFO);
				//	TViewWatcher.newInstance().send(remote);
				//}
			}
		}
	}

	@Override
	public int getKey() {
		return ITranCode.ACT_USER;
	}

}
