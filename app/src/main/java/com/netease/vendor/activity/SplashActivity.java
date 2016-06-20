package com.netease.vendor.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.netease.vendor.R;
import com.netease.vendor.application.GlobalCached;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.service.Remote;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.U;

public class SplashActivity extends TActivity {

	boolean isNeedLogin = true;
	private Context mContext;
	private final int SPLASH_DISPLAY_LENGTH = 3000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_page);
		mContext = this;

		// Request Bind
		requestBind();		
	}

	@Override
	public void onReceive(Remote remote) {
	}

	@Override
	protected void handleBound() {
		startMachine();
	}
	
	private void startMachine(){
		startVendor();
		new Handler().postDelayed(new Runnable() { 
            
			@Override 
            public void run() { 
            	if (isNeedLogin) {
            		showLoginPage();
        		} else {
                    boolean isInit = SharePrefConfig.getInstance().isDosingInit();
                    if(isInit){
                        showWelcomePage();
                    }else{
                        showStorageConfigPage();
                    }
        		}
            } 
        }, SPLASH_DISPLAY_LENGTH);
	}
	
	private void startVendor() {
		String vendorNum = U.getMyVendorNum();
		if(!TextUtils.isEmpty(vendorNum)){
			String isLogined = U.queryAppSet(U.KEY_USER_IS_LOGINED, vendorNum);
			if(isLogined.equals("true")){
				GlobalCached.activeVendor = vendorNum ;
				isNeedLogin = false;
			}else{
				isNeedLogin = true;
			}
		}else{
			isNeedLogin = true;
		}
	}
	
	private void showLoginPage(){
		Intent intent = new Intent(mContext, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
        this.finish();
	}
	
	private void showWelcomePage(){
		Intent intent = new Intent(mContext, WelcomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
	}

    private void showStorageConfigPage(){
        Intent intent = new Intent(mContext, MaterialConfigActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
    }
}
