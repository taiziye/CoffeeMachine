package com.netease.vendor.service.core;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.netease.vendor.net.client.NetworkEnums;
import com.netease.vendor.util.log.LogUtil;

import android.content.Context;

public class NetworkKeeper {
	private Timer keepaliveTimer; // 检查服务器心跳的定时器, 同时也是后台保活的定时器
	private Timer reconnectTimer; // 自动重连定时器

	private AtomicInteger reconnectCounter = new AtomicInteger(); // 重连计数
    private final int RECONNECT_TIMEOUT_THRESTHOD = 163;

    /** connectivity watcher */
	ConnectivityWatcher mConnectivityWatcher;

	VendorCore yiliaoCore = VendorCore.sharedInstance();

	public NetworkKeeper(Context context) {
		mConnectivityWatcher = new ConnectivityWatcher(context,
				new ConnectivityWatcher.Callback() {

					@Override
					public void onNetworkEvent(NetworkEnums.Event event) {
						notifyEvent(event);
					}
				});
	}

	public void startWork() {
		mConnectivityWatcher.startup();
	}

	public void stopWork() {
		mConnectivityWatcher.shutdown();

		stopKeepaliveTimer();

		stopReconnect();
	}

	public boolean isReachable() {
		return mConnectivityWatcher.isAvailable();
	}

	public void startReconnect() {
		if (reconnectTimer != null)
			return;

		int random = new Random().nextInt(10);
		final int RECONNECT_PERIOD = (10 + random) * 1000;
		reconnectCounter.set(0);
		reconnectTimer = new Timer();
		TimerTask reconnectTask = new TimerTask() {

			@Override
			public void run() {
				if (shouldReconnect(reconnectCounter.incrementAndGet()) ) {
                    LogUtil.vendor("[DEBUG]shouldReconnect is true" );
                    if(reconnectCounter.get() >= RECONNECT_TIMEOUT_THRESTHOD){
                        yiliaoCore.requestReboot();
                        return;
                    }
					// 如果网络没有恢复或者已经连上，就不重连了
					if (!mConnectivityWatcher.isAvailable()) {
						LogUtil.vendor("network is not available");
						return;
					}
					if (yiliaoCore.isConnected()) {
						LogUtil.vendor("we has connected to the server, reconnect not needed");
						return;
					}
					yiliaoCore.logout();
					yiliaoCore.login(true);
					LogUtil.vendor("relogin because of reconnect timer");
				}
			}
		};
		reconnectTimer.schedule(reconnectTask, (5 + random / 2) * 1000,
				RECONNECT_PERIOD);
	}
	
	private boolean shouldReconnect(int count) {
        LogUtil.vendor("[DEBUG] shouldReconnect count = " + count);

        int maxPeriod = 8;
		
		if (count <= 0) {
			return false;
		}

        if(count == 1){
            return true;
        }

        return count % maxPeriod == maxPeriod - 1;
	}

	public void stopReconnect() {
		if (reconnectTimer != null) {
			reconnectTimer.cancel();
			reconnectTimer = null;
		}
	}

	private void stopKeepaliveTimer() {
		if (keepaliveTimer != null) {
			keepaliveTimer.cancel();
			keepaliveTimer = null;
		}
	}

	private void notifyEvent(NetworkEnums.Event event) {
		yiliaoCore.onNetworkEvent(event);
	}
}
