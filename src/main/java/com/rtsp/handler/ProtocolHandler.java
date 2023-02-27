package com.rtsp.handler;

import java.net.SocketAddress;

/**
 * 协议处理接口
 * @author gzcheng
 *
 */
public interface ProtocolHandler {
	/**
	 * 协议包处理函数
	 */
	boolean handlePack(byte[] byPack, int nPackLen, SocketAddress sa);
}
