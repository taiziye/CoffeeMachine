package com.netease.vendor.service.protocol.response;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.IAuthService;
import com.netease.vendor.service.protocol.pack.Unpack;

@ResponseID(service = ServiceID.SVID_LITE_AUTH, command = { IAuthService.CommandId.CID_KICKOUT
		+ "" })
public class UserKickedResponse extends Response {

    @Override
    public Unpack unpackBody(Unpack unpack) throws Exception {
        return null;
    }

}
