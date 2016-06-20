package com.netease.vendor.net.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.netease.vendor.service.protocol.pack.Pack;
import com.netease.vendor.service.protocol.pack.PackagePacker;
import com.netease.vendor.service.protocol.request.Request;



public class PacketEncoder extends OneToOneEncoder {
	
	PackagePacker packer;
	
	public PacketEncoder() {
	}
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		// TODO Auto-generated method stub
		if(msg instanceof Request)
		{
			Request req = (Request)msg;
			
			Pack p = packer.packRequest(req);
			return p;
		}
		else
		{
			return msg;
		}
	}

	public void setPacker(PackagePacker packer) {
		this.packer = packer;
	}
	
	public void reset() {
		
	}
}
