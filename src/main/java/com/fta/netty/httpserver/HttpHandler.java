package com.fta.netty.httpserver;

import com.fta.netty.websocketserver.Main;
import com.fta.netty.websocketserver.NettyConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // 1

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
//        HttpServer.logInfo("class:" + fullHttpRequest.getClass().getName());
        if (fullHttpRequest instanceof DefaultFullHttpRequest) {
            DefaultFullHttpRequest defaultFullHttpRequest = (DefaultFullHttpRequest) fullHttpRequest;
            String s = defaultFullHttpRequest.toString();
//            HttpServer.logInfo("request info is :" + s);
            if (defaultFullHttpRequest.getMethod().equals(HttpMethod.POST)) {
                HttpHeaders headers = fullHttpRequest.headers();
                String strContentType = headers.get("Content-Type").trim();
                Map<String, Object> mapReturnData = new HashMap<String, Object>();
                // 处理POST请求
                if (strContentType.contains("x-www-form-urlencoded")) {
                    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
                            new DefaultHttpDataFactory(false), fullHttpRequest);
                    List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();
                    for (InterfaceHttpData data : postData) {
                        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                            MemoryAttribute attribute = (MemoryAttribute) data;
                            mapReturnData.put(attribute.getName(),
                                    attribute.getValue());
                        }
                    }
                } else if (strContentType.contains("application/json")) {
                    // 解析json数据
                    ByteBuf content = fullHttpRequest.content();
                    byte[] reqContent = new byte[content.readableBytes()];
                    content.readBytes(reqContent);
                    String strContent = new String(reqContent, "UTF-8");
                    HttpServer.logInfo("接收到的消息" + strContent);
//                    JSONObject jsonParamRoot = JSONObject.parseObject(strContent);
//                    for (String key : jsonParamRoot.keySet()) {
//                        mapReturnData.put(key, jsonParamRoot.get(key));
//                    }
                } else {
                    FullHttpResponse response = new DefaultFullHttpResponse(
                            HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    ctx.writeAndFlush(response).addListener(
                            ChannelFutureListener.CLOSE);
                }

                HttpServer.logInfo("POST方式：" + mapReturnData.toString());
            }
        }
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer("I am httpserver".getBytes())); // 2

        HttpHeaders heads = response.headers();
        heads.add(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        heads.add(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes()); // 3
        heads.add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);

        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        HttpServer.logInfo("channelReadComplete");
        super.channelReadComplete(ctx);
        ctx.flush(); // 4
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        HttpServer.logInfo("exceptionCaught");
        if (null != cause) cause.printStackTrace();
        if (null != ctx) ctx.close();
    }


}
