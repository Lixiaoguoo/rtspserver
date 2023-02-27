package com.rtsp.task;

import com.rtsp.param.IoSession;
import com.rtsp.param.SessionManage;
import com.rtsp.util.Hex;
import io.netty.buffer.ByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import java.util.concurrent.ConcurrentHashMap;


/**
 * RTP封包和发送
 */
public class RTPPackage {
    private final static Logger logger = LoggerFactory.getLogger(RTPPackage.class);
    private byte[] sendbuf = new byte[1500];
    private int packageSize = 900;
    private int singlePackageSize = 100;
    public int timeFrameRate = 90000;  //时钟频率
    public int fps = 25;  //帧率
    public byte rtpFlag = 0x24; //RTP/TCP数据标识符"$"
    public String address = "";  //RTP/UDP数据发送IP地址
    public int rtpChannelId = 0;  //RTP/TCP通信渠道标识
    public int rtpClientPort = -1;  //RTP/UDP客户端端口号
    public boolean RTP_TCP = false;  //RTPTCP标识 控制RTP包发送模式
    public String uuid;  //会话标识
    public byte naluType = 0;

    public RTPSubHeader subHead = new RTPSubHeader();
    public RTPHeader head = new RTPHeader();
    public IoSession ioSession = null;

    public int readPos = 0;
    //是否有分割头 00 00 00 01 或 00 00 01  没有直接返回fileData
    public boolean havHead;

