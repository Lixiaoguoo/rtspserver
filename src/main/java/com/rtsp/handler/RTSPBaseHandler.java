package com.rtsp.handler;

import com.rtsp.util.Constant;

public abstract class RTSPBaseHandler {

    private static final String R_N = "\r\n";
    private static final String RTSP_OK = "RTSP/1.0 200 OK" + R_N;
    private String address = "";
    private int cseq = -1;
    private static final String sessionId = "663348738776";
    private static final String server_port = String.format("%d-%d", Constant.RTP_PORT, Constant.RTCP_PORT);
    private String transport = "";
    private int rtpClientPort = 0;
    private int rtcpClientPort = 0;
    public int rtpServerPort;  //RTP/UDP客户端端口号
    public int rtcpServerPort;  //RTP/UDP客户端端口号
    private int rtpChannelId = 0;  //RTP TCP通道标识
    private int rtcpChannelId = 1; //RTP RTCP通道标识
    private boolean RTP_TCP = false;  //RTPTCP标识

    /**
     * 处理方法
     *
     * @return
     */
    public abstract String handlerRTSP();

    /**
     * TCP模式获取传输通道标识
     */
    public void getTCPInterleaved() {
        if (transport.contains(";")) {
            String[] str = transport.split(";");
            if (str.length >= 3) {
                String interl = str[2];
                String port = interl.split("=")[1];
                rtpChannelId = Integer.parseInt(port.split("-")[0]);
                rtcpChannelId = Integer.parseInt(port.split("-")[1]);
            }
        }
    }

    /**
     * UDP模式获取客户端RTP和RTCP端口
     */
    public void getUDPClientPort() {
        if (transport.contains(";")) {
            String[] str = transport.split(";");
            if (str.length >= 3) {
                String clientPort = str[2];
                String port = clientPort.split("=")[1];
                rtpClientPort = Integer.parseInt(port.split("-")[0]);
                rtcpClientPort = Integer.parseInt(port.split("-")[1]);
            }
        }
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCseq() {
        return cseq;
    }

    public void setCseq(int cseq) {
        this.cseq = cseq;
    }

    public int getRtpClientPort() {
        return rtpClientPort;
    }

    public void setRtpClientPort(int rtpClientPort) {
        this.rtpClientPort = rtpClientPort;
    }

    public int getRtcpClientPort() {
        return rtcpClientPort;
    }

    public int getRtpServerPort() {
        return rtpServerPort;
    }

    public void setRtpServerPort(int rtpServerPort) {
        this.rtpServerPort = rtpServerPort;
    }

    public int getRtcpServerPort() {
        return rtcpServerPort;
    }

    public void setRtcpServerPort(int rtcpServerPort) {
        this.rtcpServerPort = rtcpServerPort;
    }

    public void setRtcpClientPort(int rtcpClientPort) {
        this.rtcpClientPort = rtcpClientPort;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public int getRtpChannelId() {
        return rtpChannelId;
    }

    public void setRtpChannelId(int rtpChannelId) {
        this.rtpChannelId = rtpChannelId;
    }

    public int getRtcpChannelId() {
        return rtcpChannelId;
    }

    public void setRtcpChannelId(int rtcpChannelId) {
        this.rtcpChannelId = rtcpChannelId;
    }

    public static String getrN() {
        return R_N;
    }

    public static String getRtspOk() {
        return RTSP_OK;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public boolean isRTP_TCP() {
        return RTP_TCP;
    }

    public void setRTP_TCP(boolean RTP_TCP) {
        this.RTP_TCP = RTP_TCP;
    }
}
