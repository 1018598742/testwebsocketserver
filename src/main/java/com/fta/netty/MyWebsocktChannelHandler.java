package com.fta.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import sun.net.www.http.KeepAliveStream;

import java.util.concurrent.TimeUnit;

/**
 * 初始化连接时候的各个组件
 */
public class MyWebsocktChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast("http-codec", new HttpServerCodec());
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        channel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());

        channel.pipeline().addLast("idleStateHandler", new IdleStateHandler(25, 15, 10));
        channel.pipeline().addLast("myHandler", new MyHandler());

        channel.pipeline().addLast("handler", new MyWebsocketHandler());

    }
}
