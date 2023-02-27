package com.rtsp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTSPTeardownHandler extends RTSPBaseHandler {
    private final static Logger logger = LoggerFactory.getLogger(RTSPTeardownHandler.class);

    /**
     * 客户端发起关闭请求
     */
    @Override
    public String handlerRTSP() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRtspOk());
        sb.append("CSeq: ");
        sb.append(getCseq() + getrN());
        sb.append("Session: " + getSessionId());
        sb.append(getrN());
        sb.append("Connection: close");
        sb.append(getrN());
        sb.append(getrN());
        logger.info(getrN() + getrN() + "*****handlerTeardown:" + sb.toString());
        return sb.toString();
    }
}
