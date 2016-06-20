package com.netease.vendor.service.protocol.filter;

import com.netease.vendor.service.protocol.response.Response;

public interface PacketFilter {
	 public boolean accept(Response response);
}
