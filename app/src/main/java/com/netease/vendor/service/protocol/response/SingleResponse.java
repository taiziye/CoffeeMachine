package com.netease.vendor.service.protocol.response;

import com.netease.vendor.service.protocol.pack.Unpack;

public class SingleResponse extends Response {

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		unpack(unpack);
		return null;
	}

}
