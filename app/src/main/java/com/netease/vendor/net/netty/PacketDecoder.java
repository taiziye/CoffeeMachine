package com.netease.vendor.net.netty;

import java.nio.ByteBuffer;

import org.jboss.netty.channel.ChannelHandlerContext;

import com.netease.vendor.service.protocol.LinkFrame;
import com.netease.vendor.service.protocol.PacketHeader;
import com.netease.vendor.service.protocol.pack.PackagePacker;
import com.netease.vendor.service.protocol.pack.PacketCompressor;
import com.netease.vendor.service.protocol.pack.Unpack;
import com.netease.vendor.util.HexDump;
import com.netease.vendor.util.log.LogUtil;


public class PacketDecoder extends AbstractPackDecoder {

	private PackagePacker unpacker;
	
	public PacketDecoder() {
	}

	public Object parsePacket(ChannelHandlerContext ctx, byte[] packet) throws Exception 
	{
		Unpack up = new Unpack(packet);

		PacketHeader header = new PacketHeader();
		up.popMarshallable(header);
		LinkFrame lf = header.toOldStyleHeader();

		LogUtil.core("received " + header);
		
		// 如果是被压缩的大包，解压
		if (lf.isCompressed()) {
			ByteBuffer uncompressed = PacketCompressor.uncompress(up);
			up = new Unpack(uncompressed);
		}
		NioResponse p = new NioResponse();
		p.header = lf;
		p.body = up;
		
		return p;

	}

	public byte[] decrypt(byte[] src) {
		Unpack up = new Unpack(src);
		up = unpacker.decrypt(up);
		return up.getBuffer().array();
	}
	
	public void setUnpacker(PackagePacker unpacker) {
		this.unpacker = unpacker;
	}
	
	public void reset() {
		packetSize = -1;
	}
}
