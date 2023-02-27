package com.rtsp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pl
 * @date 2020/2/20
 * @since jdk1.8
 */
public class RTPTCPHandler  extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(RTPTCPHandler.class);
    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("exceptionCaught: {}", ">>>>>>>Connection reset by peer");
        ctx.close();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

}
