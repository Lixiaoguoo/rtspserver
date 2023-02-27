package com.rtsp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTSPDescribeHandler extends RTSPBaseHandler {
    private final static Logger logger = LoggerFactory.getLogger(RTSPDescribeHandler.class);
    private int contentLength = 146;
    private String contentTyep = "application/sdp";
    private String m = "video 0 RTP/AVP 96";
    private String rtpmap = "96 H264/90000";
    private String framerate = "25";

    /**
     * sdp媒体描述信息
     */
    @Override
    public String handlerRTSP() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRtspOk());
        sb.append("CSeq: ");
        sb.append(getCseq() + getrN());
        sb.append("Content-length: " + contentLength + "" + getrN());
        sb.append("Content-type: " + contentTyep + "" + getrN() + getrN());
        sb.append("v=0" + getrN());
        sb.append("o=- " + getSessionId() + " 1 in IP4 192.168.0.100" + getrN());
        sb.append("t=0 0" + getrN());
        sb.append("a=contol:*" + getrN());
        sb.append("m=" + m + "" + getrN());
        sb.append("a=rtpmap:" + rtpmap + "" + getrN());
        sb.append("a=framerate:" + framerate + "" + getrN());
        sb.append("a=control:track0" + getrN());
        sb.append(getrN());
        logger.info(getrN() + getrN() + "*****handlerDescribe:" + sb.toString());
        return sb.toString();
    }
}
