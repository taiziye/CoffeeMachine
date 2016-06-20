package com.netease.vendor.service.protocol.filter;

import com.netease.vendor.service.protocol.response.Response;

public class PacketTypeFilter implements PacketFilter {

	private Class<? extends Response> packetClass;

	public PacketTypeFilter(Class<? extends Response> packetClass) {
		this.packetClass = packetClass;
	}

	@Override
	public boolean accept(Response response) {
		return packetClass == response.getClass();
	}

}
