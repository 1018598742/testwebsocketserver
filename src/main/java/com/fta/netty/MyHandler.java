package com.fta.netty;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class MyHandler extends ChannelHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt instanceof IdleStateEvent) {

            IdleStateEvent event = (IdleStateEvent) evt;
            String channelId = ctx.channel().id().asShortText();

            if (event.state().equals(IdleState.READER_IDLE)) {
                //未进行读操作
//                System.out.println(LogUtils.printMsg("READER_IDLE"));
                Main.logInfo("READER_IDLE " + channelId);
                // 超时关闭channel
                ctx.close();

            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
//                System.out.println(LogUtils.printMsg("WRITER_IDLE"));
                Main.logInfo("WRITER_IDLE " + channelId);
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                //未进行读写
//                System.out.println(LogUtils.printMsg("ALL_IDLE"));
                Main.logInfo("ALL_IDLE " + channelId);

                // 发送心跳消息
                Date date = new Date();
                String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(format);
                ctx.channel().writeAndFlush(textWebSocketFrame);
            }

        }
    }
}
