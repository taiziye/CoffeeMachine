package com.netease.vendor.service.protocol.marshal;

import com.netease.vendor.service.protocol.pack.Pack;
import com.netease.vendor.service.protocol.pack.Unpack;

public interface Marshallable {
	public abstract void marshal(Pack pack);

	public abstract void unmarshal(Unpack unpack);
}
