package com.rtsp.param;

import com.rtsp.handler.RTCPSocketHandler;
import com.rtsp.handler.RTPSocketHandler;
import com.rtsp.udp.UDPSocket;
import com.rtsp.util.ChannelUtils;
import com.rtsp.util.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IoSession {

    private final static Logger logger = LoggerFactory.getLogger(IoSession.class);

    private String uuid;

    public boolean rtp_tcp = false;  //RTPTCP标识

    /**
     * RTP/TCP网络连接
     */
    private Channel channel;
    private int rtpChannelId;  //RTP/TCP通信渠道标识
    private int rtcpChannelId;  //RTCP/TCP通信渠道标识

    /**
     * RTP/UDP网络连接
     */
    public int rtpClientPort;  //RTP/UDP客户端端口号
    public int rtcpClientPort;  //RTP/UDP客户端端口号
    public int rtpServerPort;  //RTP/UDP客户端端口号
    public int rtcpServerPort;  //RTP/UDP客户端端口号
    public UDPSocket rtpUdpSocket; //RTP/UDP服务端Socket
    public UDPSocket rtcpUdpSocket;//RTCP/UDP服务端Socket

    /**
     * 用户请求信息
     */
    private User user;

    /**
     * 视频指令信息
     */
    private VideoCmd videoCmd;

    /**
     * ip地址
     */
    private String ipAddr;

    private boolean reconnected;

    private long startTime; //连接开始时间

    private long lastActiveTime; //最后活跃时间戳

    public IoSession(Channel channel) {
        this.channel = channel;
        this.ipAddr = ChannelUtils.getIp(channel);
    }


    /**
     * 初始化UDPSocket
     */
    public void initUDPSocket() {
        RTPSocketHandler rtpSocketHandler = new RTPSocketHandler();
        rtpUdpSocket = new UDPSocket();
        rtpUdpSocket.setProtocolHandler(rtpSocketHandler);
        if (!rtpUdpSocket.open(rtpServerPort))
            System.out.println("启动RTP Socket服务失败");
        else
            System.out.println("启动RTP Socket服务成功");

        RTCPSocketHandler rtcpSocketHandler = new RTCPSocketHandler();
        rtcpUdpSocket = new UDPSocket();
        rtcpUdpSocket.setProtocolHandler(rtcpSocketHandler);
        if (!rtcpUdpSocket.open(rtcpServerPort))
            System.out.println("启动RTCP Socket服务失败");
        else
            System.out.println("启动RTCP Socket服务成功");
    }

    /**
     * TCP模式
     * 向客户端发送消息
     *
     * @param buf
     */
    public void sendPacket(ByteBuf buf) {
        if (buf == null) {
            return;
        }
        if(isClose()){
            close("system");
        }
        if (channel != null) {
            channel.writeAndFlush(buf);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public boolean isReconnected() {
        return reconnected;
    }

    public void setReconnected(boolean reconnected) {
        this.reconnected = reconnected;
    }

    public User getUser() {
        return user;
    }

    public VideoCmd getVideoCmd() {
        return videoCmd;
    }

    public void setVideoCmd(VideoCmd videoCmd) {
        this.videoCmd = videoCmd;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public int getRtpClientPort() {
        return rtpClientPort;
    }

    public void setRtpClientPort(int rtpClientPort) {
        this.rtpClientPort = rtpClientPort;
    }

    public int getRtcpClientPort() {
        return rtcpClientPort;
    }

    public void setRtcpClientPort(int rtcpClientPort) {
        this.rtcpClientPort = rtcpClientPort;
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

    public boolean isRtp_tcp() {
        return rtp_tcp;
    }

    public void setRtp_tcp(boolean rtp_tcp) {
        this.rtp_tcp = rtp_tcp;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isClose() {
        if (channel == null) {
            return true;
        }
        return !channel.isActive() ||
                !channel.isOpen();
    }

    /**
     * 关闭session
     *
     * @param reason
     */
    public void close(String reason) {
        try {
            if (this.channel == null) {
                return;
            }
            if (channel.isOpen()) {
                channel.close();
                logger.info("close session[{}], reason is {}", getUuid(), reason);
            } else {
                logger.info("session[{}] already close, reason is {}", getUuid(), reason);
            }
            if(this.videoCmd.getAction().equals(Constant.PREVIEW) ){
                stopPreview();
            }
            if(this.videoCmd.getAction().equals(Constant.PLAYBACK) ){
                stopPlayback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止预览
     */
    public void stopPreview(){
    }

    /**
     * 停止回放
     */
    public void stopPlayback(){
    }

}
