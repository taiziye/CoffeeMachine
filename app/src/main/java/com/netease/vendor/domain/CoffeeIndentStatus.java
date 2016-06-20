package com.netease.vendor.domain;

public class CoffeeIndentStatus {
	
	public static final int INDENT_STATUS_REQUESTING = 1;
	public static final int INDENT_STATUS_TIMEOUT = 2;

	private String coffeeIndent;
	private long timestamp;
	private int status;
	
	private boolean retry;
	
	public String getCoffeeIndent() {
		return coffeeIndent;
	}

	public void setCoffeeIndent(String coffeeIndent) {
		this.coffeeIndent = coffeeIndent;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isRetry() {
		return retry;
	}

	public void setRetry(boolean retry) {
		this.retry = retry;
	}
}
