package com.netease.vendor.service.action;

import android.text.TextUtils;

import com.netease.vendor.net.http.MD5;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.LoginInfo;
import com.netease.vendor.service.bean.action.LoginRequestInfo;
import com.netease.vendor.service.bean.action.VerifyPasswordInfo;
import com.netease.vendor.service.bean.result.VerifyPasswordResult;
import com.netease.vendor.service.domain.Ancestor;
import com.netease.vendor.service.protocol.request.KeepAliveRequest;
import com.netease.vendor.service.protocol.request.LoginRequest;
import com.netease.vendor.util.log.LogUtil;

public class UserAction extends TAction {

	@Override
	public void execute(Remote remote) {
		switch (remote.getAction()) {
		case ITranCode.ACT_USER_LOGIN_REQUEST:
			requestLogin(remote);
			break;
		case ITranCode.ACT_USER_LOGIN:
			login(remote);
			break;
		case ITranCode.ACT_USER_LOGOUT:
			logout(remote);
			break;
		case ITranCode.ACT_USER_KEEPALIVE:
			requestKeepalive(remote);
			break;
        case ITranCode.ACT_USER_VERIFY_PWD:
            verifyPwd(remote);
            break;
		default:
			LogUtil.vendor("don't recognized user action: " + remote.getAction());
			break;
		}
	}

	@Override
	public int getWhat() {
		return ITranCode.ACT_USER;
	}
	
	private void requestLogin(Remote remote){
		if (!core.isLogined()) {
			LoginRequestInfo info = Ancestor.parseObject(remote.getBody());
			core.setMyVendorNum(info.getUid());
			if (!core.isConnected()) {
				connectServer();
			}
			core.login(info.getPassword());
		}
	}
	
	private void login(Remote remote) {
		if (!core.isLogined()) {
			try {
				LoginInfo loginInfo = Ancestor.parseObject(remote.getBody());
				
				core.setMyVendorNum(loginInfo.getUid());
				if (!core.isConnected()) {
					connectServer();
				}
				
				if (core.isConnected()) {
					LoginRequest lr = generateLoginRequest(loginInfo);
					core.sendRequestToServer(lr);
					
					//check here
					core.setLastVendorNum(loginInfo.getUid());
					core.setLastVendorPwd(loginInfo.getPassword());
				} else {
					LogUtil.vendor("not login because of connection failed");
				}
			} catch (Exception e) { // TODO: 通知上层，登录失败
			}
		}
	}	

	private void logout(Remote remote) {
		core.logout();
		
		core.setMyVendorNum("");
		core.setLastVendorNum("");
        core.setLastVendorName("");
		core.setLastVendorPwd("");
		
		post2UI(remote);
	}

	private void connectServer() {
		// 先判断网络有没有连上
		int retryCount = 0;
		while (!core.isConnected() && retryCount < 30) {
			if (retryCount % 30 == 0) { // 每隔30s才重新连一次  //check here
				core.connect();
			}
			++retryCount;
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				LogUtil.vendor("connect thread was interrupt");
			}
		}

		if (!core.isConnected()) { // 还是没有连上
			// 通知上层
			LogUtil.vendor("server is not available, please check the network");

			// 网络断掉
			core.disconnect();
		}
	}

	private LoginRequest generateLoginRequest(LoginInfo loginInfo) {
		LoginRequest lr = new LoginRequest(loginInfo.getUid());		
		lr.setPassword(loginInfo.getPassword());
		
		return lr;
	}

	private void requestKeepalive(Remote remote) {
		KeepAliveRequest request = new KeepAliveRequest(
				core.getMyVendorNum());
		core.sendRequestToServer(request);
	}

    private void verifyPwd(Remote remote){
        VerifyPasswordInfo info = Ancestor.parseObject(remote.getBody());
        boolean ret = false;
        if(info != null ){
            String password = info.getPassword();
            String vendorPwd = core.getLastVendorPwd();
            if(!TextUtils.isEmpty(vendorPwd) && vendorPwd.equals(MD5.md5(password))){
                ret = true;
            }
        }

        VerifyPasswordResult result = new VerifyPasswordResult();
        result.setCorrect(ret);
        result.setType(info.getType());

        core.notifyListener(result.toRemote());
    }
}
