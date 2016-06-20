package com.netease.vendor.util;

import com.alibaba.fastjson.JSONArray;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.util.log.LogUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SharePrefConfig {

	private static SharePrefConfig instance;
	
	private SharedPreferences settings;

	private Editor editor;
	
	public static final String PrefsFileName = "VendorPref";
	
	private static final String DOSING_INIT = "is_dosing_init";
	private static final String DOSING_SETTING = "setting_dosing_";

    private static final String APP_DOWNLOAD_URL_TIMESTAMP = "app_download_time";
    private static final String APP_DOWNLOAD_URL_ANDROID = "app_download_android";
    private static final String APP_DOWNLOAD_URL_IOS = "app_download_ios";

	private static final String MACHINE_WASH_TIME = "wash_time";

	private static final String WELCOME_ADV_IMGS = "adv_imgs";
	private static final String WELCOME_ADV_UPDATE_TIME = "adv_update_time";
	
	private SharePrefConfig(Context context) {
		int __sdkLevel = Build.VERSION.SDK_INT;
		settings = context.getSharedPreferences(PrefsFileName, (__sdkLevel > 8) 
				? 4 : Context.MODE_WORLD_READABLE);
		editor = settings.edit();
	}

	public static SharePrefConfig getInstance() {
		if (instance == null) {
			instance = new SharePrefConfig(MyApplication.Instance());
		}
		return instance;
	}

	public SharedPreferences getSharedPreferences() {
		return settings;
	}

	public Editor getEditor() {
		return editor;
	}

	public void clearAllData() {
		editor.clear();
		editor.commit();
	}
	
	public boolean isDosingInit() {
		return settings.getBoolean(DOSING_INIT, false);
	}

	public void setDosingInit(boolean isInit) {
		editor.putBoolean(DOSING_INIT, isInit);
		editor.commit();
	}
	
	public void setDosingValue(String value, int dosingID) {
		editor.putString(DOSING_SETTING + dosingID, value);
		editor.commit();
	}
	
	public double getDosingValue(int dosingID) {
		String dosingValue = settings.getString(DOSING_SETTING + dosingID, "0");
		double ret = 0;
		try{
			ret = Double.parseDouble(dosingValue);
		}catch(Exception e){
			e.printStackTrace();
		}

		return ret;
	}

    public void setAppDownloadUrlAndroid(String urlAndroid) {
        editor.putString(APP_DOWNLOAD_URL_ANDROID, urlAndroid);
        editor.commit();
    }

    public String getAppDownloadUrlAndroid() {
        String downloadUrl = settings.getString(APP_DOWNLOAD_URL_ANDROID , "");
        return downloadUrl;
    }

    public void setAppDownloadUrlIos(String urlIos) {
        editor.putString(APP_DOWNLOAD_URL_IOS, urlIos);
        editor.commit();
    }

    public String getAppDownloadUrlIos() {
        String downloadUrl = settings.getString(APP_DOWNLOAD_URL_IOS , "");
        return downloadUrl;
    }

    public void setAppDownloadTime(long timestamp){
        editor.putLong(APP_DOWNLOAD_URL_TIMESTAMP, timestamp);
        editor.commit();
    }

    public long getAppDownloadUrlTime(){
        long timestamp = settings.getLong(APP_DOWNLOAD_URL_TIMESTAMP, 0);
        return timestamp;
    }

	public void setWashTime(Set<String> set){
		editor.putStringSet(MACHINE_WASH_TIME, set);
		editor.commit();
	}

	public Set<String> getWashTime(){
		Set<String> timeSet = settings.getStringSet(MACHINE_WASH_TIME, null);
		return timeSet;
	}

	public void setAdvImgs(String advImgUrls){
		editor.putString(WELCOME_ADV_IMGS, advImgUrls);
		editor.commit();

		setAdvUpdateTime(TimeUtil.getNow_millisecond());
	}

	public List<String> getAdvImgs(){
		List<String> advPicUrlsList = new ArrayList<String>();
		try{
			String advImgUrls = settings.getString(WELCOME_ADV_IMGS , "");

			if(TextUtils.isEmpty(advImgUrls)){
				return null;
			}

			JSONArray array = JSONArray.parseArray(advImgUrls);
			if(array != null){
				int size = array.size();
				for(int i = 0; i < size; i++){
					String picURL = array.getString(i);
					advPicUrlsList.add(picURL);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			LogUtil.e("DEBUG", "get local history error");
		}


		return advPicUrlsList;
	}

	public void setAdvUpdateTime(long time){
		editor.putLong(WELCOME_ADV_UPDATE_TIME, time);
		editor.commit();
	}

	public long getAdvUpdateTime(){
		long updateTime = settings.getLong(WELCOME_ADV_UPDATE_TIME, 0);
		return updateTime;
	}
}
