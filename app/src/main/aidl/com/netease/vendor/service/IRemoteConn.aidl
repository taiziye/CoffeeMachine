package com.netease.vendor.service;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.IRemoteConnCall;
interface IRemoteConn {
    boolean isTaskRunning();   
    void stopRunningTask(); 
	void registerCallback(IRemoteConnCall remoteConn); 
	void unregisterCallback(IRemoteConnCall remoteConn); 
	void send(in Remote remote);

}