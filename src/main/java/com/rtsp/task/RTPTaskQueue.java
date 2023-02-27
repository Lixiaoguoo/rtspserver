package com.rtsp.task;

import com.rtsp.util.Constant;
import com.rtsp.util.H264File;


public class RTPTaskQueue extends TaskQueue {

    @Override
    public boolean svc(Object task) {
        RTPPackage rtpPackage = (RTPPackage) task;
        H264File videoFile = new H264File();
        videoFile.open(Constant.voidePath);
        //不停推送视频流
        while (true) {
            byte[] nalu = videoFile.readNalu();
            if (nalu == null) {
                videoFile.readPos = 0;
                break;
            }
            try {
                rtpPackage.naluToRtp(nalu);
            } catch (Exception e) {
                e.printStackTrace();
            }
            rtpPackage.head.timestamp += rtpPackage.timeFrameRate / rtpPackage.fps;
//            try {
//                Thread.sleep(sleepMillis);
//            } catch (Exception e) {
//            }
        }
        return true;
    }

    /**
     * 未执行
     */
    @Override
    public boolean unExecute(Object task) {
        return true;
    }

    @Override
    public void threadUninit() {

    }
}
