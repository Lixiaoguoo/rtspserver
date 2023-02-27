package com.rtsp.handler;

import com.rtsp.RTSPServer;
import com.rtsp.param.IoSession;
import com.rtsp.param.SessionManage;
import com.rtsp.param.VideoCmd;
import com.rtsp.task.RTPPackage;
import com.rtsp.util.ChannelUtils;
import com.rtsp.util.Constant;
import com.rtsp.util.MapUtils;
import com.rtsp.util.TruncateUrl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;


public class RTSPHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(RTSPHandler.class);
    public boolean RTP_TCP = false;  //RTPTCP标识
    public String reqAction;//请求动作 preview 预览，playback 回放

    public IoSession reqIoSession;
    public RTSPBaseHandler rtspBaseHandler;
    private ScheduledExecutorService tp = null;

    /**
     * 当客户端连接服务器完成就会触发该方法
     * @param ctx
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)

        IoSession ioSession = new IoSession(ctx.channel());
        if (!ChannelUtils.addChannelSession(ctx.channel(), ioSession)) {
            ctx.channel().close();
            logger.error("Duplicate session,IP=[{}]", ChannelUtils.getIp(ctx.channel()));
        }
        String uuid = UUID.randomUUID().toString();
        ioSession.setUuid(uuid);
        //存储会话
        ConcurrentHashMap<String, IoSession> concurrentHashMap = SessionManage.getInstance().getUserSessionMap();
        concurrentHashMap.put(uuid, ioSession);
        reqIoSession = ioSession;
        logger.info("channelActive session,IP=[{}],ioSession:{}", ChannelUtils.getIp(ctx.channel()), ioSession);
    }

    /**
     * 处理异常, 一般是需要关闭通道
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("exceptionCaught: {}", ">>>>>>>Connection reset by peer, "+cause.getMessage());
        try {
            stopVideoStream(reqAction);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }

    }

    /**
     * 读取客户端发送的数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof DefaultHttpRequest) {
            DefaultHttpRequest req = (DefaultHttpRequest) msg;
            HttpMethod method = req.method();
            String methodName = method.name();
            logger.info("request>>>>>>>> " + req.toString());
            /**创建RTSP Handler实例**/
            boolean sub = subHandler(methodName);
            if (!sub) {
                return;
            }

            /**解析URL请求参数**/
            decodeURLParam(ctx, req, methodName);

            /**解析RTSP协议属性**/
            boolean t = decodeRTSPParam(ctx, req);
            if (t) {
                /**RTSP协议消息的处理*/
                String returnData = rtspBaseHandler.handlerRTSP();
                /**发送返回信息**/
                send(ctx, returnData);
                /**PLAY触发RTP数据任务**/
                if (methodName.equalsIgnoreCase(Constant.Status.PLAY.toString())) {
                    //新线程去读取视频文件流
                    addRTPTask(ctx.channel());
                }
                if(methodName.equalsIgnoreCase(Constant.Status.TEARDOWN.toString())){
                    stopVideoStream(reqAction);
                }
            }
        } else if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            if (content.content().isReadable()) {
                /** 此时, 才表示HttpContent是有内容的, 否则,它是空的, 不需要处理 */
                logger.info(">>>>>HttpContent no handler");
            }
        } else {
            logger.info(">>>>>>no handler");
        }
    }

    /**
     * 多态实例
     */
    public boolean subHandler(String methodName) {
        if (methodName.equalsIgnoreCase(Constant.Status.OPTIONS.toString())) {
            rtspBaseHandler = new RTSPOptionHandler();
        } else if (methodName.equalsIgnoreCase(Constant.Status.DESCRIBE.toString())) {
            rtspBaseHandler = new RTSPDescribeHandler();
        } else if (methodName.equalsIgnoreCase(Constant.Status.SETUP.toString())) {
            rtspBaseHandler = new RTSPSetupHandler();
        } else if (methodName.equalsIgnoreCase(Constant.Status.PLAY.toString())) {
            rtspBaseHandler = new RTSPPlayHandler();
        } else if (methodName.equalsIgnoreCase(Constant.Status.PAUSE.toString())) {
            rtspBaseHandler = new RTSPPauseHandler();
        } else if (methodName.equalsIgnoreCase(Constant.Status.TEARDOWN.toString())) {
            rtspBaseHandler = new RTSPTeardownHandler();
        } else {
            return false;
        }
        return true;
    }

    /**
     * 解析URL参数
     * 获取用户名密码校验
     */
    public void decodeURLParam(ChannelHandlerContext ctx, DefaultHttpRequest req, String methodName) {
        String reqUrl = req.uri();
        if (!methodName.equalsIgnoreCase(Constant.Status.OPTIONS.toString()) || reqUrl == null || reqUrl.contains("/bad-request")) {
            return;
        }
        Map<String, Object> beanMap = TruncateUrl.URLRequest(reqUrl);
        VideoCmd videoCmd = MapUtils.map2bean(beanMap, VideoCmd.class);
        if (Objects.nonNull(videoCmd)) {
            videoCmd.setAction(TruncateUrl.getActionName(reqUrl));
            reqAction = videoCmd.getAction();
            IoSession ioSession = ChannelUtils.getSessionBy(ctx.channel());
            if (Objects.nonNull(ioSession)) {
                ioSession.setVideoCmd(videoCmd);
            }
        }
    }

    /**
     * RTSP属性解析
     * RTP传输模式，C/S端口，通道ID等
     */
    public boolean decodeRTSPParam(ChannelHandlerContext ctx, DefaultHttpRequest req) {
        HttpHeaders headers = req.headers();
        rtspBaseHandler.setAddress(req.uri());
        if (rtspBaseHandler.getAddress() == null || rtspBaseHandler.getAddress().contains("/bad-request")) {
            return false;
        }
        try {
            rtspBaseHandler.setCseq(Integer.parseInt(headers.get("CSeq")));
            if (headers.get("Transport") != null) {
                IoSession ioSession = ChannelUtils.getSessionBy(ctx.channel());
                if (ioSession != null) {
                    rtspBaseHandler.setTransport(headers.get("transport"));
                    if (rtspBaseHandler.getTransport().contains("RTP/AVP/TCP")) {
                        RTP_TCP = true;
                        rtspBaseHandler.setRTP_TCP(true);
                        rtspBaseHandler.getTCPInterleaved();
                        ioSession.setRtp_tcp(true);
                        ioSession.setRtpChannelId(rtspBaseHandler.getRtpChannelId());
                        ioSession.setRtcpChannelId(rtspBaseHandler.getRtcpChannelId());
                    } else {
                        rtspBaseHandler.getUDPClientPort();
                        //得到新的服务端端口
                        Constant.portIncr();
                        rtspBaseHandler.setRtpServerPort(Constant.RTP_PORT);
                        rtspBaseHandler.setRtcpServerPort(Constant.RTCP_PORT);
                        ioSession.setRtpClientPort(rtspBaseHandler.getRtpClientPort());
                        ioSession.setRtcpClientPort(rtspBaseHandler.getRtcpClientPort());
                        ioSession.setRtpServerPort(rtspBaseHandler.getRtpServerPort());
                        ioSession.setRtcpServerPort(rtspBaseHandler.getRtcpServerPort());
                        ioSession.initUDPSocket();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 触发RTP数据任务
     */
    public void addRTPTask(Channel channel) {
        IoSession ioSession = ChannelUtils.getSessionBy(channel);
        if (ioSession != null) {
            //此处发送RTP数据
            RTPPackage RTPPackage = new RTPPackage();
            RTPPackage.RTP_TCP = ioSession.isRtp_tcp();
            RTPPackage.rtpClientPort = ioSession.getRtpClientPort();
            RTPPackage.rtpChannelId = ioSession.getRtpChannelId();
            RTPPackage.address = ioSession.getIpAddr();
            RTPPackage.uuid = ioSession.getUuid();
            RTSPServer.getInstance().getRTPTaskQueue().AddTask(RTPPackage);
        }

    }

    /**
     * 发送返回数据
     */
    public void send(ChannelHandlerContext ctx, String data) {
        try {
            byte[] bufData = data.getBytes("UTF-8");
            ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.buffer(bufData.length);
            buf.writeBytes(bufData);
            ctx.channel().writeAndFlush(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止视频流
     * 预览流,回放流
     */
    public void stopVideoStream(String action){
        if(tp != null) {
            tp.shutdown();
        }
    }
}

