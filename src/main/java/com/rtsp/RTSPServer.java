package com.rtsp;

import com.rtsp.handler.RTSPHandler;
import com.rtsp.task.RTPTaskQueue;
import com.rtsp.util.PropsConfig;
import com.rtsp.util.ServerStartLock;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.rtsp.RtspDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;


public class RTSPServer implements WrapperListener {
    private final static Logger logger = LoggerFactory.getLogger(RTSPServer.class);
    public RTPTaskQueue rtpTaskQueue;
    protected static RTSPServer instance = new RTSPServer();

    public static RTSPServer getInstance() {
        return instance;
    }

    public RTPTaskQueue getRTPTaskQueue() {
        return rtpTaskQueue;
    }

    public static ServerStartLock serverStartLock;
    public static EventLoopGroup bossGroup = null; // (1)
    public static EventLoopGroup workerGroup = null;

    public  boolean init() {
        rtpTaskQueue = new RTPTaskQueue();
        rtpTaskQueue.start(3);
        return true;
    }

    public static void main(String[] args) {
        serverStartLock = new ServerStartLock();
        if (!serverStartLock.lock()) {
            logger.info("程序已启动");
            return;
        }
        RTSPServer.getInstance().init();
        startNetty();
    }


    /**
     * 启动netty注册监听服务
     */
    public static void startNetty() {
        bossGroup = new NioEventLoopGroup(); // (1)
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new RtspDecoder())   /** 添加netty自带的rtsp消息解析器 */
                                    .addLast(new RTSPHandler());  /** 上一步将消息解析完成之后, 再交给自定义的处理器 */
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            int port = Integer.parseInt(PropsConfig.getInstance().get("rtsp.port"));
            System.out.println("端口号："+port);
            ChannelFuture f = b.bind(port).sync(); // (7)
            logger.info("rtsp netty 服务启动成功");
        } catch (Exception ex) {
            logger.error("rtsp netty 服务启动失败, ", ex);
        }
    }

    @Override
    public Integer start(String[] strings) {

        /* 创建RMI注册表，启动RMI服务，并将远程对象注册到RMI注册表中*/
        startNetty();
        return null;
    }

    @Override
    public int stop(int i) {
        logger.info("rtsp netty 服务开始关闭");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        //移除文件锁
        serverStartLock.unlock();
        logger.info("rtsp netty 服务关闭成功");
        return 0;
    }

    @Override
    public void controlEvent(int i) {
        if (i == WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT && WrapperManager.isLaunchedAsService()) {
            System.out.println("WrapperSimpleApp: controlEvent(" + i + ") Ignored");
        } else {
            System.out.println("WrapperSimpleApp: controlEvent(" + i + ") Stopping");
            WrapperManager.stop(0);
        }
    }


}
