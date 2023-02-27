package com.rtsp.handler;

import com.rtsp.util.Hex;

import java.net.SocketAddress;

public class RTCPSocketHandler implements ProtocolHandler {
	@Override
	public boolean handlePack(byte[] byPack, int nPackLen, SocketAddress sa) {
		System.out.println("RTCPSocketHandler:"+ Hex.encodeHexStr(byPack, 0, nPackLen, false));
		return true;
	}
}
