package com.rtsp.handler;

import com.rtsp.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTSPOptionHandler extends RTSPBaseHandler {
    private final static Logger logger = LoggerFactory.getLogger(RTSPOptionHandler.class);

    /**
     * rtsp的可用方法
     */
    @Override
    public String handlerRTSP() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRtspOk());
        sb.append("CSeq: ");
        sb.append(getCseq() + getrN());
        sb.append("Public: ");
        sb.append(Constant.Status.OPTIONS.toString() + ",");
        sb.append(Constant.Status.SETUP.toString() + ",");
        sb.append(Constant.Status.PLAY.toString() + ",");
        sb.append(Constant.Status.PAUSE.toString() + ",");
        sb.append(Constant.Status.TEARDOWN.toString());
        sb.append(getrN());
        sb.append(getrN());
        logger.info(getrN() + getrN() + "*****handlerOption:" + sb.toString());
        return sb.toString();
    }
}
