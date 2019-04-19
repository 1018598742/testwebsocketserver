package com.fta.netty;

import com.fta.netty.websocketserver.NettyConfig;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TestTimer {

    private final Timer timer;

    private Channel mChannel;

    public static void main(String[] args) {
//        new TestTimer(30);
    }


    public TestTimer(int time, Channel channel) {
        this.mChannel = channel;
        timer = new Timer();
//        timer.schedule(new TimerTaskTest(),time * 1000);
        timer.scheduleAtFixedRate(new TimerTaskTest(), 0, time * 1000);
    }

    public void cancel() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public class TimerTaskTest extends TimerTask {

        @Override
        public void run() {
            Date date = new Date();
            String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            System.out.println(LogUtils.printMsg("这个通道 " + mChannel.id().asShortText()+" 发送了消息"));
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(format);
            //发送给客户端，想每一个客户都发
            int size = NettyConfig.channelGroup.size();
            System.out.println(LogUtils.printMsg("客户端个数：" + size));
//            NettyConfig.channelGroup.writeAndFlush(textWebSocketFrame);
            mChannel.writeAndFlush(textWebSocketFrame);

        }
    }
}
