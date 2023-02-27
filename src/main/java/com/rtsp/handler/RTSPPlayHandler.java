package com.rtsp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTSPPlayHandler extends RTSPBaseHandler {
    private final static Logger logger = LoggerFactory.getLogger(RTSPPlayHandler.class);

    /**
     * 处理客户端发送播放请求
     */
    @Override
    public String handlerRTSP() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRtspOk());
        sb.append("CSeq: ");
        sb.append(getCseq() + getrN());
        sb.append("Range: npt=0.000-");
        sb.append(getrN());
        sb.append("Session: " + getSessionId());
        sb.append(getrN());
        sb.append(getrN());
        logger.info(getrN() + getrN() + "*****handlerPlay:" + sb.toString());
        return sb.toString();
    }
}
