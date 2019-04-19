package com.fta.netty.websocketserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;

/**
 * 入口
 */
public class Main {
    static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        new Main().start();
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new MyWebsocktChannelHandler());
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
//            serverBootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
//            serverBootstrap.childOption(ChannelOption.SO_TIMEOUT, 30000);
//            System.out.println("服务端开启等待连接");
            logger.info("服务端开启等待连接");
            Channel ch = serverBootstrap.bind(8889).sync().channel();
//            Channel ch = serverBootstrap.bind(8899).sync().channel();
            ch.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void pushMessage() {
        ChannelGroup channelGroup = NettyConfig.channelGroup;
        logInfo("channelGroup size is " + channelGroup.size());
        if (channelGroup.size() > 0) {
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame("I am server,I am sending Message");
            channelGroup.writeAndFlush(textWebSocketFrame);
        }
    }

    public static void logInfo(String msg) {
        logger.info(msg);
    }
}
