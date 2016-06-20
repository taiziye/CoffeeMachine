package com.netease.vendor.service.protocol.response;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.IAuthService;


@ResponseID(service = ServiceID.SVID_LITE_AUTH, command = { IAuthService.CommandId.CID_REQ_CODE
		+ "" })
public class ReqCodeResponse extends SingleResponse {

}
