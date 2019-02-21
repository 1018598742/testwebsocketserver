package com.fta.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

public class MyWebsocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker webSocketServerHandshaker;

    private static final String WEBSOCKET_URL = "wss://localhost:8889/websocket";
//    private TestTimer testTimer;

    /**
     * 处理请求的核心方法
     *
     * @param channelHandlerContext
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
//        Main.logInfo("messageReceived  处理握手请求的业务");
        //处理握手请求的业务
        if (msg instanceof FullHttpMessage) {
//            Main.logInfo("messageReceived FullHttpMessage");
            handHttpRequest(channelHandlerContext, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {//处理websocket连接业务
//            Main.logInfo("messageReceived WebSocketFrame");
            handleWebsocketFrame(channelHandlerContext, (WebSocketFrame) msg);
        }

    }

    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //是否是关闭指令
        if (frame instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
//            System.out.println(LogUtils.printMsg("暂不支持二进制消息！"));
            Main.logInfo("暂不支持二进制消息！");
            throw new RuntimeException("【" + this.getClass().getName() + "】不支持消息");
        }

        //返回应答消息
        String requestMsg = ((TextWebSocketFrame) frame).text();
//        System.out.println(LogUtils.printMsg("服务端收到客户端的消息：" + requestMsg));
        Main.logInfo("服务端" + ctx.channel().id().asShortText() + "管道收到客户端的消息：" + requestMsg);
//        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(new Date().toString() + ctx.channel().id() + "===>>>" + requestMsg);
//        //发送给客户端，想每一个客户都发
//        NettyConfig.channelGroup.writeAndFlush(textWebSocketFrame);


    }

    /**
     * 处理握手请求的业务
     *
     * @param ctx
     * @param fullHttpRequest
     */
    private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        if (!fullHttpRequest.getDecoderResult().isSuccess() || !("websocket".equals(fullHttpRequest.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, fullHttpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory(WEBSOCKET_URL, null, false);
        webSocketServerHandshaker = webSocketServerHandshakerFactory.newHandshaker(fullHttpRequest);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            webSocketServerHandshaker.handshake(ctx.channel(), fullHttpRequest);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, DefaultFullHttpResponse response) {
        if (response.getStatus().code() != 200) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(byteBuf);
            byteBuf.release();
        }

        ChannelFuture channelFuture = ctx.channel().writeAndFlush(response);
        if (response.getStatus().code() != 200) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 异常调用
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
//        super.exceptionCaught(ctx, cause);
    }

    /**
     * 创建连接时调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        boolean add = NettyConfig.channelGroup.add(ctx.channel());
//        System.out.println(LogUtils.printMsg("客户端与服务端连接开启：" + add + "=连接的ID=" + ctx.channel().id().asLongText()));
        Main.logInfo("客户端与服务端连接开启：" + add + "=连接的ID=" + ctx.channel().id().asLongText());
    }

    /**
     * 断开连接时调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println(LogUtils.printMsg("客户端与服务端连接关闭:ID=" + ctx.channel().id().asLongText()));
        boolean remove = NettyConfig.channelGroup.remove(ctx.channel());
        Main.logInfo("客户端与服务端连接关闭:" + remove + "ID=" + ctx.channel().id().asLongText());
    }

    /**
     * 接收数据结束后
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ChannelHandlerContext flush = ctx.flush();

//        super.channelReadComplete(ctx);
    }
}
