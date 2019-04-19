package com.fta.netty.httpserver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class MyChannelInitializer extends ChannelInitializer {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        HttpServer.logInfo("init channel");
        channel.pipeline().addLast("decoder", new HttpRequestDecoder());  // 1
        channel.pipeline().addLast("encoder", new HttpResponseEncoder());  // 2
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(512 * 1024)); // 3
        channel.pipeline().addLast("handler", new HttpHandler());        // 4
    }
}
