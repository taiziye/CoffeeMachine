package com.netease.vendor.action;

import com.netease.vendor.activity.LoginActivity;
import com.netease.vendor.application.GlobalCached;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.common.action.TAction;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.StatusChangeNotify;
import com.netease.vendor.service.domain.Ancestor;
import com.netease.vendor.util.log.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class SysAction extends TAction {

	@Override
	public void execute(Remote remote) {
		send(remote);
	}

	@Override
	public void receive(Remote remote) {
		notifyAll(remote);
		
		if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
			onStatusChange(remote);
		}else if(remote.getAction() == ITranCode.ACT_SYS_REBOOT){
            LogUtil.vendor("[SAILACTION] DO REBOOT COFFEE MACHINE");

            if(!MyApplication.Instance().isMakingCoffee()){
                String cmd = "su -c reboot";
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	}

	@Override
	public int getKey() {
		return ITranCode.ACT_SYS;
	}

	private boolean onStatusChange(Remote remote) {
		StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
		if (notify.getStatus() == ITranCode.STATUS_KICKOUT) {
			
			Intent intent = new Intent();
			intent.setClass(MyApplication.Instance().getApplicationContext(), 
					LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("status", notify.getStatus());
			intent.putExtra("vendor", GlobalCached.activeVendor);
			MyApplication.Instance().getApplicationContext().startActivity(intent);
			
			// 清空全局缓存
			GlobalCached.clear();

			return false;
		}
		
		return true;
	}
}
