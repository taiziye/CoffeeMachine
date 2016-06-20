package com.netease.vendor.net.client;

import com.netease.vendor.application.AppConfig;
import com.netease.vendor.util.log.LogUtil;

import android.text.TextUtils;

public class ServerAddressFetcher {

	public static class IPAddress {
		public String ip;
		public int port;
	}

	private long mTime;
	private static final long PERION_TIME = 60 * 60 * 1000;
	public static final String TAG = "ServerAddressFetcher";

	private IPAddress mLinkAddr;

	private static ServerAddressFetcher mInstance = new ServerAddressFetcher();

	private ServerAddressFetcher() {
		mLinkAddr = new IPAddress();
	}

	public static ServerAddressFetcher sharedInstance() {
		return mInstance;
	}

	private boolean isAddressInvalid(IPAddress address) {
		if (address == null || TextUtils.isEmpty(address.ip))
			return true;
		else
			return false;
	}

	public synchronized IPAddress getLinkAddress() {
		if (isAddressInvalid(mLinkAddr)) {
			updateAddress();
		} else {
			if (System.currentTimeMillis() - mTime >= ServerAddressFetcher.PERION_TIME) {
				updateAddress();
			}
		}

		LogUtil.d(TAG, "Link:" + mLinkAddr.ip + ":" + mLinkAddr.port);

		return mLinkAddr;
	}

	public synchronized void resetLinkAddress() {
		mLinkAddr = null;
	}

	private void updateAddress() {
		mLinkAddr = new IPAddress();

        if(AppConfig.BUILD_SERVER == AppConfig.Build.REL ){
//          mLinkAddr.ip = "121.41.45.225";      // ALI YUN
			mLinkAddr.ip = "120.26.219.169";
        }else if(AppConfig.BUILD_SERVER == AppConfig.Build.PRE_REL){
			mLinkAddr.ip = "121.40.226.93";
        }else{
            mLinkAddr.ip = "10.240.154.48";
        }
//      mLinkAddr.port = 4442;
		mLinkAddr.port = 4441;

		mTime = System.currentTimeMillis();
	}
}
