package com.netease.vendor.service.protocol.response.coffee;

import com.netease.vendor.service.protocol.ServiceID;
import com.netease.vendor.service.protocol.enums.ICoffeeService;
import com.netease.vendor.service.protocol.pack.Unpack;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.ResponseID;

@ResponseID(service = ServiceID.SVID_LITE_COFFEE, command = { ICoffeeService.CommandId.UPDATE_STOCK
        + "" })
public class UpdateStockResponse extends Response {

    @Override
    public Unpack unpackBody(Unpack unpack) throws Exception {
        return null;
    }

}
