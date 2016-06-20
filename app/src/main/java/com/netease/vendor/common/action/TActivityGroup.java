package com.netease.vendor.common.action;

 


import com.netease.vendor.inter.IServiceBindListener;
import com.netease.vendor.service.Remote;
import com.netease.vendor.util.ToolUtil;

import android.app.Activity;
import android.app.ActivityGroup;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class TActivityGroup extends ActivityGroup implements IServiceBindListener{

 
	protected String uuid = ToolUtil.getUUID(); 
	/**
	 * UI逻辑层通知通道
	 */
	protected Handler handler = new Handler() {                

		public void handleMessage(Message message) {
			
			 Remote remote = (Remote)message.obj;
		     onReceive(remote);
			 
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		com.netease.vendor.util.log.LogUtil.vendor("TActivity onCreate()");
		TViewWatcher.newInstance().bindView(handler);
	 
	}
	public void onDestroy()
	{
		super.onDestroy();
		TViewWatcher.newInstance().unBindView(handler);
	}
	/**
	 * 页面提交工作，将输入场打包后进入具体的action处理
	 * @param remote
	 */
	public Remote execute(Remote remote)
	{
		TViewWatcher.newInstance().execute(remote);
		return remote;
	}
	
	public Remote executeBackground(Remote remote)
	{
		TViewWatcher.newInstance().executeBackground(remote);
		return remote;
	}
	/**
	 * 接收到消息,需要具体的页面来实现
	 * @param 
	 */
	public abstract void onReceive(Remote remote);
	
 
	public void onBindSuccess()
	{
		
	}
	
	public void onBindFailed(String errorMessage)
	{
		
	}
}
