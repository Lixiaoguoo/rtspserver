package com.rtsp.handler;

import com.rtsp.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTSPSetupHandler extends RTSPBaseHandler {
    private final static Logger logger = LoggerFactory.getLogger(RTSPSetupHandler.class);

    /**
     * 客户端与服务器建立会话,并确定传输模式
     */
    @Override
    public String handlerRTSP() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRtspOk());
        sb.append("CSeq: ");
        sb.append(getCseq() + getrN());
        if (isRTP_TCP()) {
            sb.append("Transport: " + getTransport());
        } else {
            sb.append("Transport: " + getTransport() + ";server_port=" + String.format("%d-%d", getRtpServerPort(), getRtcpServerPort()));
        }
        sb.append(getrN());
        sb.append("Session: " + getSessionId());
        sb.append(getrN());
        sb.append(getrN());
        logger.info(getrN() + getrN() + "*****handlerSetup:" + sb.toString());
        return sb.toString();
    }
}
