package com.netease.vendor.service.action;

import com.netease.vendor.service.Remote;

/**
 * @author zhousq
 *
 */
public interface IAction {
	
    /**
     * 客户端调用会从Factory分发到这里
     * @param remote
     */
	public void execute(Remote remote);
	public int getWhat();

}
