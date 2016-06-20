package com.netease.vendor.common.adapter;

import com.netease.vendor.service.domain.Ancestor;

public abstract class TListItem extends Ancestor{
 
	private int viewType;
	private long viewId;
	private boolean enabled;
	
	public TListItem()
	{
		this.enabled = true;
		this.viewId = 1L;
		this.viewType = 0;
	}
	
	public int getViewType() {
		return viewType;
	}
	
	public void setViewType(int viewType) {
		this.viewType = viewType;
	}
 
	public long getViewId() {
		return viewId;
	}
	
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getHintId() {
		return null;
	}
}
