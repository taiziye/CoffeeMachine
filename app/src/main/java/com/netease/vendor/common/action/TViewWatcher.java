package com.netease.vendor.common.action;

import java.util.ArrayList;
import java.util.List;

import com.netease.vendor.service.IRemoteConn;
import com.netease.vendor.service.IRemoteConnCall;

import com.netease.vendor.application.MyApplication;
import com.netease.vendor.inter.IServiceBindListener;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.VendorService;
import com.netease.vendor.util.log.LogUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * 视图层观察器
 * @author zhousq
 *
 */
public class TViewWatcher {

	public final static String SERVICENAME = "com.netease.vendor.service.VendorService";
	/**
	 * 采用饿汉式设计，因为是一定要启动的，防止多进程启动调用的同步行为
	 */
	private static TViewWatcher watcher = new TViewWatcher();
	
	private List<IServiceBindListener>  serviceBindListenerList;

	public static TViewWatcher newInstance(){
		return watcher;
	}
	
	private List<Handler> handlers;
	private List<Remote> pendings;

	private Context mComtext; //应用于application进程，因为applicaiton进程优先启动
	
	private TViewWatcher(){
		LogUtil.vendor("TViewWatcher()");
		handlers = new ArrayList<Handler>();
		mComtext = MyApplication.Instance().getApplicationContext();
		serviceBindListenerList = new ArrayList<IServiceBindListener>();
		bindService();
	}
	
	public void addServiceBinderListener(IServiceBindListener serviceBindListener){
		serviceBindListenerList.add(serviceBindListener);
	}

	public void removeServiceBinderListener(IServiceBindListener serviceBindListener){
		serviceBindListenerList.remove(serviceBindListener);
	}
	
	public boolean bindService() { 
		Intent intent = new Intent(SERVICENAME);  
		if( mComtext.bindService (intent, serviceConn, Context.BIND_AUTO_CREATE)){
			return true;
		}else{
			LogUtil.e("bind", "bindService failed, restart it");
			VendorService.stopService(MyApplication.Instance());
			// give it a chance to retry
			return mComtext.bindService (intent, serviceConn, Context.BIND_AUTO_CREATE);
		}
	}
	
	public void unBindService(){
		mComtext.unbindService(serviceConn);  // 取消绑定  
	}
	
	private IRemoteConn  serviceCalls = null;
	private ServiceConnection serviceConn = new ServiceConnection() {   

		public void onServiceConnected(ComponentName name, IBinder service) {
			LogUtil.vendor("TViewWatcher ->onServiceConnected()");
			serviceCalls = IRemoteConn.Stub.asInterface(service); 
			LogUtil.vendor("TViewWatcher ->serviceCalls=" + serviceCalls);
			try {
				if(serviceCalls != null){
					serviceCalls.registerCallback(mCallback);
					VendorService.isBind = true;
					for(IServiceBindListener serviceBindListener : serviceBindListenerList){
						serviceBindListener.onBindSuccess();
					}
					
					onPending();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void onServiceDisconnected(ComponentName name) {
			LogUtil.vendor("已经不在连了,哎");
			VendorService.isBind = false;
			for(IServiceBindListener serviceBindListener : serviceBindListenerList){
				serviceBindListener.onBindFailed(name.getClassName());
			}
			
			// 重启service
			VendorService.restartService(MyApplication.Instance());
		}   
	};

	private IRemoteConnCall mCallback = new IRemoteConnCall.Stub() {
		/**
		 * 这里将进行远程消息分发和处理
		 */
		public void receive(Remote remote) {
			
			TActionFactory factory = TActionFactory.newInstance();
			IAction aciton  = factory.getAction(remote.getWhat());
			if(aciton != null)
				aciton.receive(remote);
		  
		}
	}; 
	
	/**
	 * 视图接收数据,发送对象为实体对象，不再序列化
	 * 这里涉及到回调问题，如果仅仅需要注册的handle都发送一遍，这样有利于统一处理
	 * 
	 */
	public void notifyAll(Remote remote){
		for (Handler handler : handlers) {
			if(handler != null){
				Message msg = new Message();
				msg.what =  remote.getWhat();
				msg.obj = remote;
				handler.sendMessage(msg);
			}
		}
	}
 
	/**
	 * 视图提交数据,启动分发作用,线程中运行
	 * @param event
	 */
	public void executeBackground(final Remote remote){		 
		TActionFactory factory = TActionFactory.newInstance();
		final IAction aciton  = factory.getAction(remote.getWhat());
		Thread exeThread = new Thread(
			   new Runnable() {
				public void run() {
					aciton.execute(remote);
				}
			}
		
		);
		exeThread.start();
	}
   
	/**
	 * 视图提交数据,启动分发作用
	 * @param event
	 */
	public void execute( Remote remote){
		 TActionFactory factory = TActionFactory.newInstance();
		 final IAction aciton  = factory.getAction(remote.getWhat());
	  	 aciton.execute(remote);
	}
 
	/**
	 * 提交数据到远程服务
	 * @param event
	 * @return
	 */
	public boolean send(Remote remote) {
		boolean sent = false;

		if (serviceCalls != null) {
			try {
				serviceCalls.send(remote);
				sent = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			pending(remote);
		}
		
		return sent;
	}
	
	private void pending(Remote remote) {
		synchronized (this) {
			if (pendings == null) {
				pendings = new ArrayList<Remote>();
			}
			
			pendings.add(remote);
			
			LogUtil.vendor("pending remote: what " + remote.getWhat());
		}
	}
	
	private void onPending() {
		List<Remote> pendings = null;
		
		synchronized (this) {
			pendings = this.pendings;
			this.pendings = null;
		}
		
		if (pendings != null) {
			LogUtil.vendor("handle pending remote: count " + pendings.size());
			
			for (Remote pending : pendings) {
				send(pending);
			}
			
			pendings = null;
		}
	}

	public void clearView(){
		handlers.clear();
	}
	
	public void bindView(Handler handler){  
		handlers.add(handler);
	}
	
	public void unBindView(Handler handler){
		handlers.remove(handler);
	}
}