    /**
     * 读取nalu数据
     *
     * @param streamData
     * @param dataLen
     * @return
     */
    public boolean readNalu(byte[] streamData, int maxPos, int dataLen, int split) {
        int bufLen = maxPos;
        if (readPos >= bufLen)
            return false;
        int pos = readPos;
        int naluPos = -1;
        while (pos < (bufLen - 2)) {
            if (naluPos == -1) {
                if (streamData[pos] == 0x00 && streamData[pos + 1] == 0x00) {
                    if (streamData[pos + 2] == 0x01) {
                        naluPos = pos + 3;
                        //有分割头
                        havHead = true;
                        //有分割头,但分割头不是第一位,分割头前面的数据当成尾包发出去
                        if(pos > readPos) {
                            int naluLen = pos - readPos;
                            naluToRtp(streamData, readPos, naluLen, 2);
                        }
                        pos += 3;
                        continue;
                    } else if (streamData[pos + 2] == 0x00 && (pos + 3) < bufLen && streamData[pos + 3] == 0x01) {
                        naluPos = pos + 4;
                        //有分割头
                        havHead = true;
                        //有分割头,但分割头不是第一位,分割头前面的数据当成尾包发出去
                        if(pos > readPos) {
                            int naluLen = pos - readPos;
                            naluToRtp(streamData, readPos, naluLen, 2);
                        }
                        pos += 4;
                        continue;
                    }
                }
                pos++;
                continue;
            }
            //找到下一个nalu开头
            if (streamData[pos] == 0x00 && streamData[pos + 1] == 0x00) {
                if (streamData[pos + 2] == 0x01) {
                    int naluLen = pos - naluPos;
                    readPos = pos;
                    //有分割头
                    havHead = true;
                    naluToRtp(streamData, naluPos, naluLen, 1);
                    return true;
                } else if (streamData[pos + 2] == 0x00 && (pos + 3) < bufLen && streamData[pos + 3] == 0x01) {
                    int naluLen = pos - naluPos;
                    readPos = pos;
                    //有分割头
                    havHead = true;
                    naluToRtp(streamData, naluPos, naluLen, 1);
                    return true;
                }
            }
            pos++;
            continue;
        }
        if (naluPos == -1 && havHead) {
            readPos = 0;
            return false;
        }
        if (naluPos == -1 && !havHead) {
            naluToRtp(streamData, readPos, dataLen, split);
            readPos = 0;
            return false;
        }
        //最后一个
        int naluLen = bufLen - naluPos;
        readPos = bufLen;
        try {
            naluToRtp(streamData, naluPos, naluLen, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 本地h264文件nalu rtp封包
     *
     * @throws Exception
     */
    public void naluToRtp(byte[] nalu) throws Exception {
        int naluLen = nalu.length;
        byte naluType = nalu[0];
        if (naluLen <= packageSize) {
            head.m = 1;
            int sendBufLen = 0;
            sendBufLen = head.encode(sendbuf, sendBufLen);
            System.arraycopy(nalu, 0, sendbuf, sendBufLen, nalu.length);
            sendBufLen += nalu.length;
            sendRTP(sendbuf, sendBufLen);
            head.seq++;
        } else if (naluLen > packageSize) {
            int pktNum = naluLen / packageSize;
            int remainPktSize = naluLen % packageSize;
            int i, pos = 0;
            for (i = 0; i < pktNum; i++) {
                head.m = 0;
                subHead.nri = (byte) ((naluType & 0x60) >> 5);
                subHead.type = 28;
                subHead.s = 0;
                subHead.e = 0;
                subHead.r = 0;
                subHead.type2 = (byte) (naluType & 0x1F);
                int offset = 0;
                if (i == 0) {
                    offset = 1;
                    subHead.s = 1;
                } else if (remainPktSize == 0 && i == pktNum - 1) {
                    head.m = 1;
                    subHead.e = 1;
                }
                int sendBufLen = 0;
                sendBufLen = head.encode(sendbuf, sendBufLen);
                sendBufLen = subHead.encode(sendbuf, sendBufLen);
                System.arraycopy(nalu, pos + offset, sendbuf, sendBufLen, packageSize - offset);
                sendBufLen += (packageSize - offset);
                sendRTP(sendbuf, sendBufLen);
                head.seq++;
                pos += packageSize;
            }
            if (remainPktSize > 0) {
                head.m = 1;
                subHead.nri = (byte) ((naluType & 0x60) >> 5);
                subHead.type = 28;
                subHead.s = 0;
                subHead.e = 1;
                subHead.r = 0;
                subHead.type2 = (byte) (naluType & 0x1F);
                int sendBufLen = 0;
                sendBufLen = head.encode(sendbuf, sendBufLen);
                sendBufLen = subHead.encode(sendbuf, sendBufLen);
                System.arraycopy(nalu, pos, sendbuf, sendBufLen, remainPktSize);
                sendBufLen += remainPktSize;
                sendRTP(sendbuf, sendBufLen);
                head.seq++;
            }
        }
    }

    /**
     * 终端数据RTP封包
     *
     * @param nalu
     * @param split
     * @throws Exception
     */
    public void naluToRtp(byte[] nalu, int redPos, int dataLen, int split){
        //logger.debug(" naluToRtp.naluData:" + split + " > " + dataLen + " > " + Hex.encodeHexStr(nalu, redPos, dataLen, false));
        try {
            int naluLen = dataLen;
            if (split == 1) {
                naluType = nalu[redPos];
            }
            if (split == 1 && naluLen <= singlePackageSize) { //单包
                head.m = 1;
                int sendBufLen = 0;
                sendBufLen = head.encode(sendbuf, sendBufLen);
                System.arraycopy(nalu, redPos, sendbuf, sendBufLen, naluLen);
                sendBufLen += naluLen;
                sendRTP(sendbuf, sendBufLen);
                head.seq++;
                return;
            }
            head.m = 0;
            subHead.nri = (byte) ((naluType & 0x60) >> 5);
            subHead.type = 28;
            subHead.s = 0;
            subHead.e = 0;
            subHead.r = 0;
            subHead.type2 = (byte) (naluType & 0x1F);
            int offset = 0;
            if (split == 1) {
                offset = 1;
                subHead.s = 1;
            }
            if (split == 2) {  //尾包
                head.m = 1;
                subHead.e = 1;
            }
            int sendBufLen = 0;
            sendBufLen = head.encode(sendbuf, sendBufLen);
            sendBufLen = subHead.encode(sendbuf, sendBufLen);
            System.arraycopy(nalu, redPos + offset, sendbuf, sendBufLen, naluLen - offset);
            sendBufLen += (naluLen - offset);
            sendRTP(sendbuf, sendBufLen);
            head.seq++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     */
    public void sendRTP(byte[] bufData, int sendBufLen) {
        if (RTP_TCP) {
            sendToClientByTcp(bufData, sendBufLen);
        } else {
            sendToClientUdp(bufData, sendBufLen);
        }
    }

    /**
     * tcp模式发送数据包
     *
     * @param bufData
     * @param sendBufLen
     */
    public void sendToClientByTcp(byte[] bufData, int sendBufLen) {
        logger.debug(" TCP RTP:" + Hex.encodeHexStr(bufData, 0, sendBufLen, false));
        byte[] byteTcp = new byte[sendBufLen + 4];
        byteTcp[0] = rtpFlag;
        byteTcp[1] = (byte) rtpChannelId;
        byteTcp[2] = (byte) ((sendBufLen >> 8) & 0x0F);
        byteTcp[3] = (byte) (sendBufLen & 0xFF);
        System.arraycopy(bufData, 0, byteTcp, 4, sendBufLen);

        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(byteTcp.length);
        buf.writeBytes(byteTcp);
        if (ioSession == null) {
           ConcurrentHashMap concurrentHashMap = SessionManage.getInstance().getUserSessionMap();
           ioSession = (IoSession) concurrentHashMap.get(uuid);
        }
        ioSession.sendPacket(buf);
    }

    /**
     * udp模式发送数据包
     *
     * @param bufData
     * @param sendBufLen
     */
    public void sendToClientUdp(byte[] bufData, int sendBufLen) {
        IoSession ioSession = (IoSession) SessionManage.getInstance().getUserSessionMap().get(uuid);
        if (ioSession != null) {
            ioSession.rtpUdpSocket.sendData(bufData, sendBufLen, ioSession.getIpAddr(), ioSession.getRtpClientPort());
        }
    }

    public class RTPHeader {
        public byte v = 2;
        public byte p;
        public byte x;
        public byte cc;
        public byte m;
        public byte pt = 96;
        public int seq;
        public int timestamp;
        public int ssrc = 1348738776;

        public int encode(byte[] buf, int writePos) {
            buf[writePos++] = (byte) ((v << 6) | (p << 5) | (x << 4) | cc);
            buf[writePos++] = (byte) ((m << 7) | pt);

            buf[writePos++] = (byte) ((seq >> 8) & 0xFF);
            buf[writePos++] = (byte) (seq & 0xFF);

            buf[writePos++] = (byte) ((timestamp >> 24) & 0xFF);
            buf[writePos++] = (byte) ((timestamp >> 16) & 0xFF);
            buf[writePos++] = (byte) ((timestamp >> 8) & 0xFF);
            buf[writePos++] = (byte) (timestamp & 0xFF);

            buf[writePos++] = (byte) ((ssrc >> 24) & 0xFF);
            buf[writePos++] = (byte) ((ssrc >> 16) & 0xFF);
            buf[writePos++] = (byte) ((ssrc >> 8) & 0xFF);
            buf[writePos++] = (byte) (ssrc & 0xFF);
            return writePos;
        }
    }

    class RTPSubHeader {
        public byte f;
        public byte nri;
        public byte type;
        public byte s;
        public byte e;
        public byte r;
        public byte type2;

        public int encode(byte[] buf, int writePos) {
            buf[writePos++] = (byte) ((f << 7) | (nri << 5) | type);
            buf[writePos++] = (byte) ((s << 7) | (e << 6) | (r << 5) | type2);
            return writePos;
        }
    }
}
