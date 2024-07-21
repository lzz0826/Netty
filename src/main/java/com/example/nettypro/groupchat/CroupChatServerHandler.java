package com.example.nettypro.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * channelRegistered: 當 Channel 已經註冊到它的 EventLoop 並且能夠處理 I/O 時被調用。
 * channelUnregistered: 當 Channel 從它的 EventLoop 註銷並且無法處理任何 I/O 時被調用。
 * channelActive: 當 Channel 處於活動狀態（已連接）時被調用。
 * channelInactive: 當 Channel 處於非活動狀態（已斷開連接）時被調用。
 * channelRead0: 當從 Channel 讀取到消息時被調用。注意，這個方法會自動釋放接收的消息。
 * channelReadComplete: 當 Channel 上的一批數據被讀取完成並且準備就緒時被調用。
 * exceptionCaught: 當處理過程中發生異常時被調用。
 * userEventTriggered: 當 ChannelInboundHandler.fireUserEventTriggered() 方法被調用時，且有事件觸發時被調用。
 * channelWritabilityChanged: 當 Channel 的可寫狀態發生改變時被調用（例如：可寫狀態從可寫變為不可寫時）。
 *
 */
public class CroupChatServerHandler extends SimpleChannelInboundHandler<String> {


//    private static final Map<String, DefaultChannelGroup> groupMap = new HashMap<>(); //群
//    private static final Map<String,Channel> channelMap = new HashMap<>(); //私

    //定義一個channel 組 管理所有的 channel
    //GlobalEventExecutor.INSTANCE 全局事件執行器 是一個單例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //連接建立 一旦連接 第一個被執行
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //將該客戶加入聊天的信息推送給其它在線的客戶端
        //該方法會將 channelGroup 中所有的 channel 遍歷，併發送消息
        channelGroup.writeAndFlush("[ 客戶端 ]" + channel.remoteAddress() + " 上線了 " + sdf.format(new
                java.util.Date())+ "\n");
        //將當前 channel 加入到 channelGroup
        channelGroup.add(ctx.channel());

    }

    //表示 channel 處於就緒狀態, 提示上線
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 上線了"+ "\n");
        System.out.println("channelGroup size=" + channelGroup.size());
    }



    //表示 channel 處於不活動狀態, 提示離線了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.remove(channel);
        channelGroup.writeAndFlush("[ 客戶端 ]" + channel.remoteAddress() + " 離線了 " + sdf.format(new
                java.util.Date())+ "\n");
        System.out.println("channelGroup size=" + channelGroup.size());

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Channel channel = ctx.channel();

        for (Channel c : channelGroup) {
            if(c != channel){
                c.writeAndFlush("[ 客戶端 ]" + channel.remoteAddress() + " 發送了消息：" + msg + "\n");
            }else {
                c.writeAndFlush("[ 自己 ]發送了消息：" + msg + "\n");
            }
        }
    }



    /**
     * 處理異常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}


