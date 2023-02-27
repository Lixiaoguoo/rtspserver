package com.rtsp.util;

public class Constant {

    public static final String voidePath = System.getProperty("user.dir") + "/video.h264";
    public static int INCR = 2;  //端口自增步长
    public static int RTP_PORT = 56400;
    public static int RTCP_PORT = 56401;
    public static final String PREVIEW = "preview";
    public static final String PLAYBACK = "playback";
    public enum Status {
        OPTIONS, DESCRIBE, SETUP, PLAY, PAUSE, TEARDOWN
    }

    /**
     * UDP多会话对应的服务端Socket端口
     */
    public static void portIncr(){
        RTP_PORT += INCR;
        RTCP_PORT += INCR;
    }
}
