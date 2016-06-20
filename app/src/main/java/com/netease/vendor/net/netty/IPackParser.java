package com.netease.vendor.net.netty;

import org.jboss.netty.channel.ChannelHandlerContext;

public interface IPackParser 
{
	public Object parsePacket(ChannelHandlerContext ctx, byte[] packet) throws Exception ;
	public abstract byte[] decrypt(byte[] src);
}
