package com.rtsp.handler;

import com.rtsp.util.Hex;

import java.net.SocketAddress;

public class TCPSocketHandler implements ProtocolHandler {
    public boolean handlePack(byte[] byPack, int nPackLen, SocketAddress sa) {
        System.out.println("TCPSocketHandler:" + Hex.encodeHexStr(byPack, 0, nPackLen, false));
        return true;
    }
}
