package com.netease.vendor.service;

import com.netease.vendor.service.IRemoteConn;
import com.netease.vendor.service.IRemoteConnCall;
import com.netease.vendor.service.core.VendorCore;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

/**
 * 远程服务通讯类，负责响应来自客户层的调用
 * @author zhousq
 */
public class TRemoteConn extends IRemoteConn.Stub {

	private RemoteCallbackList<IRemoteConnCall> mCallbacks;
	private Context mContext;
	private VendorCore yiliaoCore = VendorCore.sharedInstance();

	public TRemoteConn(Context mContext) {
		this.mContext = mContext;
	}

	public TRemoteConn(Context mContext,
			RemoteCallbackList<IRemoteConnCall> mCallbacks) {
		this.mContext = mContext;
		this.mCallbacks = mCallbacks;
	}

	/**
	 * 来自客户端的业务提交， type 交易码 body 交易体，可以用json转换 class 容器类 类型真正意义上的实体处理
	 */
	// 在这里进行客户端调用发送
	public void send(final Remote remote) throws RemoteException {
		yiliaoCore.sendPacket(remote);
	}

	public void registerCallback(IRemoteConnCall remoteConn)
			throws RemoteException {
		if (remoteConn != null)
			mCallbacks.register(remoteConn);
	}

	public void unregisterCallback(IRemoteConnCall remoteConn)
			throws RemoteException {
		if (remoteConn != null)
			mCallbacks.unregister(remoteConn);

	}

	public boolean isTaskRunning() throws RemoteException {

		return false;
	}

	public void stopRunningTask() throws RemoteException {

	}

}
