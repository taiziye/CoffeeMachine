package com.netease.vendor.service.protocol.response;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ILinkService;
import com.netease.vendor.service.protocol.pack.Unpack;


@ResponseID(service = ServiceID.SVID_LITE_LINK, command = { ILinkService.CommandId.CID_HEARTBEAT
		+ "" })
public class KeepAliveResponse extends Response {

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		return null;
	}

}
