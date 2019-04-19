package com.fta.netty.httpserver;

import com.fta.netty.websocketserver.Main;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

public class HttpServer {

    static Logger logger = Logger.getLogger(HttpServer.class);

    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        int port = 8989;
        new HttpServer(port).start();
    }

    private void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new MyChannelInitializer());
//            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
//            serverBootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
//            serverBootstrap.childOption(ChannelOption.SO_TIMEOUT, 30000);
//            System.out.println("服务端开启等待连接");
            logInfo("服务端开启等待连接，连接端口：" + port);
            Channel ch = serverBootstrap.bind(port).sync().channel();
//            Channel ch = serverBootstrap.bind(8899).sync().channel();
            ch.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void logInfo(String msg) {
        logger.info(msg);
    }
}
